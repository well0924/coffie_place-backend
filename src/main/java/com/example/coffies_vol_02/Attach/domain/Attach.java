package com.example.coffies_vol_02.Attach.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
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
public class Attach extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String originFileName; //원본 파일명
    @Column(nullable = false)
    private String filePath;  // 파일 저장 경로
    private Long fileSize; // 파일 크기

    //게시판
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    //공지 게시판
    @ManyToOne
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
