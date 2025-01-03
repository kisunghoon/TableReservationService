package com.zerobase.tablereservationservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String address;
    private String description;
}
