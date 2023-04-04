package com.example.coffies_vol_02.Place.controller.api;

import com.example.coffies_vol_02.Place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PlaceApiController {
    private final PlaceService placeService;


}
