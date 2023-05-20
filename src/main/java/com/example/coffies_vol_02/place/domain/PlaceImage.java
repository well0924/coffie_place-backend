package com.example.coffies_vol_02.place.domain;

import com.example.coffies_vol_02.config.BaseTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
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
            String isTitle){
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
    }
}
