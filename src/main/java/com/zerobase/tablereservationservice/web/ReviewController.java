package com.zerobase.tablereservationservice.web;

import com.zerobase.tablereservationservice.model.ReviewRequest;
import com.zerobase.tablereservationservice.model.ReviewUpdateRequest;
import com.zerobase.tablereservationservice.persist.entity.MemberEntity;
import com.zerobase.tablereservationservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * http://localhost:8080/review/register
     * 리뷰 등록  API
     * @param
        {
        "storeId":1,
        "review" : "리뷰 "
        }
     * @param authentication
     */
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerReview(@RequestBody ReviewRequest request,
                                            Authentication authentication) {

        try{

            MemberEntity member = (MemberEntity) authentication.getPrincipal();
            Long memberId = member.getId();

            reviewService.registerReview(request,memberId);
            return ResponseEntity.ok("리뷰작성이 성공적으로 완료되었습니다.");

        } catch (Exception e) {
            log.error("리뷰 작성 중 문제가 생겼습니다." ,e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * http://localhost:8080/review/update
     * 리뷰 수정 API
     * @param
        {
            "id" :1,
            "storeId": 1,
            "review" : "리뷰 수정 "
        }
     * @param authentication
     */
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @PostMapping("/update")
    public ResponseEntity<?> updateReview(@RequestBody ReviewUpdateRequest request,
                                          Authentication authentication) {

        try{

            MemberEntity member = (MemberEntity) authentication.getPrincipal();
            Long memberId = member.getId();

            reviewService.updateReview(request,memberId);
            return ResponseEntity.ok("리뷰 수정이 완료 되었습니다.");


        } catch(Exception e){

            log.error("리뷰 수정 중 오류 발생 : {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        }
    }

    /**
     * http://localhost:8080/review/delete/{id}
     * 리뷰 삭제 API
     * @param id
     * @param authentication
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    public ResponseEntity<?> deleteReview(@PathVariable Long id,
                                          Authentication authentication) {

        try{
            MemberEntity member = (MemberEntity) authentication.getPrincipal();
            Long memberId = member.getId();

            this.reviewService.deleteReview(id,memberId);
            return ResponseEntity.ok("리뷰가 성공적으로 삭제 되었습니다.");

        } catch(Exception e){

            log.error("리뷰 삭제 중 오류 발생: {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


}
