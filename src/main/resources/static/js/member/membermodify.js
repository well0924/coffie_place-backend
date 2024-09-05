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

//성별 체크
function checkvalid(chk){
    let gendercheck = document.getElementsByName('userGender');
    for(let i=0; i<gendercheck.length; i++){
        if(gendercheck[i] != chk){
            gendercheck[i].checked = false;
        }
    }
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
                    localStorage.setItem('updateLat',lat);
                    localStorage.setItem('updateLng',lng);
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

//회원 정보 수정
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
    let memberLng= Number(localStorage.getItem('updateLng'));
    let memberLat= Number(localStorage.getItem('updateLat'));

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
        userAddr2 : addr2,
        memberLat : memberLat,
        memberLng : memberLng
    }

    $.ajax({
        url:'/api/member/'+id,
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

//회원 삭제
function memberdelete(){
    let id = $('#user_id').val();

    const isConfirm = confirm('회원을 삭제하겠습니까?');

    if(isConfirm){
        $.ajax({
            url:'/api/member/'+id,
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
