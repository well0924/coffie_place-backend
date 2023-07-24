package com.example.coffies_vol_02.Factory;

import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;

public class NoticeFactory {

    public static NoticeBoard noticeBoard(){
        return NoticeBoard
                .builder()
                .id(1)
                .noticeWriter(MemberFactory.memberDto().getUserId())
                .noticeTitle("title")
                .noticeGroup("공지게시판")
                .noticeContents("내용")
                .isFixed('Y')
                .fileGroupId("notice_few3432")
                .build();
    }
    public static NoticeRequest noticeRequest(){
        return new NoticeRequest(
                noticeBoard().getNoticeGroup(),
                noticeBoard().getIsFixed(),
                noticeBoard().getNoticeTitle(),
                noticeBoard().getNoticeWriter(),
                noticeBoard().getNoticeContents(),
                noticeBoard().getFileGroupId());
    }
    public static NoticeResponse response(){
        return new NoticeResponse(noticeBoard());
    }
}
