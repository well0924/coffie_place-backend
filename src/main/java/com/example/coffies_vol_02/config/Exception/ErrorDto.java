package com.example.coffies_vol_02.config.Exception;

import org.springframework.http.HttpStatus;

public interface ErrorDto {
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
