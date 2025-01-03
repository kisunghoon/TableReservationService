package com.zerobase.tablereservationservice.model;

import com.zerobase.tablereservationservice.type.ArrivalStatus;
import com.zerobase.tablereservationservice.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckApprovalRequest {

    Long reservationId;
    ReservationStatus status;
}
