package com.example.coffies_vol_02.factory;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;

public class BoardFactory {

    public static Board board(){
        return Board
                .builder()
                .id(1)
                .boardTitle("test")
                .boardAuthor(MemberFactory.memberDto().getUserId())
                .boardContents("test!")
                .readCount(1)
                .passWd("132v")
                .fileGroupId("free_weft33")
                .member(MemberFactory.memberDto())
                .build();
    }

    public static BoardRequest boardRequestDto(){
        return new BoardRequest(
                board().getBoardTitle(),
                board().getBoardContents(),
                board().getMember().getUserId(),
                board().getReadCount(),
                board().getPassWd(),
                board().getFileGroupId()
        );
    }
    public static BoardResponse boardResponse(){
        return new BoardResponse(board());
    }
}
