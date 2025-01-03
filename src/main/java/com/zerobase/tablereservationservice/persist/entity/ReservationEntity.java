package com.zerobase.tablereservationservice.persist.entity;


import com.zerobase.tablereservationservice.type.ArrivalStatus;
import com.zerobase.tablereservationservice.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="store_id")
    private StoreEntity store;

    @ManyToOne
    @JoinColumn(name="member_id")
    private MemberEntity member;

    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    private ArrivalStatus arrivalStatus;

    private LocalDateTime arrivalTime;

    public boolean isArrival(LocalDateTime reservationTime ,LocalDateTime arrivalTime) {

        LocalDateTime tenMinutes = reservationTime.minusMinutes(10);

        return (arrivalTime.isBefore(tenMinutes) || arrivalTime.isEqual(tenMinutes));

    }

    public static boolean isValidReservation(LocalDateTime reservationTime, LocalDateTime arrivalTime){

        if(reservationTime.isBefore(LocalDateTime.now())){
            return false;
        }

        return true;
    }
}
