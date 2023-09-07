package com.example.coffies_vol_02.config.exception.Dto;

import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DownloadResponseDto<T> {
    private Integer status;
    private HttpHeaders headers;
    private Resource res;
}
