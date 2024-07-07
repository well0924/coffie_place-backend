function deleteWish(){
    const isConfirm = confirm("위시리스트를 삭제하시겠습니까?");

    let favoriteId = $('#favoriteId').val();
    //로그인한 회원 아이디
    let userId = $('#userId').val();

    if(isConfirm){

        $.ajax({
            url:'/api/my-page/'+favoriteId,
            type:'delete',
            dataType:'json',
            data:null,
            contentType:'application/json; charset = utf-8'
        }).done(function(){
            alert('삭제되었습니다.');
            location.href="/page/mypage/page/"+userId;
        });
    }
}

//회원탈뢰
function memberDelete(){
    let id = $('#user_id').val();

    $.ajax({
        url:'/api/member/delete/'+id,
        type:'delete',
        data:null
    }).done(function(resp){
        alert('탈퇴했습니다.');
        location.href='/page/main/main';
    });
    return false;
}
