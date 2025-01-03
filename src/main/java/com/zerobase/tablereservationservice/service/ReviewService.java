package com.zerobase.tablereservationservice.service;

import com.zerobase.tablereservationservice.except.RegisterReviewException;
import com.zerobase.tablereservationservice.except.ReviewDeleteException;
import com.zerobase.tablereservationservice.except.ReviewUpdateException;
import com.zerobase.tablereservationservice.model.ReviewRequest;
import com.zerobase.tablereservationservice.model.ReviewUpdateRequest;
import com.zerobase.tablereservationservice.persist.MemberRepository;
import com.zerobase.tablereservationservice.persist.ReservationRepository;
import com.zerobase.tablereservationservice.persist.ReviewRepository;
import com.zerobase.tablereservationservice.persist.StoreRepository;
import com.zerobase.tablereservationservice.persist.entity.MemberEntity;
import com.zerobase.tablereservationservice.persist.entity.ReviewEntity;
import com.zerobase.tablereservationservice.persist.entity.StoreEntity;
import com.zerobase.tablereservationservice.type.ArrivalStatus;
import com.zerobase.tablereservationservice.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ReviewService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    // 해당 가게에 예약과 ,사용을 한 사람만이 리뷰 작성 가능
    public void registerReview(ReviewRequest reviewRequest , Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 회원 ID입니다."));

        StoreEntity storeEntity = storeRepository.findById(reviewRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID 입니다."));

        boolean isRegisterReview = reservationRepository.existsByStoreIdAndMemberIdAndArrivalStatusAndStatus(
                storeEntity.getId(),
                memberEntity.getId(),
                ArrivalStatus.ARRIVED,
                ReservationStatus.CONFIRMED);


        if(!isRegisterReview) {
            throw new RegisterReviewException("해당 가게에 예약과 , 사용을 한 사람만이 리뷰 작성 가능 합니다.");
        } else {
            ReviewEntity reviewEntity = ReviewEntity.builder()
                                        .member(memberEntity)
                                        .store(storeEntity)
                                        .review(reviewRequest.getReview())
                                        .build();

            reviewRepository.save(reviewEntity);
        }
    }

    //리뷰 수정 기능 (리뷰 작성자만 수정 할 수 있도록)

    public void updateReview(ReviewUpdateRequest reviewRequest, Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 회원 ID입니다."));

        StoreEntity storeEntity = storeRepository.findById(reviewRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID 입니다."));


        boolean isCreatedBy = reservationRepository.existsByMemberId(memberId);

        if(isCreatedBy){
            ReviewEntity reviewEntity = reviewRepository.findById(reviewRequest.getId())
                    .orElseThrow(() -> new RuntimeException("수정은 리뷰 작성자만 가능합니다."));

            reviewEntity.setReview(reviewRequest.getReview());

            reviewRepository.save(reviewEntity);
        } else {
            throw new ReviewUpdateException("수정은 리뷰 작성자만 가능합니다.");
        }
    }

    //리뷰 삭제 기능 (리뷰를 작성한 사람과, 매장 매니저만 삭제 할수 있도록)

    public void deleteReview(Long id, Long memberId) {

        boolean isCreatedBy = reviewRepository.existsByMemberId(memberId);
        boolean isStoreManager = storeRepository.existsByManagerId(memberId);

        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("삭제는 리뷰 작성자 또는 점장만 가능합니다."));

        if(isCreatedBy || isStoreManager){

            reviewRepository.delete(reviewEntity);
        }
        if(!isCreatedBy && !isStoreManager){
            throw new ReviewDeleteException("삭제는 리뷰 작성자 또는 점장만 가능합니다.");
        }
    }
}
