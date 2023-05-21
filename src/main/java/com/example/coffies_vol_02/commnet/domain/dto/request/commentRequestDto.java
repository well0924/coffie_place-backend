package com.example.coffies_vol_02.commnet.domain.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class commentRequestDto {
    private String replyWriter;
    private String replyContents;
    private Integer replyPoint;
}
