package com.zerobase.tablereservationservice.web;

import com.zerobase.tablereservationservice.model.CheckApprovalRequest;
import com.zerobase.tablereservationservice.model.ReservationRequest;
import com.zerobase.tablereservationservice.persist.entity.MemberEntity;
import com.zerobase.tablereservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * http://localhost:8080/reservation/register
     * 예약 등록 API
     * @param request
     * {
     *     "storeId":1,
     *     "reservationTime" : "2024-12-28 18:30"
     * }
     *
     */
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerReservation(@RequestBody ReservationRequest request,
                                                 Authentication authentication) {

        try{
            MemberEntity member = (MemberEntity) authentication.getPrincipal();
            Long memberId = member.getId();

            reservationService.registerReservation(request,memberId);
            return ResponseEntity.ok("예약이 성공적으로 완료 되었습니다.");


        }catch(Exception e){
            log.error("예약 처리 중에 문제가 생겼습니다.", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * http://localhost:8080/reservation/confirm?reservationId={id}
     * 방문 확인 API
     * @param reservationId
     *
     */
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @PostMapping("/confirm")
    public ResponseEntity<?> visitConfirm(@RequestParam Long reservationId) {

        try{

            reservationService.visitConfirm(reservationId);
            return ResponseEntity.ok("방문확인이 확인 되었습니다.");

        } catch(RuntimeException e){
            log.error("visitConfirm Error ",e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * http://localhost:8080/reservation/approval
     * 승인/예약 취소, 처리 API
     * @param
        {
        "reservationId":1,
        "status" : "CONFIRMED"
        }
     * @param authentication
     * @return
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/approval")
    public ResponseEntity<?> checkApproval(@RequestBody CheckApprovalRequest request,
                                           Authentication authentication){

        boolean isManager = authentication.getAuthorities().stream().anyMatch(auth-> auth.getAuthority().equals("ROLE_MANAGER"));

        if(!isManager) {
            log.info("매니저에 권한을 갖고 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try{

            reservationService.checkApproval(request);
            return ResponseEntity.ok("승인 / 예약 거절을 완료하였습니다.");

        } catch(RuntimeException e){

            log.error("checkApproval Error ",e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }




}
