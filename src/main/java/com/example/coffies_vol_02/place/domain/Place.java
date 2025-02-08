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
        @Index(name = "place_index2",columnList = "placeAddr")})
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "place_id"))})
public class Place extends BaseTime {

    private Double reviewRate;

    private String placeName;

    private String placeAddr;

    private String placePhone;

    private String placeAuthor;

    //가게운영 시작시간
    private String placeStart;

    //가게운영 종료시간
    private String placeClose;

    //가게정보가 삭제가 되면 가게 댓글이 삭제가 된다.
    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "place",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment>commentList = new ArrayList<>();

    //가게글이 삭제가 되면 가게 이미지도 같이 삭제가 된다.
    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(fetch =FetchType.EAGER,mappedBy = "place",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PlaceImage> placeImageList = new ArrayList<>();
    
    @Builder
    public Place( String placeName, String placeAuthor, String placeStart, String placeClose, String placePhone, String placeAddr, Double reviewRate, List<PlaceImage>placeImages){
        this.placeName = placeName;
        this.placeAuthor = placeAuthor;
        this.placeStart = placeStart;
        this.placeClose = placeClose;
        this.placePhone = placePhone;
        this.placeAddr = placeAddr;
        this.reviewRate = reviewRate != null ? reviewRate : 0.0;
        this.placeImageList = placeImages;
    }

    //이미지 첨부
    public void addPlaceImage(PlaceImage placeImage){
        this.placeImageList.add(placeImage);
        if(placeImage.getPlace() !=this){
            placeImage.setPlace(this);
        }
    }
    
    //가게 수정
    public void placeUpdate(PlaceRequestDto dto){
        this.placeStart = dto.getPlaceStart();
        this.placeClose = dto.getPlaceClose();
        this.placeAddr = dto.getPlaceAddr();
        this.placePhone = dto.getPlacePhone();
        this.placeName = dto.getPlaceName();
    }

    public void updateReviewRate(Double newRate) {
        this.reviewRate = newRate;
    }
}
