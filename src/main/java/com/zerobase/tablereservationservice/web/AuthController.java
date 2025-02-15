package com.zerobase.tablereservationservice.web;

import com.zerobase.tablereservationservice.model.Auth;
import com.zerobase.tablereservationservice.security.TokenProvider;
import com.zerobase.tablereservationservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 회원 가입 API
     * 사용자 회원 가입
     * @param
     * {
     *     "username":"graceUser",
     *     "password":"grace123!@#",
     *     "roles":["ROLE_USER"]
     * }
     * 매장 관리자 회원 가입
     * {
     *     "username":"graceManager",
     *     "password":"grace123!@#",
     *     "roles":["ROLE_MANAGER"]
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request){
        var result = this.memberService.register(request);

        return ResponseEntity.ok(result);
    }

    /**
     * 로그인 API
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request){

        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getUsername(),member.getRoles());

        return ResponseEntity.ok(token);
    }


}
