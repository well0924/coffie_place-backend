package com.example.coffies_vol_02.config.exception.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)//null값이 나오는 경우에는 화면에 나오지 않게 하는 어노테이션
public class CommonResponse<T> implements Serializable {
    private Integer status;
    private String message;
    private T data;

    public CommonResponse(Integer status,String message){
        this.status = status;
        this.message = message;
    }
    public CommonResponse(Integer status,T data){
        this.status = status;
        this.data = data;
    }
}
