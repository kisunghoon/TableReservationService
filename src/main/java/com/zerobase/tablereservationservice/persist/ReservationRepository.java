package com.zerobase.tablereservationservice.persist;

import com.zerobase.tablereservationservice.persist.entity.ReservationEntity;
import com.zerobase.tablereservationservice.type.ArrivalStatus;
import com.zerobase.tablereservationservice.type.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,Long> {

    void deleteByStoreId(Long storeId);

    /*
    예약 후 1시간은 예약 불가
    * */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ReservationEntity r " +
            "WHERE r.store.id = :storeId " +
            "AND ((:startTime BETWEEN r.reservationTime AND r.reservationTime + 1 HOUR) " +
            "OR (:endTime BETWEEN r.reservationTime AND r.reservationTime + 1 HOUR) " +
            "OR (r.reservationTime BETWEEN :startTime AND :endTime))")
    boolean existsByStoreAndReservationTime(@Param("storeId") Long storeId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    boolean existsByStoreIdAndMemberIdAndArrivalStatusAndStatus(Long storeId, Long memberId ,
                                                              ArrivalStatus arrivalStatus, ReservationStatus status);

    boolean existsByMemberId(Long memberId);


    boolean existsByIdAndStatus(Long id, ReservationStatus status);
}
