//비밀번호체크
function validation(){

    let input = $('#inputnumber').val();
    console.log(pw);

    if(input.trim().length==0){
        alert('비밀번호를 입력해주세요.');
        return false;
    }
    return true;
}

function pwdCheck(){
    let id = $('#boardid').val();
    let pwd = $('#inputnumber').val();

    if(validation()){
        $.ajax({
            type: 'get',
            url:'/api/board/password/'+id+'/'+pwd,
            dataType:'json',
            contentType : 'application/json; charset=utf-8'
        }).done(function(result){
            //성공을 했을시 조회화면으로 이동.
            alert('이동');
            location.href = '/page/board/modify/'+id;
            console.log(result.passWd == pw);
        }).fail(function(fail){
            alert('비밀번호가 맞지 않습니다.');
        });
    }
}
