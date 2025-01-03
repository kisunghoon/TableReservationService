package com.zerobase.tablereservationservice.except;

public class ManagerExistException extends RuntimeException {

    public ManagerExistException(String message) {
        super(message);
    }
}
