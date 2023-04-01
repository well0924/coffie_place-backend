package com.example.coffies_vol_02.Attach.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.config.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Attach extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String originFileName;
    @Column(nullable = false)
    private String filePath;  // 파일 저장 경로
    private Long fileSize;
    //게시판
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
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
}
