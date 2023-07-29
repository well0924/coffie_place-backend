//지도 출력
$(document).ready(function(){
    nearPLaceList();
});

const container = document.getElementById('map');
// 마커 이미지의 이미지 주소입니다
var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";

let placeLang= $('#memberLongitude').val();
let placeLat= $('#memberLatitude').val();

//지도의 중심좌표
const options = {
    //입력을 받은 위경도를 넣는다.->가게 위경도
    center: new kakao.maps.LatLng(placeLang,placeLat),
    level: 3//지도의 확대레벨
};

//지도를 생성
let map = new kakao.maps.Map(container, options);
//
function nearPLaceList() {
    $.ajax({
        url:'/api/mypage/nearlist',
        type: 'get',
        dataType:"json",
        contentType:"application/json; charset=utf-8"
    }).done(function(resp){
        for(var i = 0; i<resp.size;i++){
            console.log(resp.data[i]);
        }
    });
}

