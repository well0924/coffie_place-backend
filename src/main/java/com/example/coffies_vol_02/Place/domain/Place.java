package com.example.coffies_vol_02.Place.domain;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_place")
@ToString(exclude = {"placeImageList","commentList"})
@NoArgsConstructor
@AllArgsConstructor
public class Place extends BaseTime {
    @Id
    @Column(name = "place_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double placeLng;
    private Double placeLat;
    private Double reviewRate;
    private String placeName;
    private String placeAddr1;
    private String placeAddr2;
    private String placePhone;
    private String placeAuthor;
    private String placeStart;
    private String placeClose;
    private String fileGroupId;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "place")
    private List<Comment>commentList = new ArrayList<>();
    //가게글이 삭제가 되면 가게 이미지도 같이 삭제가 된다.
    @OneToMany(fetch =FetchType.LAZY,mappedBy = "place",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PlaceImage> placeImageList = new ArrayList<>();
    
    @Builder
    public Place(Integer id, String placeName, String placeAuthor, String placeStart, String placeClose, String placePhone, String placeAddr1, String placeAddr2, String fileGroupId, Double placeLng, Double placeLat, Double reviewRate, List<PlaceImage>placeImages){
        this.id = id;
        this.placeName = placeName;
        this.placeAuthor = placeAuthor;
        this.placeStart = placeStart;
        this.placeClose = placeClose;
        this.placePhone = placePhone;
        this.placeAddr1 = placeAddr1;
        this.placeAddr2 = placeAddr2;
        this.fileGroupId = fileGroupId;
        this.placeLng = placeLng;
        this.placeLat = placeLat;
        this.reviewRate = 0.0;
        this.placeImageList = placeImages;
    }

    //이미지 첨부
    public void addPlaceImage(PlaceImage placeImage){
        this.placeImageList.add(placeImage);
        if(placeImage.getPlace() !=this){
            placeImage.setPlace(this);
        }
    }

    public void placeUpadate(PlaceDto.PlaceRequestDto dto){
        this.placeLat = dto.getPlaceLat();
        this.placeLng = dto.getPlaceLng();
        this.placeStart = dto.getPlaceStart();
        this.placeClose = dto.getPlaceClose();
        this.placeAddr1 = dto.getPlaceAddr1();
        this.placeAddr2 = dto.getPlaceAddr2();
        this.placePhone = dto.getPlacePhone();
        this.placeName = dto.getPlaceName();
    }
}
