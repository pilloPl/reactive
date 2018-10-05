package io.pillopl.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}


@RestController
class SomeController {

    private final BookingRepository bookingRepository;

    public static final Logger log = LoggerFactory.getLogger("this");

    SomeController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }


//	@GetMapping("/sth")
//	Mono<String> flux() throws InterruptedException {
//	    return Mono.fromCallable(() -> {
//			try {
//				TimeUnit.SECONDS.sleep(1);
//				log.info("this");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "this";
//        })
//				.subscribeOn(Schedulers.elastic());
//
//	}

//    @GetMapping("/sth")
//    String flux() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(1);
//        log.info("this");
//        return "this";
//    }

    @GetMapping("/sth")
    Callable<String> flux() throws InterruptedException {

        return () -> {
            TimeUnit.SECONDS.sleep(1);
            log.info("this");
            return "this";
        };
    }

    @PostMapping(path = "/bookings", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Flux<Booking> post(@RequestBody Flux<Booking> bookings) {
        return bookingRepository.saveAll(bookings);
    }


    @GetMapping(path = "/bookings", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    Flux<Booking> load() {
        return bookingRepository.findBookingsBy().log();
    }

    @GetMapping(path = "/bookings")
    Flux<Booking> load2() {
        return bookingRepository.findAll().log();
    }


    @Bean
    public CommandLineRunner runner(MongoOperations mongoOperations) {
        return args -> {

            mongoOperations.dropCollection(Booking.class);
            mongoOperations.createCollection(Booking.class, CollectionOptions.empty().size(1000000).capped());

        };
    }

}