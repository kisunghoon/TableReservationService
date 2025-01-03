package com.zerobase.tablereservationservice.except;

public class ReservationTimeException extends RuntimeException {

    public ReservationTimeException(String message) {
        super(message);
    }
}
