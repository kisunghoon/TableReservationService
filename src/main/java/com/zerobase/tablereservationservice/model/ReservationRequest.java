package com.zerobase.tablereservationservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    Long storeId;
    String reservationTime;

    public LocalDateTime getReservationTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(reservationTime, formatter);
    }
}
