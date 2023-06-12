package com.example.coffies_vol_02.board.domain.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.coffies_vol_02.board.domain.dto.response.QBoardResponse is a Querydsl Projection type for BoardResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QBoardResponse extends ConstructorExpression<BoardResponse> {

    private static final long serialVersionUID = 1153247536L;

    public QBoardResponse(com.querydsl.core.types.Expression<? extends com.example.coffies_vol_02.board.domain.Board> board) {
        super(BoardResponse.class, new Class<?>[]{com.example.coffies_vol_02.board.domain.Board.class}, board);
    }

}

