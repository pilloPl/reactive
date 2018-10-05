package io.pillopl.reactive;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ReactiveApplicationTests {

    @Test
    public void testLoans() throws Exception {

        Mono<String> loan1 = loan(10);
        Mono<String> loan2 = loan(20);
        Mono<String> loan3 = loan(6);


        Mono<String> theFastest = loan1
                .mergeWith(loan3)
                .mergeWith(loan2)
                .next();

        StepVerifier
                .create(theFastest)
                .expectNext("loan 6")
                .expectComplete()
                .verify();

    }

    @Test
    public void testZip() throws Exception {
        Flux<String> weatherInCities =
                cities()
                .zipWith(weather(), (c, w) -> w + " in " + c);

        StepVerifier.create(weatherInCities)
                .expectNext("rainy in London")
                .expectNext("warm in Paris")
                .expectNext("chilly in Brussels")
                .expectComplete()
                .verify();

    }

    @Test
    public void test() throws Exception {
            StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(2)).map(delay -> "loan after 2 days"))
                    .expectSubscription()
                    .expectNoEvent(Duration.ofDays(2))
                    .expectNext("loan after 2 days")
                    .verifyComplete();
    }

    private Flux<String> cities() {
        return Flux.just("London", "Paris", "Brussels");
    }

    private Flux<String> weather() {
        return Flux.just("rainy", "warm", "chilly");
    }

    private Mono<String> loan(long milis) {
        return Mono.delay(Duration.ofMillis(milis)).map(loan -> "loan " + milis).log();
    }

}
