package com.example.coffies_vol_02.Notice.domain;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = {"attachList"})
@Table(name = "tbl_notice",
        indexes = {
        @Index(name = "notice_index1",columnList = "id"),
        @Index(name = "notice_index2",columnList = "noticeGroup"),
        @Index(name = "notice_index3",columnList = "isFixed"),
        @Index(name = "notice_index4",columnList = "noticeTitle")
})
@NoArgsConstructor
@AllArgsConstructor
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

    public void NoticeUpdate(NoticeBoardDto.BoardRequestDto dto){
        this.noticeContents = dto.getNoticeContents();
        this.noticeGroup = dto.getNoticeGroup();
        this.isFixed = dto.getIsFixed();
        this.noticeTitle = dto.getNoticeTitle();
    }

    public void addAttach(Attach attachFile){
        this.attachList.add(attachFile);
        if(attachFile.getNoticeBoard()!=this){
            attachFile.setNoticeBoard(this);
        }
    }
}
