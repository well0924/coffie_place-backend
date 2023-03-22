package com.example.coffies_vol_02.config.Exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final ErrorDto errorDto;

    public RestApiException(String message,ERRORCODE errorDto){
        super(message);
        this.errorDto = errorDto;
    }
}
