package com.example.coffies_vol_02.Place.service;

import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public Page<PlaceDto.PlaceResponseDto>placeList(Pageable pageable){
        Page<Place>list = placeRepository.findAll(pageable);
        return list.map(place -> new PlaceDto.PlaceResponseDto(place));
    }

    @Transactional
    public PlaceDto.PlaceResponseDto placeDetail(Integer placeId){
        Optional<Place> place = Optional.of(placeRepository.findById(placeId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));
        Place detail = place.get();
        return PlaceDto.PlaceResponseDto
                .builder()
                .place(detail)
                .build();
    }
}
