package com.example.coffies_vol_02.Place.domain;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Config.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_place")
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
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "place")
    private List<Comment>commentList = new ArrayList<>();
    @ToString.Exclude
    @OneToMany(fetch =FetchType.LAZY,mappedBy = "place")
    private List<PlaceImage> placeImageList = new ArrayList<>();


    @Builder
    public Place(String placeName,String placeAuthor,String placeStart,String placeClose,String placePhone,String placeAddr1,String placeAddr2,String fileGroupId,Double placeLng,Double placeLat,Double reviewRate){
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
        this.reviewRate = reviewRate;
    }

}
