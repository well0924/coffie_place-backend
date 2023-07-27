//지도 출력
const container = document.getElementById('map');

let placeLang= $('#memberLongitude').val();
let placeLat= $('#memberLatitude').val();

console.log(placeLang);
console.log(placeLat);

//지도의 중심좌표
const options = {
    //입력을 받은 위경도를 넣는다.->가게 위경도
    center: new kakao.maps.LatLng(placeLang,placeLat),
    level: 1//지도의 확대레벨
};
//지도를 생성
let map = new kakao.maps.Map(container, options);
