package com.example.coffies_vol_02.Board.controller.view;

import com.example.coffies_vol_02.Board.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class BoardViewController {
    private final BoardService boardService;

}
