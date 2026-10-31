package io.github.gbolahan507.giveaway.item

import io.github.gbolahan507.giveaway.auth.User
import io.github.gbolahan507.giveaway.auth.UserRepository
import io.github.gbolahan507.giveaway.common.ItemAlreadyClaimedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Note: deliberately NOT @Transactional. A rolled-back test transaction
 * would hide the setup data from the claiming threads, and each thread
 * commits its own transaction anyway. We clean up by hand instead.
 */
@SpringBootTest
class ClaimConcurrencySpec extends Specification {

    @Autowired ItemService itemService
    @Autowired ItemRepository itemRepository
    @Autowired UserRepository userRepository

    UUID itemId
    List<UUID> userIds = []

    def setup() {
        // unique emails so repeated runs don't collide on the unique constraint
        def suffix = UUID.randomUUID().toString().take(8)

        def poster = userRepository.save(new User("poster-${suffix}@test.local", "x", "Poster"))
        def first  = userRepository.save(new User("first-${suffix}@test.local",  "x", "First"))
        def second = userRepository.save(new User("second-${suffix}@test.local", "x", "Second"))
        userIds = [poster.id, first.id, second.id]

        itemId = itemService.post(
                new CreateItemRequest("Blue sofa", "Collection only",
                        Category.FURNITURE, "Luton", "07700 900000"),
                poster.id).id()
    }

    def cleanup() {
        itemRepository.deleteById(itemId)      // item first — it references the users
        userIds.each { userRepository.deleteById(it) }
    }

    def "exactly one of two simultaneous claimers wins"() {
        given: "two rivals and a starting gun"
        def start = new CountDownLatch(1)
        def pool = Executors.newFixedThreadPool(2)
        def rivals = [userIds[1], userIds[2]]

        when: "both call claim at the same instant"
        def futures = rivals.collect { userId ->
            pool.submit({
                start.await()                  // park here until released
                itemService.claim(itemId, userId)
            } as Callable)
        }
        start.countDown()                      // both threads go at once

        // future.get() rethrows whatever happened inside the thread,
        // so exceptions reach the assertions instead of vanishing
        def outcomes = futures.collect { future ->
            try {
                future.get(5, TimeUnit.SECONDS)
            } catch (ExecutionException e) {
                e.cause
            }
        }

        then: "one claim, one conflict — asserted as a pair"
        outcomes.count { it instanceof ItemDetail } == 1
        outcomes.count { it instanceof ItemAlreadyClaimedException } == 1

        and: "the database agrees"
        itemRepository.findById(itemId).get().status == ItemStatus.CLAIMED

        cleanup:
        pool.shutdown()
    }
}