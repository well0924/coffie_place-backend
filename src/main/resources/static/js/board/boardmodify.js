/**
 * 글 수정 기능o.k
 */
function updateboard(){
    const formdate = new FormData();

    let title = $('#board_Title').val();
    let author= $('#board_Author').val();
    let contents= $('#board_Contents').val();
    let pwd = $('#passWd').val();
    let id = $('#board_id').val();
    let inputFiles = $("input[name='file']");
    let files = inputFiles[0].files;

    formdate.append("boardTitle",title);
    formdate.append("boardContents",contents);
    formdate.append("boardAuthor",author);
    formdate.append("passWd",pwd);

    if(inputFiles != null){
        for(let i = 0; i< files.length; i++){
            console.log(files[i]);
            formdate.append("files",files[i]);
        }
    }

    $.ajax({
        url:'/api/board/update/'+id,
        type:'put',
        data:formdate,
        processData: false,
        contentType : false,
        cache:false,
        enctype: 'multipart/form-data',
    }).done(function(resp){
        console.log(resp);
        alert('수정되었습니다.');
        location.href='/page/board/list';
    });
}

//글 삭제 기능o.k
function deleteboard(){
    let id = $('#board_id').val();

    $.ajax({
        url:'/api/board/delete/'+id,
        type:'delete',
        dataType:'json',
        contentType :'application/json; charset=utf-8',
    }).done(function(resp){
        console.log(resp);
        alert('삭제되었습니다.');
        location.href='/page/board/list';
    });
}
