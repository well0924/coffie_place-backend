package com.example.coffies_vol_02.place.domain;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_place",indexes = {
        @Index(name = "place_index1",columnList = "placeName"),
        @Index(name = "place_index4",columnList = "placeAuthor"),
        @Index(name = "place_index2",columnList = "placeAddr1")})
@NoArgsConstructor
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
    //가게운영 시작시간
    private String placeStart;
    //가게운영 종료시간
    private String placeClose;
    private String fileGroupId;
    
    //가게정보가 삭제가 되면 가게 댓글이 삭제가 된다.
    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "place",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment>commentList = new ArrayList<>();

    //가게글이 삭제가 되면 가게 이미지도 같이 삭제가 된다.
    @BatchSize(size = 100)
    @JsonIgnore
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
    }

    //이미지 첨부
    public void addPlaceImage(PlaceImage placeImage){
        this.placeImageList.add(placeImage);
        if(placeImage.getPlace() !=this){
            placeImage.setPlace(this);
        }
    }
    
    //가게 수정
    public void placeUpadate(PlaceRequestDto dto){
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
