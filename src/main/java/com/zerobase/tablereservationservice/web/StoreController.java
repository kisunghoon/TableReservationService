package com.zerobase.tablereservationservice.web;

import com.zerobase.tablereservationservice.model.StoreRequest;
import com.zerobase.tablereservationservice.model.StoreResponse;
import com.zerobase.tablereservationservice.model.StoreUpdateRequest;
import com.zerobase.tablereservationservice.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 매장 등록 API
     * @param request :
     * {
     *   "name": "Test Store",
     *   "address": "123 address",
     *   "description": "A test store for demonstration."
     * }
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> register(@RequestBody StoreRequest request, Authentication authentication) {

        boolean isManager = authentication.getAuthorities().stream().anyMatch(auth-> auth.getAuthority().equals("ROLE_MANAGER"));

        if(!isManager) {
            log.info("매니저에 권한을 갖고 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        String manager = authentication.getName();
        try{
            storeService.registerStore(request,manager);
            return ResponseEntity.ok("매장 등록이 성공적으로 되었습니다.");

        } catch (Exception e){
            log.error("매장 등록 중 오류 발생: {} ",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
        }

    }

    /**
     * 매장 수정 API
     * @param request :
        {
        "id" :1,
        "name": "update Store",
        "address": "123 Main Street update",
        "description": "A test store for demonstration update ."
        }
     *
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> update(@RequestBody StoreUpdateRequest request, Authentication authentication) {

        boolean isManager = authentication.getAuthorities().stream().anyMatch(auth-> auth.getAuthority().equals("ROLE_MANAGER"));

        if(!isManager) {
            log.info("매니저에 권한을 갖고 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try{
            storeService.updateStore(request);
            return ResponseEntity.ok("매장 수정이 성공적으로 되었습니다.");

        }catch(Exception e){

            log.error("매장 수정 중 오류 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }

    }

    /**
     * 매장 담당 매니저 변경 API
     * http://localhost:8080/store/changemanager?storeId= {storeId} &newManagerId= {newManagerId}
     * 
     */
    @PostMapping("/changemanager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeManager(@RequestParam Long storeId, @RequestParam Long newManagerId,
                                           Authentication authentication) {

        boolean isManager = authentication.getAuthorities().stream().anyMatch(auth-> auth.getAuthority().equals("ROLE_MANAGER"));

        if(!isManager) {
            log.info("매니저에 권한을 갖고 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try{

            storeService.changeManager(storeId,newManagerId);

        } catch(Exception e){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }

        return ResponseEntity.ok("매니저가 성공적으로 변경되었습니다");
    }

    /**
     * 매장 삭제 API
     * http://localhost:8080/store/delete/{storeId}
     *
     */
    @DeleteMapping("/delete/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteStore(@PathVariable Long storeId, Authentication authentication) {

        boolean isManager = authentication.getAuthorities().stream().anyMatch(auth-> auth.getAuthority().equals("ROLE_MANAGER"));

        if(!isManager) {
            log.info("매니저에 권한을 갖고 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try{
            this.storeService.deleteStore(storeId);
            return ResponseEntity.ok("매장 삭제가 성공적으로 되었습니다.");
        }catch(Exception e){

            log.error("매장 삭제 중 오류 발생 : {} ",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 매장 검색 API
     * http://localhost:8080/store/search?name={name}
     * @param name 매장 이름
     * @return
     */
    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @GetMapping("/search")
    public ResponseEntity<List<StoreResponse>> searchStore(@RequestParam String name){

        List<StoreResponse> stores = storeService.searchStores(name);
        return ResponseEntity.ok(stores);
    }
}
