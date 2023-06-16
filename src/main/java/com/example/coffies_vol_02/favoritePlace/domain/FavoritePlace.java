package com.example.coffies_vol_02.favoritePlace.domain;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "tbl_favorite_place")
@NoArgsConstructor
public class FavoritePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Place place;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Member member;
    private String fileGroupId;
    @Transient
    private List<PlaceImage>placeImages = new ArrayList<>();
    @Transient
    private String isTitle;
    @Transient
    private String ThumbFileImagePath;
    @Builder
    public FavoritePlace(Integer id, Place place, Member member, String fileGroupId,List<PlaceImage>placeImages) {
        this.id = id;
        this.place = place;
        this.member = member;
        this.fileGroupId = place.getFileGroupId();
        this.placeImages = placeImages;
        this.isTitle = placeImages.get(0).getIsTitle();
        this.ThumbFileImagePath = placeImages.get(0).getThumbFileImagePath();
    }
}
