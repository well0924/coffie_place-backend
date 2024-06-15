package com.example.coffies_vol_02.testBoard;

import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.like.service.LikeService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class BoardRedisTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Disabled
    @DisplayName("자유 게시글 조회수 증가(동시성 테스트)")
    public void BoardReadCountUpTest() throws InterruptedException {
        //100명이 동시에 조회수를 증가
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(35);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int i =0; i< numberOfThreads; i++){
            executorService.submit(()->{
                try{
                    boardService.findFreeBoard(15);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    @Test
    @DisplayName("자유게시글 좋아요 증가 테스트")
    public void BoardLikeCountUpTest()throws Exception{

        Optional<Member>member = memberRepository.findById(1);

        //100명이 동시에 조회수를 증가
        int numberOfThreads = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(35);

        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i=0;i<numberOfThreads;i++){
            executorService.submit(()->{
                try{
                    likeService.boardLikePlus(15,member.get().getId());
                }catch (Exception e){
                    e.getMessage();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}
