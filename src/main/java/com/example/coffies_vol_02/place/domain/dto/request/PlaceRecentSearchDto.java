package com.example.coffies_vol_02.place.domain.dto.request;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRecentSearchDto implements Serializable {

    private String name;

    private String createdTime;

}
