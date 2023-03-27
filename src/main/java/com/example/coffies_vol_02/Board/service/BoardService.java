package com.example.coffies_vol_02.Board.service;

import com.example.coffies_vol_02.Board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    
}
