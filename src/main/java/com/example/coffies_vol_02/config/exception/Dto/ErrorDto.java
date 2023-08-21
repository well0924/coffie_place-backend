package com.example.coffies_vol_02.config.exception.Dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ErrorDto {

    private HttpStatus httpStatus;
    private Integer errorCode;
    private String message;

    @Builder
    public ErrorDto(HttpStatus httpStatus,Integer errorCode,String message){
        this.errorCode = errorCode;
        this.message = message;
    }
}
