package com.example.coffies_vol_02.Board.controller.api;

import com.example.coffies_vol_02.Board.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

}
