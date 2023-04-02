package com.example.coffies_vol_02.Config.Exception.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDto {
    private Integer errorcode;
    private String message;
}
