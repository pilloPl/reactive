package io.pillopl.reactive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Random;

@Document
public class Booking {

    @Id
    private String refNo;

    @JsonCreator
    public Booking(@JsonProperty("refNo") String refNo) {
        this.refNo = refNo;
    }

    Booking() {
        refNo = "REF" + new Random().nextInt(1000000);
    }

    public String getRefNo() {
        return refNo;
    }
}
