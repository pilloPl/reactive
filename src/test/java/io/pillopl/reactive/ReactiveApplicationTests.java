package io.pillopl.reactive;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ReactiveApplicationTests {

    @Test
    public void test() throws Exception {

        Mono<String> ramos = canWeBuy("no!", 1000);
        Mono<String> umtitti = canWeBuy("no *f* way!!", 500);
        Mono<String> pazdan = canWeBuy("sure :D", 300);

        Flux<String> theQuickest =
                ramos
                .mergeWith(umtitti)
                .mergeWith(pazdan)
                .filter(decsion -> !decsion.contains("no"));

        StepVerifier.create(theQuickest)
                .expectNext("sure :D")
                .verifyComplete();

    }

    @Test
    public void test2() throws Exception {
            Flux<String> newPlayersInNewClubs =
                    randomClubs()
                    .skipLast(1)
                    .takeLast(3)
                    .zipWith(randomPlayers(), (c, p) -> p + " in " + c);

            StepVerifier.create(newPlayersInNewClubs)
                    .expectNext("Lloris in Roma")
                    .expectNext("Kane in Chelsea")
                    .expectNext("Moura in Arsenal")
                    .verifyComplete();
    }

    @Test
    public void test4() throws Exception {

        StepVerifier.withVirtualTime(() -> canWeBuy("no", 10))
                .expectSubscription()
                .expectNoEvent(Duration.ofDays(10))
                .expectNext("no")
                .verifyComplete();
    }

    Flux<String> randomClubs() {
        return Flux.just("FCB", "Roma", "Chelsea", "Arsenal", "Bayern");
    }

    Flux<String> randomPlayers() {
        return Flux.just("Lloris", "Kane", "Moura");
    }

    Mono<String> canWeBuy(String decision, long days) {
        return Mono.delay(Duration.ofDays(days)).map(i -> decision);
    }

}
