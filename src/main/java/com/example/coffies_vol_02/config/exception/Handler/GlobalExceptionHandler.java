package com.example.coffies_vol_02.config.exception.Handler;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomExceptionHandler.class})
    public CommonResponse<?> exceptionHandler(CustomExceptionHandler ex){
        return CommonResponse.builder()
                .status(ex.getErrorCode().getErrorCode())
                .data(ex.getErrorCode().getMessage())
                .build();
    }

}
