package com.example.coffies_vol_02.place.domain;

import com.example.coffies_vol_02.config.BaseTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
//@Proxy(lazy = false)
@Table(name = "tbl_place_imge")
@NoArgsConstructor
public class PlaceImage extends BaseTime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileGroupId;

    private String fileType;

    private String imgGroup;

    private String imgPath;

    private String thumbFilePath;

    private String thumbFileImagePath;

    @Column(length = 1000)
    private String storedName;

    @Column(length = 1000)
    private String originName;

    private String imgUploader;

    private String isTitle;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "place_id")
    private Place place;

    @Builder
    public PlaceImage(
            String fileGroupId,
            String fileType,
            String imgGroup,
            String imgPath,
            String thumbFileImagePath,
            String thumbFilePath,
            String storedName,
            String originName,
            String imgUploader,
            String isTitle) {
        this.fileGroupId = fileGroupId;
        this.fileType = fileType;
        this.imgGroup = imgGroup;
        this.imgPath = imgPath;
        this.thumbFileImagePath = thumbFileImagePath;
        this.thumbFilePath = thumbFilePath;
        this.originName = originName;
        this.storedName = storedName;
        this.imgUploader = imgUploader;
        this.isTitle = isTitle;
        setPlace(place);
    }

    //가게 이미지 추가
    public void setPlace(Place place){
        this.place = place;
        if (place != null && !place.getPlaceImageList().contains(this)) {
            place.getPlaceImageList().add(this);  // Place 객체에 이미지 추가
        }
    }
}
