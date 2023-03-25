package com.example.coffies_vol_02.config.Exception.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDto {
    private Integer errorcode;
    private String message;
}
