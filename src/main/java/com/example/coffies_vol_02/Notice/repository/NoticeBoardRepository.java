package com.example.coffies_vol_02.Notice.repository;

import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeBoardRepository extends JpaRepository<NoticeBoard,Integer> {
}
