$(document).ready(function() {

    $('#autocompleteText').autocomplete({
        source: function (request, response) {
            $.ajax({
                url: '/api/member/autocomplete',
                type: 'get',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: {userId : request.term}
            }).done(function (result) {
                console.log(result.data);
                response(
                    $.map(result.data, function (item) {
                        console.log(item);
                        return {label: item, value: item};
                    })
                );
            });
        },
        select: function (event, ui) {
            console.log(ui);
            console.log(ui.item.data);
        },
        focus:function (event,ui) {
            return false;
        },minLength: 1
    });
});

//체크박스 전부 선택
function allChecked(target){
    if($(target).is(":checked")){
        $(".chk").prop("checked",true);
    }else{
        $(".chk").prop("checked",false);
    }
}

//회원 체크박스 기능
function cchkClicked(){

    //체크박스 전체개수
    const allCount = $("input:checkbox[name=cchk]").length;
    //체크된 체크박스 전체개수
    const checkedCount = $("input:checkbox[name=cchk]:checked").length;

    //체크박스 전체개수와 체크된 체크박스 전체개수가 같으면 체크박스 전체 체크
    if(allCount == checkedCount){
        $(".chk").prop("checked",true);
    }else{
        $("#allCheckBox").prop("checked",false);
    }
}
//회원 선택삭제
function selectDelete(){

    let MemberArray = [];

    $("input:checkbox[name=cchk]:checked").each(function(){
        MemberArray.push($(this).val());
        console.log(MemberArray);
    });

    if(MemberArray == ""){
        alert("삭제할 항목을 선택해주세요.");
        return false;
    }

    const confirmAlert = confirm('정말로 삭제하시겠습니까?');

    if(confirmAlert){
        $.ajax({
            url:'/api/member/select-delete',
            type:'post',
            dataType:'json',
            data:JSON.stringify(MemberArray),
            contentType:'application/json'
        }).done(function(resp){
            alert("삭제되었습니다.");
            location.href="/page/admin/adminlist";
        }).fail(function(err){
            console.log(err);
        });
    }
}

//회원 검색기능
function searchResult(){
    let searchVal = $('#autocompleteText').val();
    let searchType = $('#searchType').val();
    location.href='/page/admin/adminlist?searchType='+searchType+'&searchVal='+searchVal;
}
//가게 목록으로 이동
function cafelist(){
    location.href='/page/place/list';
}
//공지게시글 이동
function noticewrite(){
    location.href='/page/notice/writePage';
}
//가게등록으로 이동
function palceregister(){
    location.href='/page/place/placeregister';
}