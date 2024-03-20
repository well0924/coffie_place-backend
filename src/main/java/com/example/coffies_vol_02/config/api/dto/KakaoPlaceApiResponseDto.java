package com.example.coffies_vol_02.config.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPlaceApiResponseDto {
    @JsonProperty("meta")
    private MetaDto metaDto;

    @JsonProperty("documents")
    private List<PlaceDocumentDto> documentList;
}
