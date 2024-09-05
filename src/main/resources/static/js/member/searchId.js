/*
*  회원 아이디 찾기 페이지
*/

//name유효성 검사o.k
function validation(){
    let id = document.getElementById('user_id').value;
    let name = document.getElementById('user_name').value;

    if(id.trim().length==0){
        alert('아이디를 입력해주세요.');
        return false;
    }
    if(name.trim().length==0){
        alert('이름을 입력해주세요.');
        return false;
    }

    return true;
}

//이메일 형식 체크o.k
function validemail(){
    //이메일 입력값
    let useremail = document.getElementById('user_email').value;
    //이메일 양식 확인하는 정규식
    let regEmail = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;

    //형식검사
    if(useremail.trim().length == 0){
        alert('이메일을 입력해주세요.');
        return false;
    }

    if(!regEmail.test(useremail)){
        alert('이메일 형식이 아닙니다.');
        return false;
    }
    return true;
}

//아이디 찾기 o.k
function findId(){
    let name = $('#user_name').val();
    let email  = $('#user_email').val();
    if(validation()&&validemail()){
        $.ajax({
            url:'/api/member/find-id/'+name+'/'+email,
            type:'get',
            dataType:'json',
            contentType:'application/json; charset=UTF-8'
        }).always(function(resp){
            console.log(resp.data);

            if(resp.data != null){
                document.getElementById('msg').innerHTML = '</br> 찾으시는 아이디는'+resp.data+'입니다.';
                document.getElementById('msg').style.color='blue';
            }else{
                document.getElementById('msg').innerHTML = '</br>찾으시는 아이디가 없거나 이름을 잘못 입력하셨습니다.';
                document.getElementById('msg').style.color='red';
            }
        });
    }
}

function changepwd(){
    location.href='/page/login/pwd-change';
}