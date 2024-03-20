package com.example.coffies_vol_02.config.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCrawlingDto {
    @JsonProperty("place_start")
    private String placeStart;
    @JsonProperty("place_close")
    private String placeClose;
    @JsonProperty("place_image")
    private List<MultipartFile> placeImage = new ArrayList<>();
    public void addPlaceTime(String placeStart, String placeClose){
        this.placeStart = placeStart;
        this.placeClose = placeClose;
    }

    public void addPlaceImage(MultipartFile image){
        this.placeImage.add(image);
    }
}
