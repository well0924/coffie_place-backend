package com.example.coffies_vol_02.Place.service;

import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

}
