package com.zerobase.tablereservationservice.except;

public class ReservationExistException extends RuntimeException {

    public ReservationExistException(String message) {
        super(message);
    }
}
