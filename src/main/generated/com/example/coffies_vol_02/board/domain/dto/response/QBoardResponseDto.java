package com.example.coffies_vol_02.board.domain.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.coffies_vol_02.board.domain.dto.response.QBoardResponseDto is a Querydsl Projection type for BoardResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QBoardResponseDto extends ConstructorExpression<BoardResponseDto> {

    private static final long serialVersionUID = 954013327L;

    public QBoardResponseDto(com.querydsl.core.types.Expression<? extends com.example.coffies_vol_02.board.domain.Board> board) {
        super(BoardResponseDto.class, new Class<?>[]{com.example.coffies_vol_02.board.domain.Board.class}, board);
    }

}

