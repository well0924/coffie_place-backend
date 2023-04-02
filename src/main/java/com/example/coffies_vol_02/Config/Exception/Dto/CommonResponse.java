package com.example.coffies_vol_02.config.Exception.Dto;

import lombok.*;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {

    private Integer status;

    private T data;
}
