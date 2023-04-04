
//댓글 유효성 검사
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

    $.getJSON("/api/reply/"+id,function(data){
        let str ="";

        $(data).each(function(){
            str +='<div class="card mb-2">';
            str +='<div class="card-header bg-light">';
            str +='<i class="fa fa-comment fa"></i>';
            str +='</div>';
            str +='<div class="card-body">';
            str +='<ul class="list-group list-group-flush" id="replies">';
            str +='<li class="list-group-item" data-replyId="'+this.replyId+'">';
            str +='<div class="form-inline mb-2">';
            str +='<label for="replyId"><i class="fa fa-user-circle-o fa-2x"></i></label>';
            str +='<span>'+this.replyId+'</span>'+'<br>';
            str +='</div>';
            str +='<div class="form-inline mb-2">';
            str +='<label for="replywriter"><i class="fa fa-user-circle-o fa-2x"></i></label>';
            str +="작성자:<span id='replywriter'>"+this.replyWriter+'</span>'+'</br>';
            str +='</div>';
            str +='<div class="form-inline mb-2">';
            str +='<label for="replycontents"><i class="fa fa-user-circle-o fa-2x"></i></label>';
            str +="글 내용:<span id='replycontents'>"+this.replyContents+'</span>'+'</br>';
            str +='</div>';
            str +='<div class="form-inline mb-2">';
            str +='<label for="createdAt"><i class="fa fa-user-circle-o fa-2x"></i></label>';
            str +='<span id="createdAt">'+this.createdAt+'</span>'+'</br>';
            str +='</div>';
            str +='<button type="button" class="btn btn-primary" onClick="deleteReply('+this.replyId+'\)">'+'삭제'+'</button>';
            str +='</li>';
            str +='</ul>';
            str +='</div>';
            str +='</div>';
        });
        $('#replylist').html(str);
    });
}

//댓글 작성(자유게시판)o.k
function replyWrite(){

    let replywriter = $('#writer').val();
    let replycontents = $('#contents').val();
    let boardid = $('#board_id').val();

    const formdate ={
        boardId : boardid,
        replyWriter : replywriter,
        replyContents : replycontents
    };

    if(validation()){
        $.ajax({
            url:'/api/reply/write',
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

    const Isconfirm = confirm('삭제하겠습니까?');

    if(Isconfirm){
        $.ajax({
            url:'/api/reply/delete/'+replyId,
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