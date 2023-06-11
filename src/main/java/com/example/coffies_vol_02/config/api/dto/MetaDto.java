package com.example.coffies_vol_02.config.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetaDto {
    //json으로 응답을 받을 때 필드와 매핑을 시켜주는 어노테이션
    @JsonProperty("total_count")
    private Integer totalCount;

}
