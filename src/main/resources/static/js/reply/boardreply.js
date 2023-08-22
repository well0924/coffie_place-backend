function validation(){
    let writer = $('#writer').val();
    let contents = $('#contents').val();

    if(writer.trim().length == 0){
        alert('작성자를 입력해주세요.');
        return false;
    }

    if(contents.trim().length == 0){
        alert('내용을 입력해주세요.');
        return false;
    }
    return true;
}

//댓글목록
$(document).ready(function(){
    Replylist();
});

function Replylist(){
    let id = $('#board_id').val();

    $.ajax({
        url:"/api/comment/list/"+id,
        type:"GET",
        dataType:"json",
        contentType:"application/json; charset=utf-8"
    }).done(function(resp){
        let str = "";
        let count = resp.data.length;

        if(resp.data.length>0){
            for(let i = 0; i<resp.data.length;i++){
                str +='<div class="card mb-2">';
                str +='<div class="card-header bg-light">';
                str +='<i class="fa fa-comment fa"></i>';
                str +='</div>';
                str +='<div class="card-body">';
                str +='<ul class="list-group list-group-flush" id="replies">';
                str +='<li class="list-group-item" data-id="'+resp.data[i].id+'">';
                str +='<div class="form-inline mb-2">';
                str +='<label for="replyId"><i class="fa fa-user-circle-o fa-2x"></i></label>';
                str +='<span>'+resp.data[i].id+'</span>'+'<br>';
                str +='</div>';
                str +='<div class="form-inline mb-2">';
                str +='<label for="replywriter"><i class="fa fa-user-circle-o fa-2x"></i></label>';
                str +="작성자:<span id='replywriter'>"+resp.data[i].replyWriter+'</span>'+'</br>';
                str +='</div>';
                str +='<div class="form-inline mb-2">';
                str +='<label for="replycontents"><i class="fa fa-user-circle-o fa-2x"></i></label>';
                str +="글 내용:<span id='replycontents'>"+resp.data[i].replyContents+'</span>'+'</br>';
                str +='</div>';
                str +='<div class="form-inline mb-2">';
                str +='<label for="createdAt"><i class="fa fa-user-circle-o fa-2x"></i></label>';
                str +='<span id="createdAt">'+resp.data[i].createdTime+'</span>'+'</br>';
                str +='</div>';
                str +='<button type="button" class="btn btn-primary" onClick="deleteReply('+resp.data[i].id+'\)">'+'삭제'+'</button>';
                str +='</li>';
                str +='</ul>';
                str +='</div>';
                str +='</div>';
            }
        }else{
            //댓글이 없는 경우
            str += "<div class='mb-2'>";
            str += "<h6><strong>등록된 댓글이 없습니다.</strong></h6>";
            str += "</div>";
        }
        $('#replylist').html(str);
    }).fail(function(error){
        console.log(error);
    });;
}

//댓글 작성(자유게시판)o.k
function replyWrite(){

    let replywriter = $('#board_writer').val();
    let replycontents = $('#contents').val();
    let boardid = $('#board_id').val();

    const formdate ={
        boardId : boardid,
        replyWriter : replywriter,
        replyContents : replycontents
    };

    if(validation()){
        $.ajax({
            url:'/api/comment/write/'+boardid,
            type:'post',
            data:JSON.stringify(formdate),
            dataType:'json',
            contentType:'application/json; charset=utf-8'
        }).done(function(resp){
            console.log(resp);
            alert('댓글이 작성되었습니다.');
            $('#replylist').empty();
            //작성 부분 지우기.
            $('#contents').val("");
            $('#writer').val("");
            Replylist();
        });
    }
}
//댓글 삭제o.k
function deleteReply(replyId){

    const IsConfirm = confirm('삭제하겠습니까?');

    if(IsConfirm){
        $.ajax({
            url:'/api/comment/delete/'+replyId,
            type:'delete',
            dataType:'json',
            contentType : 'application/json; charset=utf-8'
        }).done(function(resp){
            console.log(resp);
            alert('댓글이 삭제 되었습니다.');
            $('#replylist').empty();
            Replylist();
        });
    }
}