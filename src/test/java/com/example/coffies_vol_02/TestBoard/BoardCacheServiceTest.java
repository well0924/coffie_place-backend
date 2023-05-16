package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Config.Redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardCacheServiceTest {
    @InjectMocks
    private BoardService boardService;
    @Mock
    private RedisService redisService;
    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("redis에 조회수를 저장하는 테스트- 캐시가 비어있을 경우, insert가 수행")
    public void redisCountViewInsertTest(){
        Integer boardId = 1;

        given(redisService.getData(any())).willReturn(null);
        given(boardRepository.ReadCount(boardId)).willReturn(1);

        boardService.boardViewCount(boardId);

        verify(redisService,times(1)).setValues(any(),any(),any());
        then(boardRepository).should().ReadCount(any());
    }

    @Test
    @DisplayName("redis에 값이 저장이 되면 조회수를 increasement한다.")
    public void redisCountIncreaseTest(){
        Integer boardId = 1;
        String viewCount = "3";

        given(redisService.getData(any())).willReturn(viewCount);

        boardService.boardViewCount(boardId);

        verify(redisService).increasement(any());
    }

    @Test
    @DisplayName("redis에 저장된 조회수를 db에 저장하기.")
    public void redisDBInsertTest(){
        String cacheKey = "boardViewCount::1";
        String cacheValue = "5";

        given(redisService.keys(any())).willReturn(Set.of(cacheKey));
        given(redisService.getData(any())).willReturn(cacheValue);

        //when
        boardService.boardViewCountDB();

        //then
        verify(boardRepository).ReadCountUpToDB(1, Integer.parseInt(cacheValue));
        //캐시 삭제시 조회수 및 캐시 키 삭제
        verify(redisService,times(2)).deleteValues(any());
    }

}
