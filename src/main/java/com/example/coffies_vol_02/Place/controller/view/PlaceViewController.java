package com.example.coffies_vol_02.Place.controller.view;

import com.example.coffies_vol_02.Place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PlaceViewController {
    private final PlaceService placeService;
}
