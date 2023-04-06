function fileCheck(){

    if(inputFiles == null){
        writeboard();
    }

    return true;
}

function writeboard(){

    const formdate = new FormData();
    let title = $('#board_Title').val();
    let author= $('#board_Author').val();
    let contents= $('#board_Contents').val();
    let pwd = $('#passWd').val();
    let fileid = $('#fileGroupId').val();
    let data = {boardTitle : title,boardContents : contents, boardAuthor: author,fileGroupId:fileid,passWd:pwd};
    let filecount = 6;
    let inputFiles = $("input[name='file']");
    let files = inputFiles[0].files;

    console.log(files);
    console.log(inputFiles);

    //파일첨부 제한.
    if(files.length > filecount){
        $('#valid_file').text('파일은 6개까지 입니다.');
        $('#valid_file').css('color','red');
        return false;
    }

    //첨부한 만큼 반복문을 돌린다.
    if(inputFiles != null){
        for(let i = 0; i<files.length;i++){
            console.log(files[i]);
            formdate.append("files",files[i]);
        }
    }
    formdate.append("board",new Blob([JSON.stringify(data)], {type: "application/json"}));

    $.ajax({
        url:'/api/board/write',
        type:'post',
        data:formdate,
        processData: false,
        contentType : false,
        cache:false,
        enctype: 'multipart/form-data',
    }).always(function(resp){

        if(resp.status == 200){
            alert('글이 작성 되었습니다.');
            location.href='/page/board/list';
        }

        if(resp.status == 400){

            if(resp.data.hasOwnProperty('valid_boardTitle')){
                $('#valid_boardTitle').text(resp.data.valid_boardTitle);
                $('#valid_boardTitle').css('color','red');
            }else{
                $('#valid_boardTitle').text('');
            }
            if(resp.data.hasOwnProperty('valid_boardContents')){
                $('#valid_boardContents').text(resp.data.valid_boardContents);
                $('#valid_boardContents').css('color','red');
            }else{
                $('#valid_boardContents').text('');
            }
        }
    });
}
