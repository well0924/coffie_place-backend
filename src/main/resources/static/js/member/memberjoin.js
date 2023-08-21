/*
*  회원가입 페이지
*
*/
//아이디 중복 검사 o.k
function idcheck(){
    let id = $('#user_id').val();

    $.ajax({
        url:'/api/member/id-check/'+id,
        type:'get',
        dataType:'json',
        contentType:"application/json; charset=UTF-8"
    }).done(function(resp){
        if(resp.data === true){
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

    if(pwd.trim() === pwdcheck.trim()){
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

//이메일 중복체크
function emailCheck(){
    let email = $('#user_email').val();
    console.log(email);
    $.ajax({
        url:'/api/member/email-check/'+email,
        type:'GET',
        dataType:'json',
        contentType:"application/json; charset=UTF-8"
    }).done(function (resp){
        if(resp.data == true){
            document.getElementById('emailExists').innerHTML = '</br>이메일이 중복!';
            document.getElementById('emailExists').style.color='red';
        }else{
            document.getElementById('emailExists').innerHTML = '</br>사용가능한 이메일입니다.';
            document.getElementById('emailExists').style.color='blue';
        }
    });
}

//kakao map api
var mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new daum.maps.LatLng(37.537187, 127.005476), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };
//지도를 미리 생성
var map = new daum.maps.Map(mapContainer, mapOption);
//주소-좌표 변환 객체를 생성
var geocoder = new daum.maps.services.Geocoder();
//마커를 미리 생성
var marker = new daum.maps.Marker({position: new daum.maps.LatLng(37.537187, 127.005476), map: map}),
    infowindow = new kakao.maps.InfoWindow({zindex:1});

//주소api기능o.k
function AddressCode(){
    new daum.Postcode({
        oncomplete: function(data) {
            //주소
            let extraName = '';
            let addr = ''+ extraName;

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
            //주소1 부분에 주소의 값을 넣기.
            document.getElementById("signUpUserPostNo").value = addr;
            console.log(addr);
            console.log(data);
            //daum api의 주소값을 토대로 카카오 api에서
            let userAddr = data.address; // 최종 주소 변수

            // 주소 정보를 해당 필드에 넣는다.
            document.getElementById("map").value = userAddr;
            // 주소로 상세 정보를 검색
            geocoder.addressSearch(data.address, function(results, status) {
                // 정상적으로 검색이 완료됐으면
                if (status === daum.maps.services.Status.OK) {

                    var result = results[0]; //첫번째 결과의 값을 활용

                    // 해당 주소에 대한 좌표를 받아서
                    var coords = new daum.maps.LatLng(result.y, result.x);
                    // 지도를 보여준다.
                    mapContainer.style.display = "block";
                    map.relayout();
                    // 지도 중심을 변경한다.
                    map.setCenter(coords);
                    // 마커를 결과값으로 받은 위치로 옮긴다.
                    marker.setPosition(coords);
                    //위경도 값을 로컬 스토리지에 저장을 한다.
                    var lat = coords.La;
                    var lng = coords.Ma;
                    localStorage.setItem('memberLat',lat);
                    localStorage.setItem('memberLng',lng);
                    console.log(result);
                }
            });
        }
    }).open();
}

// 지도를 클릭했을 때 클릭 위치 좌표에 대한 주소정보를 표시하도록 이벤트를 등록합니다
kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
    searchDetailAddrFromCoords(mouseEvent.latLng, function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            var detailAddr = !!result[0].road_address ? '<div>도로명주소 : ' + result[0].road_address.address_name + '</div>' : '';
            detailAddr += '<div>지번 주소 : ' + result[0].address.address_name + '</div>';

            var content = '<div class="bAddr">' +
                '<span class="title">법정동 주소정보</span>' +
                detailAddr +
                '</div>';

            // 마커를 클릭한 위치에 표시합니다
            marker.setPosition(mouseEvent.latLng);
            marker.setMap(map);

            // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
            infowindow.setContent(content);
            infowindow.open(map, marker);
        }
    });
});

// 중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록합니다
kakao.maps.event.addListener(map, 'idle', function() {
    searchAddrFromCoords(map.getCenter(), displayCenterInfo);
});

function searchAddrFromCoords(coords, callback) {
    // 좌표로 행정동 주소 정보를 요청합니다
    geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
}

function searchDetailAddrFromCoords(coords, callback) {
    // 좌표로 법정동 상세 주소 정보를 요청합니다
    geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
}

function displayCenterInfo(result, status) {
    if (status === kakao.maps.services.Status.OK) {
        var infoDiv = document.getElementById('centerAddr');

        for(var i = 0; i < result.length; i++) {
            // 행정동의 region_type 값은 'H' 이므로
            if (result[i].region_type === 'H') {
                infoDiv.innerHTML = result[i].address_name;
                break;
            }
        }
    }
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
    let memberLat = Number(localStorage.getItem('memberLat'));
    let memberLng = Number(localStorage.getItem('memberLng'));

    console.log(memberLat);
    console.log(memberLng);

    const dateForm={
        userId :id,
        password :pw,
        memberName : name,
        userAge : age,
        userGender : gender,
        userEmail : email,
        userPhone : phone,
        userAddr1 : addr1,
        userAddr2 : addr2,
        memberLng : memberLng,
        memberLat : memberLat
    };

    $.ajax({
        url:'/api/member/join',
        type:'post',
        data: JSON.stringify(dateForm),
        dataType:'json',
        contentType:'application/json; charset=utf-8'
    }).done(function(resp){
        console.log(resp);
        if(resp.status ==200){
            alert("회원 가입이 되었습니다.");
            location.href='/page/login/loginPage';
        }
        if(resp.status ==400){
            if(resp.data.hasOwnProperty('valid_userId')){
                $('#valid_userId').text(resp.data.valid_userId).css('color','red');
            }else{
                $('#valid_userId').text('');
            }
            if(resp.data.hasOwnProperty('valid_password')){
                $('#validation_check').text(resp.data.valid_password).css('color','red');
            }else{
                $('#validation_check').text('');
            }
            if(resp.data.hasOwnProperty('valid_memberName')){
                $('#valid_memberName').text(resp.data.valid_memberName).css('color','red');
            }else{
                $('#valid_memberName').text('');
            }
            if(resp.data.hasOwnProperty('valid_userGender')){
                $('#valid_userGender').text(resp.data.valid_userGender).css('color','red');
            }else{
                $('#valid_userGender').text('');
            }
            if(resp.data.hasOwnProperty('valid_userEmail')){
                $('#valid_userEmail').text(resp.data.valid_userEmail).css('color','red');
            }else{
                $('#valid_userEmail').text('');
            }
            if(resp.data.hasOwnProperty('valid_userPhone')){
                $('#valid_userPhone').text(resp.data.valid_userPhone).css('color','red');
            }else{
                $('#valid_userPhone').text('');
            }
            if(resp.data.hasOwnProperty('valid_userAddr1')){
                $('#valid_userAddr1').text(resp.data.valid_userAddr1).css('color','red');
            }else{
                $('#valid_userAddr1').text('');
            }
            if(resp.data.hasOwnProperty('valid_userAge')){
                $('#valid_userAge').text(resp.data.valid_userAge).css('color','red');
            }else{
                $('#valid_userAge').text('');
            }
        }
    });
}
