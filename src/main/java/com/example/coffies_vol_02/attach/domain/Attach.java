package com.example.coffies_vol_02.attach.domain;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@ToString
@Proxy(lazy = false)
@Entity
@Table(name="tbl_file")
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "id"))})
public class Attach extends BaseTime implements Serializable {

    private Integer id;
    @Column(nullable = false)
    private String originFileName; //원본 파일명
    @Column(nullable = false)
    private String filePath;  // 파일 저장 경로
    private Long fileSize; // 파일 크기

    //게시판
    @Setter
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private Board board;

    //공지 게시판
    @Setter
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "notice_id")
    private NoticeBoard noticeBoard;

    @Builder
    public Attach(Integer id,String originFileName, String filePath, Long fileSize,Board board,NoticeBoard noticeBoard){
        this.id = id;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.board = board;
        this.noticeBoard = noticeBoard;
    }

    @Builder
    public Attach(Integer id,String originFileName, String filePath, Long fileSize){
        this.id = id;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
     }

    @Builder
    public Attach(Integer id,String originFileName, String filePath, Long fileSize,NoticeBoard noticeBoard){
        this.id = id;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.noticeBoard = noticeBoard;
    }

    @Builder
    public Attach(Integer id,String originFileName, String filePath, Long fileSize,Board board){
        this.id = id;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.board = board;
    }

    @Builder
    public Attach(String originFileName, String filePath, Long fileSize){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }


    //첨부파일 관련 메서드
    public void setBoard(Board board){
        this.board = board;
        // 게시글에 현재 파일이 존재하지 않는다면
        if(!board.getAttachList().contains(this)){
            // 파일 추가
            board.getAttachList().add(this);
        }
    }

    public void setNoticeBoard(NoticeBoard noticeBoard){
        this.noticeBoard = noticeBoard;
        if(noticeBoard.getAttachList().contains(this)){
            noticeBoard.getAttachList().add(this);
        }
    }
}
