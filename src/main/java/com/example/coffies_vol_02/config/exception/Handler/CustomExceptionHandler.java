package com.example.coffies_vol_02.config.exception.Handler;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomExceptionHandler extends RuntimeException{

    private ERRORCODE errorCode;

    public CustomExceptionHandler(ERRORCODE errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ERRORCODE getErrorCode() {
        return errorCode;
    }

}
