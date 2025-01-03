package com.zerobase.tablereservationservice.service;

import com.zerobase.tablereservationservice.except.ReservationTimeException;
import com.zerobase.tablereservationservice.model.CheckApprovalRequest;
import com.zerobase.tablereservationservice.model.ReservationRequest;
import com.zerobase.tablereservationservice.persist.MemberRepository;
import com.zerobase.tablereservationservice.persist.ReservationRepository;
import com.zerobase.tablereservationservice.persist.StoreRepository;
import com.zerobase.tablereservationservice.persist.entity.MemberEntity;
import com.zerobase.tablereservationservice.persist.entity.ReservationEntity;
import com.zerobase.tablereservationservice.persist.entity.StoreEntity;
import com.zerobase.tablereservationservice.type.ArrivalStatus;
import com.zerobase.tablereservationservice.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    /**
     *  예약 등록 기능
     */
    @Transactional
    public void registerReservation(ReservationRequest request , Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원 ID입니다."));

        StoreEntity storeEntity = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID입니다."));

        LocalDateTime startTime = request.getReservationTime();
        LocalDateTime endTime = startTime.plusHours(1);

        boolean isExistReservation = reservationRepository.
                existsByStoreAndReservationTime(request.getStoreId(),startTime,endTime);

        boolean isValidReservation = ReservationEntity.isValidReservation(request.getReservationTime(), LocalDateTime.now());

        if(isExistReservation){
            throw new ReservationTimeException("해당 시간에는 예약 시간이 존재합니다.");
        }

        if(!isValidReservation){
            throw new ReservationTimeException("예약 시간 과거는 예약 할 수 없습니다.");
        }


        ReservationEntity reservationEntity = ReservationEntity.builder()
                .member(memberEntity)
                .store(storeEntity)
                .reservationTime(request.getReservationTime())
                .arrivalStatus(ArrivalStatus.WAITING)
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservationEntity);

    }

    /*
    * 예약 10분전이 확인이 된다면
    * */
    public void visitConfirm(Long reservationId) {

        ReservationEntity reservationEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 예약 ID입니다."));

        boolean isArrival = reservationEntity.isArrival(reservationEntity.getReservationTime(), LocalDateTime.now());

        if(isArrival) {

            reservationEntity.setArrivalStatus(ArrivalStatus.ARRIVED);
            reservationEntity.setArrivalTime(LocalDateTime.now());

            reservationRepository.save(reservationEntity);

        } else {
            throw new ReservationTimeException("예약 10분 전에 도착하여야 합니다..\n 예약 시간 초과입니다.");
        }


    }
    /*
     승인/예약 취소 기능
     */
    public void checkApproval(CheckApprovalRequest request) {

        ReservationEntity reservationEntity = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 예약 ID입니다."));

        reservationEntity.setStatus(ReservationStatus.CONFIRMED);

        reservationRepository.save(reservationEntity);

    }
}
