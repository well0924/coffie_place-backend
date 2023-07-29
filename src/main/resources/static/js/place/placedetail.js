$(document).ready(function(){
    ReviewList();
    resizeMap();
});
//지도 부분
//1.화면에서 필요한 값을 가져온다.
//2.지도를 생성한다.
//3.마커의 이미지를 가져온다.
//4.마커를 생성한다.

//에러:kakao.maps.size is not a constructor4
//내용:LatLng이라는 메서드는 아직 로딩이 끝나지 않아서 존재하지 않는데
//	  생성자 함수(constructor)로 사용하려고 하기 때문에 발생하는 에러...

//지도를 생성할 div
var placeLat = $('#placelat').val();
var placeLang = $('#placelng').val();
//지도의 중심좌표
var container = document.getElementById('map'),
    options = {
    //입력을 받은 위경도를 넣는다.->가게 위경도
    center: new kakao.maps.LatLng(placeLang,placeLat),
    level: 1//지도의 확대레벨
};
//지도를 생성
var map = new kakao.maps.Map(container, options);
//지도 리사이즈
function resizeMap(){
    var container = document.getElementById('map');
    container.style.width='500px';
    container.style.height='1000px';
}
//위치를 담은 마커를 생성한다.
var markerPosition = new kakao.maps.LatLng(placeLat,placeLang);

var marker = new kakao.maps.Marker({
    position : markerPosition
});

//맵에 표시한다.
marker.setMap(map);

//댓글 목록 o.k
function ReviewList(){
    let placeId = $('#placeid').val();

    $.ajax({
        url:'/api/comment/place/list/'+placeId,
        type:'get',
        dataType:'json'
    }).done(function(data){
        let str = '';
        let starDiv = '';
        let starScore;
        console.log(data.data);
        if(data.data.length<0){
            str +='<span>'+'댓글이 없습니다.'+'</span>';
        }else{
            for(let i =0; i<data.data.length;i++){
                starScore = getStarRange(data.data[i].reviewPoint);
                console.log(starScore);
                starDiv = '<div class="star-ratings">\n' +
                    '  <div  class="star-ratings-fill space-x-2 text-lg" style="width:' + starScore +'%">\n' +
                    '   <span>★</span><span>★</span><span>★</span><span>★</span><span>★</span>\n' +
                    '  </div>\n' +
                    '  <div class="star-ratings-base space-x-2 text-lg">\n' +
                    '    <span>★</span><span>★</span><span>★</span><span>★</span><span>★</span>\n' +
                    '  </div>\n' +
                    '</div>';
                str +='<div class="mb-3"  id="myform">';
                str += starDiv;
                str +='<div>';
                str +='<p id="reviewAuthor" name="replyWriter">'+data.data[i].replyWriter+'</p>';
                str += data.data[i].replyContents;
                str +='</br>';
                str +='<i class="fa-solid fa-heart" onclick="LikeCheck('+data.data[i].id+')">'+data.data[i].liked+'</i>';
                str +='</br>';
                str +='<button type="button" class="btn btn-dark mt-3" name="id" onclick="deleteReply('+data.data[i].id+','+placeId+')">'+'삭제'+'</button>';
                str +='</div>';
                str +='</div>';
            }
            $('#replylist').append(str);
        }
    });
}

//댓글 유효성 검사.o.k
function validation(){
    let comment = $('#reviewContents').val();

    if(comment.trim().length ==  0){
        alert('내용을 입력해 주세요.');
        return false;
    }
    return true;
}

//댓글 작성 o.k
function replyPost(){

    let placeId = $('#placeid').val();
    let point = $('input[name=replyPoint]:checked').val();
    let comment = $('#reviewContents').val();
    let createdby = $('#reviewAuthor').val();

    const formdate = {
        placeId : placeId,
        replyPoint : point,
        replyContents : comment,
        replyWriter : createdby
    }

    if(validation()){
        $.ajax({
            url:'/api/comment/place/write/'+placeId,
            type:'post',
            dataType:'json',
            data: JSON.stringify(formdate),
            contentType: 'application/json; chartset = utf-8'
        }).done(function(resp){
            alert('완료');

            console.log(resp);

            $('#reviewContents').val("");
            $('#reviewAuthor').val("");
            $('#replylist').empty();
            ReviewList();

        });
    }
}

//댓글 삭제 o.k
function deleteReply(replyId,placeId){
    $.ajax({
        url:'/api/comment/place/delete/'+placeId+'/'+replyId,
        type:'delete',
        dataType:'json'
    }).done(function(data){
        console.log(data);
        alert('삭제되었습니다.');
        $('#replylist').empty();
        ReviewList();
    });
}

//별점 계산o.k
function getStarRange(starScore) {
    console.log(starScore);
    let score = starScore * 20;
    console.log(score);
    return score + 1.5;
}

//위시리스트 확인기능 o.k
function WishCheck(){
    let userid = $('#userid').val();
    let placeId = $('#placeid').val();

    $.ajax({
        url:'/api/mypage/check/'+userid+'/'+placeId,
        type:'get',
        dataType:'json',
        contentType:'application/json; charset= utf-8'
    }).done(function(resp){
        console.log(resp.data);
        if(resp.data==false){
            alert("위시리스트에서 추가합니다.");
        }else {
            alert("이미 위시리스트에 추가되었습니다.");
        }
    });
    console.log(userid);
    console.log(placeId);
}

//댓글 좋아요 확인 기능
function LikeCheck(replyId){
    console.log('like checked');

    $.ajax({
        url:'/api/like/comment/' + replyId,
        type:'GET',
        dataType:'json',
        contentType:'application/json; charset= utf-8'
    }).done(function(resp){
        console.log(resp);
        console.log(resp.data[0]);
        console.log(resp.data[1]);
        if(resp.data[0]!=null){
            //처음 누르는 경우
            if(resp.data[1]== false){
                alert('좋아요가 추가 되었습니다.');
                console.log('좋아요 추가');
                //좋아요 추가 기능
                CommentLikePlus(replyId);
            }
            if(resp.data[1]==true){
                alert('좋아요를 취소했습니다');
                console.log('좋아요를 취소했습니다.');
                //좋아요 취소 기능
                CommentLikeMinus(replyId);
            }
        }
    });
}

//댓글 좋아요 추가 기능
function CommentLikePlus(replyId){
    let placeId = $('#placeid').val();

    const formdata = {placeId : placeId,replyId :replyId};

    $.ajax({
        url:'/api/like/plus/'+placeId+'/'+replyId,
        type:'post',
        data: JSON.stringify(formdata),
        dataType:'json',
        contentType:'application/json; charset= utf-8'
    }).done(function(resp){
        console.log(resp);
        console.log('좋아요 추가!');
        alert('좋아요 추가');
        $('#replylist').empty();
        ReviewList();

    }).fail(function (error){
        console.log(error);
        console.log('좋아요 실패!');
        $('#replylist').empty();
        ReviewList();

    });
}

//댓글 좋아요 취소 기능
function CommentLikeMinus(replyId){
    let placeId = $('#placeid').val();

    $.ajax({
        url:'/api/like/minus/'+placeId+'/'+replyId,
        type:'delete',
        dataType:'json'
    }).done(function(resp){
        console.log(resp);
        console.log('좋아요 감소');
        $('#replylist').empty();
        ReviewList();

    });
}

//이미지 팝업기능 o.k
function fnImgPop(url){
    let img = new Image();
    img.src =url;
    let name = "image popup";

    let option = "width = 500, height = 500, top = 100, left = 200, location = no"
    window.open(url, name, option);
}

//가게 수정 페이지 이동
function placemodify(){
    let placeId = $('#placeid').val();

    location.href='/page/place/placemodify/'+placeId;
}
