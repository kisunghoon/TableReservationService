package com.zerobase.tablereservationservice.service;


import com.zerobase.tablereservationservice.except.ManagerExistException;
import com.zerobase.tablereservationservice.model.StoreRequest;
import com.zerobase.tablereservationservice.model.StoreResponse;
import com.zerobase.tablereservationservice.model.StoreUpdateRequest;
import com.zerobase.tablereservationservice.persist.MemberRepository;
import com.zerobase.tablereservationservice.persist.ReservationRepository;
import com.zerobase.tablereservationservice.persist.StoreRepository;
import com.zerobase.tablereservationservice.persist.entity.MemberEntity;
import com.zerobase.tablereservationservice.persist.entity.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    /*
    * 매장 등록 기능
    * */
    public void registerStore(StoreRequest storeRequest, String manager) {

        MemberEntity member = memberRepository.findByUsername(manager)
                .orElseThrow(() -> new RuntimeException("Manager Not found"));

        StoreEntity storeEntity = StoreEntity.builder()
                .name(storeRequest.getName())
                .address(storeRequest.getAddress())
                .description(storeRequest.getDescription())
                .manager(member)
                .build();

        storeRepository.save(storeEntity);
    }
    /*
    * 매장 수정 기능
    * 
    * */
    public void updateStore(StoreUpdateRequest request) {

        StoreEntity storeEntity = storeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID 입니다."));

        storeEntity.setName(request.getName());
        storeEntity.setAddress(request.getAddress());
        storeEntity.setDescription(request.getDescription());

        storeRepository.save(storeEntity);

    }

    /*
    * 매장 매니저 변경 기능 ( 매장 수정 로직 과 분리시키는게 좋을 것으로 판단)
    * */
    public void changeManager(Long storeId, Long newManagerId) {

        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID 입니다."));

        MemberEntity memberEntity = memberRepository.findById(newManagerId)
                .orElseThrow(() -> new ManagerExistException("존재하지 않는 매니저 입니다."));

        boolean hasManagerRole = memberEntity.getRoles().contains("ROLE_MANAGER");

        if(!hasManagerRole) {
            throw new ManagerExistException("존재하지 않는 매니저 입니다.");
        }else {

            storeEntity.changeMember(memberEntity);

            storeRepository.save(storeEntity);
        }

    }

    /*
    * 매장 삭제 기능
    * */
    @Transactional
    public void deleteStore(Long storeId) {

        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장 ID입니다. "));

        reservationRepository.deleteByStoreId(storeId);
        storeRepository.delete(storeEntity);
    }

    /**
     * 매장 검색 기능
     * @param name 매장 이름
     * @return StoreResponse
     */
    public List<StoreResponse> searchStores(String name) {
        List<StoreEntity> storeEntities = storeRepository.findByNameContainingIgnoreCase(name);

        return storeEntities.stream()
                .map(store -> new StoreResponse(store.getId(),store.getName(),store.getAddress(),store.getDescription()))
                .collect(Collectors.toList());
    }
}
