/*
* 공지게시판 조회 화면
*/
function noticeupdate(){
    let id = $('#noticeid').val();
    location.href='/page/notice/noticemodify/'+id
}