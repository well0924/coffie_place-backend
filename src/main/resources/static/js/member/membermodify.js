function adminlist(){
    location.href="/page/admin/adminlist";
}
function mylist(){
    let id = $('#user_id').val();
    location.href="/page/mypage/page/"+id;
}
function validation(){
    let pw = $('#user_pw').val();
    let name = $('#user_name').val();
    let age = $('#user_age').val();
    let email = $('#user_email').val();
    let phone = $('#user_phone').val();
    let gender = $('input[name=userGender]:checked').val();
    let addr1 = $('#user_addr1').val();
    let addr2 = $('#user_addr2').val();

    if(id.trim().length ==0){
        alert('아이디를 입력해주세요.');
        return false;
    }
    if(pw.trim().length ==0){
        alert('비밀번호를 입력해주세요.');
        return false;
    }
    if(name.trim().length ==0){
        alert('이름을 입력해주세요.');
        return false;
    }
    if(age.trim().length ==0){
        alert('나이를 입력해주세요.');
        return false;
    }
    if(email.trim().length ==0){
        alert('이메일을 입력해주세요.');
        return false;
    }
    if(phone.trim().length ==0){
        alert('전화번호를 입력해주세요.');
        return false;
    }
    if(gender.trim().length ==0){
        alert('성별을 체크해주세요.');
        return false;
    }
    if(addr1.trim().length ==0){
        alert('주소를 입력해주세요.');
        return false;
    }
    return true;
}

function checkvalid(chk){
    let gendercheck = document.getElementsByName('userGender');
    for(let i=0; i<gendercheck.length; i++){
        if(gendercheck[i] != chk){
            gendercheck[i].checked = false;
        }
    }
}
function AddressCode(){
    new daum.Postcode({
        oncomplete: function(data) {
            //주소
            let extraName = '';
            let addr = ''+extraName;

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraName += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraName += (extraName !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraName !== ''){
                    extraName = ' (' + extraName + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("user_addr1").value = extraName;
            } else {
                document.getElementById("user_addr1").value = '';
            }

            document.getElementById("user_addr1").value = addr;
            console.log(addr);
        }
    }).open();
}

function memberupdate(){
    let no =$('#user_no').val();
    let id = $('#user_id').val();
    let pw = $('#user_pw').val();
    let name = $('#user_name').val();
    let age = $('#user_age').val();
    let email = $('#user_email').val();
    let phone = $('#user_phone').val();
    let gender = $('input[name=userGender]:checked').val();
    let addr1 = $('#user_addr1').val();
    let addr2 = $('#user_addr2').val();

    const form = {
        id : no,
        userId : id,
        password : pw,
        memberName : name,
        userAge : age,
        userEmail : email,
        userPhone : phone,
        userGender : gender,
        userAddr1 : addr1,
        userAddr2 : addr2
    }

    $.ajax({
        url:'/api/admin/update/'+id,
        type:'put',
        data:JSON.stringify(form),
        dataType:'json',
        contentType:'application/json; charset=utf-8'
    }).done(function(resp){
        console.log(resp);
        if(resp.data == 200){
            alert('정보가 수정 되었습니다.');
            location.href="/page/admin/adminlist";
        }
        if(resp.data == 400){

        }
    });
}
function memberdelete(){
    let id = $('#user_id').val();

    const isConfirm = confirm('회원을 삭제하겠습니까?');

    if(isConfirm){
        $.ajax({
            url:'/api/admin/delete/'+id,
            type:'delete',
            dataType:'json',
            data:null
        }).done(function(resp){
            console.log(resp);
            alert('삭제되었습니다.');
            location.href="/page/admin/adminlist";
        });
    }
}
