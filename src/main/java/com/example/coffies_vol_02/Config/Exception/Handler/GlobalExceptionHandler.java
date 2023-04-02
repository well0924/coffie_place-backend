package com.example.coffies_vol_02.config.Exception.Handler;

import com.example.coffies_vol_02.config.Exception.Dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomExceptionHandler.class})
    protected ResponseEntity<ErrorDto> HandleCustomException(CustomExceptionHandler ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getErrorCode().getHttpStatus(), ex.getErrorCode().getMessage()), HttpStatus.valueOf(ex.getErrorCode().getHttpStatus()));
    }

}
