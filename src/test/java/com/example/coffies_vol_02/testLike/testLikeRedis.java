package com.example.coffies_vol_02.testLike;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.like.service.LikeService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
@SpringBootTest
public class testLikeRedis {

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    private Comment comment;
    private Member member;

    @BeforeEach
    public void set(){
        Optional<Member>memberDetail = memberRepository.findById(1);
        Optional<Comment>commentDetail = commentRepository.findById(1);
        member = memberDetail.get();
        comment = commentDetail.get();
    }
    
    @Test
    @DisplayName("가게 댓글 좋아요 증가 테스트")
    public void placeCommentLikePlusTest() throws InterruptedException {

        final int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    likeService.commentLikePlus(comment.getId(),member);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);

        Comment updatedComment = commentRepository.findById(comment.getId()).orElseThrow();
        System.out.println(updatedComment.getLikes().size());
    }
    
    @Test
    @DisplayName("가게 댓글 좋아요 감소 테스트")
    public void placeCommentLikeMinusTest() throws InterruptedException {

        for (int i = 0; i < 1; i++) {
            likeService.commentLikePlus(comment.getId(), member);
        }

        final int threadCount = 1;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        log.info("감소 로직 시작.");

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    likeService.commentLikeMinus(comment.getId(), member);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);

        CommentLike commentLike = commentLikeRepository.findByCommentId(comment.getId());
        System.out.println(commentLike);
    }
}
