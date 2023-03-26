
//검색 기능o.k
$(document).on('click', '#btnSearch', function(e){
    var url ="/page/admin/adminlist?"+"page="+[[${paging.cri.page}]]+"&perPageNum="+[[${paging.cri.perPageNum}]]+"&searchType="+$('#searchType').val()+"&keyword="+$('input[name=keyword]').val();
    location.href = url;
});

//체크 박스 전체 선택 클릭 이벤트 o.k
function allChecked(target){
    if($(target).is(":checked")){
        $(".chk").prop("checked",true);
    }else{
        $(".chk").prop("checked",false);
    }
}

//체크박스 클릭 이벤트 o.k
function cchkClicked(){
    //체크박스 전체개수
    var allCount = $("input:checkbox[name=cchk]").length;
    //체크된 체크박스 전체개수
    var checkedCount = $("input:checkbox[name=cchk]:checked").length;
    //체크박스 전체개수와 체크된 체크박스 전체개수가 같으면 체크박스 전체 체크
    if(allCount == checkedCount){
        $(".chk").prop("checked",true);
    }else{
        $("#allCheckBox").prop("checked",false);
    }
}

//선택 삭제 기능o.k
function selectDelete(){
    var MemberArray = [];
    $("input:checkbox[name=cchk]:checked").each(function(){
        MemberArray.push($(this).val());
        console.log(MemberArray);
    });

    if(MemberArray == ""){
        alert("삭제할 항목을 선택해주세요.");
        return false;
    }
    var confirmAlert = confirm('정말로 삭제하시겠습니까?');

    if(confirmAlert){
        $.ajax({
            url:'/api/admin/selectdelete',
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

//가게 목록으로 이동ok
function cafelist(){
    location.href='/page/place/placelist';
}

//공지글 작성 페이지 이동o.k
function noticewrite(){
    location.href='/page/notice/noticeinsert';
}

//가게등록 페이지 이동o.k
function palceregister(){
    location.href='/page/place/placeregister';
}

//회원자동완성.o.k
$('#autocomplete').autocomplete({
    source : function(request,response){

        $.ajax({
            url:'/api/admin/autocompetekeyword',
            type:'post',
            dataType:'json',
            data:{value: request.term}

        }).done(function(data){

            console.log(data);

            response(

                $.map(data.resultList,function(item){

                    return{

                        label : item.user_id,

                        value : item.user_id
                    };
                })
            );
        }).fail(function(error){
            alert('오류가 발생했습니다.');
        });
    }

    ,focus: function(event,ui){
        return false;
    },
    minLength: 1,
    delay: 100,
    select : function(evt,ui){
        console.log(ui.item.label);
        console.log(ui.item.value);
    }
});