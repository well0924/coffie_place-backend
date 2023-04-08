var filegroupid = $('#fileGroupId').val();
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