package io.pillopl.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(MongoOperations mongoOperations) {
        return args -> {
            mongoOperations.dropCollection(Booking.class);
            mongoOperations.createCollection(Booking.class, CollectionOptions.empty().size(1000000).capped());
        };
    }


}

@RestController

class SalesController {

    @GetMapping("/sales/{player}")
    Mono<Boolean> canWeBuy(@PathVariable String player) throws InterruptedException {
        Callable<Boolean> callable = () -> {
            System.out.println("can we buy " + player);
            TimeUnit.SECONDS.sleep(1);
            return false;

        };
        return Mono.fromCallable(callable).subscribeOn(Schedulers.elastic());
    }
}

@RestController
class BookingController {

    private final BookingRepository bookingRepository;

    BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("bookings")
    Flux<Booking> bookings() {
        return bookingRepository.findAll().log();
    }

    @GetMapping(value = "bookings", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    Flux<Booking> bookingsStream() {
        return bookingRepository.findBookingsBy().log();
    }

    @PostMapping("bookings")
    Flux<Booking> bookings(@RequestBody Flux<Booking> bookings) {
        return bookingRepository.insert(bookings).log();
    }


}
