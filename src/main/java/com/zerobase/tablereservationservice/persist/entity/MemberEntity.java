package com.zerobase.tablereservationservice.persist.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MemberEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    /**
     * 
     * 계정의 권한 목록을 리턴
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
    }

    /**
     * 
     * 계정의 만료 여부 리턴
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
    /**
     *
     * 계정의 잠김 여부 리턴
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }
    /**
     *
     * 비밀번호 만료 여부 리턴
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * 
     * 계정의 활성화 여부 리턴
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
