package com.zerobase.tablereservationservice.persist;

import com.zerobase.tablereservationservice.persist.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findByNameContainingIgnoreCase(String storeName);

    boolean existsByManagerId(Long memberId);

}
