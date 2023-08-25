$(document).ready(function(){
    likeCount();
});
let filegroupid = $('#fileGroupId').val();
let pwd = $('#pwd').val();

console.log(pwd);

//비밀번호화면 이동 페이지o.k
function pwdCheck(){
    let pwd = $('#pwd').val();
    let id = $('#board_id').val();
    //비밀번호를 입력하지 않았을 경우 또는 비밀번호가 null인 경우
    if(pwd.trim().length == 0){

        location.href = '/page/board/modify/'+id;
        return false;
    }else if(pwd != null){
        location.href='/page/board/passwordCheck/'+id;
        return false;
    }
    return true;
}

//게시판 좋아요 카운팅
function likeCount(){
    let str = '';
    let id = $('#board_id').val();
    $.ajax({
        url:"/api/like/board/"+id,
        type:"get",
        dataType:"json",
        contentType:"application/json; charset=utf-8"
    }).done(function (resp) {
        if(resp.data[0]!=null){
            if(resp.data[1]==false){
                str='<i class="fa-sharp fa-solid fa-thumbs-up">'+resp.data[0]+'</i>'
            }
            if(resp.data[1]!=false){
                str='<i class="fa-sharp fa-solid fa-thumbs-up">'+resp.data[0]+'</i>'
            }
        }
        $('#favoriteCount').html(str);
    });
}
//게시판 좋아요 추가하기.
function boardLike(){
    console.log("좋아요 추가");
    let id = $('#board_id').val();
    $.ajax({
        url:"/api/like/plus/"+id,
        type: 'post',
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(id)
    }).done(function(resp){
        alert(resp.message);
        $('#favoriteCount').empty();
        likeCount();
    });
}
//좋아요 감소
function likeCancel(){
    let id = $('#board_id').val();

    $.ajax({
        url:'/api/like/minus/'+id,
        type:'delete',
        dataType:'json',
        contentType:'application/json; charset=utf-8'
    }).done(function(resp){
        alert(resp.message);
        $('#favoriteCount').empty();
        likeCount();
    });
}