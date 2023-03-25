/*
*  회원가입 페이지
*
*/
//아이디 중복 검사 o.k
function idcheck(){
    let id = $('#user_id').val();

    $.ajax({
        url:'/api/member/idduplicated/'+id,
        type:'get',
        dataType:'json',
        contentType:"application/json; charset=UTF-8"
    }).done(function(resp){
        console.log(resp);
        if(resp == true){
            document.getElementById('msg').innerHTML = '</br>아이디가 중복!';
            document.getElementById('msg').style.color='red';
        }else{
            document.getElementById('msg').innerHTML = '</br>사용가능한 아이디입니다.';
            document.getElementById('msg').style.color='blue';
        }
    });
}

//비밀번호 재확인 o.k
function pwcheck(){
    let pwd = document.getElementById('user_pw').value;
    let pwdcheck = document.getElementById('user_pw_check').value;

    console.log(pwd);
    console.log(pwdcheck);

    if(pwd.trim() == pwdcheck.trim()){
        document.getElementById('pwcheck').innerHTML="비밀번호가 일치합니다.";
        document.getElementById('pwcheck').style.color='blue';
    }else{
        document.getElementById('pwcheck').innerHTML="비밀번호가 일치하지 않습니다.";
        document.getElementById('pwcheck').style.color='red';
    }
}

//성별체크박스 중복 o.k
function checkvalid(chk){
    let gendercheck = document.getElementsByName('userGender');
    for(let i=0; i<gendercheck.length; i++){
        if(gendercheck[i] != chk){
            gendercheck[i].checked = false;
        }
    }
}

//주소api기능o.k
function AddressCode(){
    new daum.Postcode({
        oncomplete: function(data) {
            //주소
            var extraName = '';
            var addr = ''+ extraName;

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
                document.getElementById("signUpUserPostNo").value = extraName;
            } else {
                document.getElementById("signUpUserPostNo").value = '';
            }

            document.getElementById("signUpUserPostNo").value = addr;
            console.log(addr);
        }
    }).open();
}
//회원가입 기능o.k
function memberjoin(){

    let id = $('#user_id').val();
    let pw = $('#user_pw').val();
    let name = $('#user_name').val();
    let age = $('#user_age').val();
    let gender = $('input[name=userGender]:checked').val();
    let email = $('#user_email').val();
    let phone = $('#user_phone').val();
    let addr1 = $('#signUpUserPostNo').val();
    let addr2 = $('#signUpUserAddress').val();

    const dateForm={

        userId :id,
        password :pw,
        memberName : name,
        userAge : age,
        userGender : gender,
        userEmail : email,
        userPhone : phone,
        userAddr1 : addr1,
        userAddr2 : addr2
    };

    $.ajax({
        url:'/api/member/memberjoin',
        type:'post',
        data: JSON.stringify(dateForm),
        dataType:'json',
        contentType:'application/json; charset=utf-8'
    }).always(function(resp){
        console.log(resp);
        alert("회원 가입이 되었습니다.");
        location.href='/page/login/loginPage';
    });
}
