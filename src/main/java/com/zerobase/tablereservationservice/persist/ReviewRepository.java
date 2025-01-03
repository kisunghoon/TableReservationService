package com.zerobase.tablereservationservice.persist;


import com.zerobase.tablereservationservice.persist.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByMemberId(Long memberId);
}
