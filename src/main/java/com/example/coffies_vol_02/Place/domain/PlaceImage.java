package com.example.coffies_vol_02.Place.domain;

import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "tbl_place_imge")
@NoArgsConstructor
@AllArgsConstructor
public class PlaceImage extends BaseTime {
    @Id
    @Column(name = "img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileGroupId;
    private String fileType;
    private String imgGroup;
    private String imgPath;
    private String thumbFilePath;
    private String thumbFileImagePath;
    private String storedName;
    private String originName;
    private String imgUploader;
    private String isTitle;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Builder
    public PlaceImage(String fileGroupId,String imgUploader,String fileType,String thumbFileImagePath,String thumbFilePath,String storedName,String originName,String imgPath,String imgGroup,String isTitle){
        this.fileGroupId = fileGroupId;
        this.fileType = fileType;
        this.imgPath = imgPath;
        this.isTitle = isTitle;
        this.imgGroup = imgGroup;
        this.imgUploader = imgUploader;
        this.originName = originName;
        this.storedName = storedName;
        this.thumbFileImagePath = thumbFileImagePath;
        this.thumbFilePath = thumbFilePath;
    }
}
