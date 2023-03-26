$(document).ready(function() {
    $('#autocompleteText').autocomplete({
        source: function (request, response) {
            $.ajax({
                url: '/api/member/autocompetekeyword',
                type: 'get',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: {searchValue: request.term}
            }).always(function (result) {
                console.log(result);
                response(
                    $.map(result, function (item) {
                        return {
                            label: item.data,
                            value: item.data
                        };
                    })
                );
            });
        },
        select: function (event, ui) {
            console.log(ui);
            console.log(ui.item.data);
        },focus:function (event,ui) {
            return false;
        },
        minLength: 1
    });
});
function allChecked(target){
    if($(target).is(":checked")){
        $(".chk").prop("checked",true);
    }else{
        $(".chk").prop("checked",false);
    }
}
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
function cafelist(){
    location.href='/page/place/placelist';
}
function noticewrite(){
    location.href='/page/notice/noticeinsert';
}
function palceregister(){
    location.href='/page/place/placeregister';
}