/*
* 공지 게시판 수정 및 삭제 페이지
* 
*/
//공지게시글 수정o.k
function noticeupdate(){
    const formDate = new FormData();

    let id=$('#noticeid').val();
    let title=$('#noticeTitle').val();
    let author =$('#noticeAuthor').val();
    let contents=$('#noticeContent').val();
    let category=$('#noticeGroup').val();
    let fixed=$('#isFixed').val();
    let inputFiles = $("input[name='file']");
    let files = inputFiles[0].files;
    let noticeDate ={noticeTitle:title,noticeAuthor:author,noticeContent:contents,noticeGroup:category,isFixed: fixed};

    if(inputFiles != null){
        for(let i =0; i<files.length; i++){
            console.log(files[i]);
            formDate.append('files',files[i]);
        }
    }

    formDate.append("updateDto",new Blob([JSON.stringify(noticeDate)], {type: "application/json"}));

    $.ajax({
        url:'/api/notice/update/'+id,
        type:'put',
        data:formDate,
        processData: false,
        contentType : false,
        cache:false,
        enctype: 'multipart/form-data',
    }).done(function(resp){
        console.log(resp);
        alert('공지글이 수정 되었습니다.');
        location.href='/page/notice/list';
    });
}

//공지게시글 삭제o.k
function noticedelete(){

    let id=$('#noticeid').val();

    $.ajax({
        url:'/api/notice/delete/'+id,
        type:'delete',
        data:null,
        contentType:'application/json; charset=utf-8'
    }).done(function(resp){
        console.log(resp);
        alert('공지글이 삭제되었습니다.');
        location.href='/page/notice/list';
    });
}
