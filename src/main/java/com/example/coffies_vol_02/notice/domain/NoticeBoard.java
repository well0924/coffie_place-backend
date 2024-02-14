package com.example.coffies_vol_02.notice.domain;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_notice",
        indexes = {
        @Index(name = "notice_index1",columnList = "id"),
        @Index(name = "notice_index3",columnList = "isFixed"),
        @Index(name = "notice_index4",columnList = "noticeTitle")
})
@NoArgsConstructor
public class NoticeBoard extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String noticeGroup;
    private String noticeTitle;
    private String noticeWriter;
    private String noticeContents;
    private String fileGroupId;
    private Character isFixed;

    @JsonIgnore
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "noticeBoard",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Attach> attachList = new ArrayList<>();

    @Builder
    public NoticeBoard(Integer id,String noticeGroup,String noticeTitle,String noticeWriter,String noticeContents,String fileGroupId,Character isFixed){
        this.id = id;
        this.noticeGroup = noticeGroup;
        this.noticeTitle = noticeTitle;
        this.noticeContents = noticeContents;
        this.noticeWriter = noticeWriter;
        this.fileGroupId = fileGroupId;
        this.isFixed = isFixed;
    }

    public void NoticeUpdate(NoticeRequest dto){
        this.noticeContents = dto.noticeContents();
        this.noticeGroup = dto.noticeGroup();
        this.isFixed = dto.isFixed();
        this.noticeTitle = dto.noticeTitle();
    }

    public void addAttach(Attach attachFile){
        this.attachList.add(attachFile);
        if(attachFile.getNoticeBoard()!=this){
            attachFile.setNoticeBoard(this);
        }
    }
}
