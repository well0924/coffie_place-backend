function deleteWish(){
    const isConfirm = confirm("위시리스트를 삭제하시겠습니까?");

    let favoriteid = $('#favoriteid').val();

    if(isConfirm){

        $.ajax({
            url:'/api/mypage/delete/'+favoriteid,
            type:'delete',
            dataType:'json',
            data:null,
            contentType:'application/json; charset = utf-8'
        }).done(function(resp){
            alert('삭제되었습니다.');
            location.href="/page/mypage/page/"+userid;
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
