package com.example.coffies_vol_02.Config.Exception.Handler;

import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomExceptionHandler extends RuntimeException{

    private ERRORCODE errorcode;

    public CustomExceptionHandler(ERRORCODE errorcode) {
        super(errorcode.getMessage());
        this.errorcode = errorcode;
    }


    public ERRORCODE getErrorCode() {
        return errorcode;
    }
}
