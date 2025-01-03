package com.zerobase.tablereservationservice.persist.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private MemberEntity manager;


    public void changeMember(MemberEntity newManager) {
        this.manager = newManager;
    }
}
