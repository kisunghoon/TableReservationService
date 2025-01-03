package com.zerobase.tablereservationservice.persist.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="store_id")
    private StoreEntity store;

    @ManyToOne
    @JoinColumn(name="member_id")
    private MemberEntity member;

    @Column(columnDefinition = "TEXT")
    private String review;

}
