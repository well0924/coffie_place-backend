package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.favoritePlace.domain.dto.recentPostDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {

    private final BoardRepository boardRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<Object, Object> redisTemplate;

    private final RedisTemplate<String,recentPostDto>postDtoRedisTemplate;

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    // 가게 key word 를 위한 setValues
    public void setValues(String key, String keyword) {
        ListOperations<Object, Object> listOperations = redisTemplate.opsForList();
        for (Object pastKeyword : Objects.requireNonNull(listOperations.range(key, 0, listOperations.size(key)))) {
            if (String.valueOf(pastKeyword).equals(keyword)) return;
        }
        if (listOperations.size(key) < 5) {
            //키워드를 저장(최대 5개 까지)
            listOperations.rightPush(key, keyword);
        } else if (listOperations.size(key) == 5) {
            listOperations.leftPop(key);
            listOperations.rightPush(key, keyword);
            //최근 검색어 TTL 설정
            redisTemplate.expireAt(key,Date.from(ZonedDateTime.now().plusDays(1).toInstant()));
        }
    }

    //redis 검색어 목록
    public List<String> getSearchList(String key) {
        ListOperations listOperations = redisTemplate.opsForList();
        return listOperations.range(key, 0, listOperations.size(key));
    }

    //redis에서 값 제거
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    //redis에 저장된 값 가져오기.
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    //조회수 증가
    public void increasement(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.increment(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    //게시글 조회수
    public void boardViewCount(Integer boardId){
        String countKey = CacheKey.BOARD+"viewCount"+"::"+boardId;

        if(getData(countKey)==null){//조회가 처음이면
            setValues(countKey,String.valueOf(boardRepository.ReadCount(boardId)+1), Duration.ofMinutes(CacheKey.BOARD_EXPIRE_SEC));
        }else{//2번 이상이면 조회수를 증가.
            increasement(countKey);
        }
    }
    
    //게시글 조회수 디비에 반영
    @Scheduled(cron = "0/10 * * * * ?",zone = "Asia/Seoul")
    public void boardViewCountDB(){
        Set<String>viewKeys = keys("boardviewCount*");

        log.info("boardViewCount::"+viewKeys);

        if(Objects.requireNonNull(viewKeys).isEmpty())return;

        for(String viewKey : viewKeys){

            Integer boardId = Integer.parseInt(viewKey.split("::")[1]);
            Integer viewCount = Integer.parseInt(getData(viewKey));

            log.info("게시글 번호:"+boardId);
            log.info("조회수:"+viewCount);

            boardRepository.ReadCountUpToDB(boardId,viewCount);
            //캐시 삭제
            deleteValues(viewKey);
            deleteValues(CacheKey.BOARD+"viewCount"+"::"+boardId);
        }
    }

    //최근에 자유게시판에서 읽은 글 저장
    /**
     * 회원이 최근에 읽은 글 저장
     *
     **/
    public void recentPostSave(Integer userIdx,Integer boardId){
        ListOperations<String, recentPostDto>listOperations = postDtoRedisTemplate.opsForList();
        //자유 게시글 조회
        Optional<Board>detailBoard = Optional.ofNullable(boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board board = detailBoard.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

        recentPostDto recentDto = new recentPostDto(board);
        //redis에 저장할 key값 저장
        String recentKey = "userIdx::"+userIdx;
        //list에 넣기.
        listOperations.leftPush(recentKey,recentDto);
        //유효기간 정하기.(일주일)
        redisTemplate.expireAt(recentKey, Date.from(ZonedDateTime.now().plusDays(7).toInstant()));
    }
    //최근에 자유게시판에서 읽은 글 조회
    public List<recentPostDto>recentPostDtoList(Integer userIdx){
        ListOperations<String,recentPostDto>listOperations = postDtoRedisTemplate.opsForList();
        String recentKey = "userIdx::"+userIdx;
        Integer size = Math.toIntExact(listOperations.size(recentKey) == null ? 0 : listOperations.size(recentKey));

        return listOperations.range(recentKey,0,size);
    }
}
