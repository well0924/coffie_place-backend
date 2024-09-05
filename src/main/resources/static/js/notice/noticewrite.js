/*
* 공지 게시판 글 작성 페이지
*
*/
//공지게시글 작성
function noticewrite(){

    const formDate = new FormData();
    let title=$('#noticeTitle').val();
    let author=$('#noticeAuthor').val();
    let contents=$('#noticeContent').val();
    let category=$('#noticeGroup').val();
    let fixed=$('#isFixed').val();
    let fileId=$('#fileGroupId').val();
    let noticeDate ={noticeTitle:title,noticeAuthor:author,noticeContent:contents,noticeGroup:category,isFixed: fixed,fileGroupId: fileId};
    let inputFiles = $("input[name='file']");
    let files = inputFiles[0].files;
    let fileCount =6;

    formDate.append("noticeDto",new Blob([JSON.stringify(noticeDate)], {type: "application/json"}));

    if(files.length > fileCount){
        $('#valid_file').text('파일은 6개까지입니다.');
        $('#valid_file').css('color','red');
        return false;
    }

    if(inputFiles != null){
        for(var i =0; i<files.length; i++){
            console.log(files[i]);
            formDate.append("files",files[i]);
        }
    }

    $.ajax({
        url:'/api/notice/',
        type:'post',
        data:formDate,
        processData: false,
        contentType : false,
        cache:false,
        enctype: 'multipart/form-data',
    }).always(function(resp){
        if(resp.status==200){
            console.log(resp);
            alert('글이 작성 되었습니다.');
            location.href='/page/notice/list';
        }
        if(resp.status==400){
            if(resp.data.hasOwnProperty('valid_noticeGroup')){
                $('#valid_noticeGroup').text(resp.data.valid_noticeGroup);
                $('#valid_noticeGroup').css('color','red');
            }else{
                $('#valid_noticeGroup').text('');
            }
            if(resp.data.hasOwnProperty('valid_noticeFixed')){
                $('#valid_noticeFixed').text(resp.data.valid_noticeFixed);
                $('#valid_noticeFixed').css('color','red');
            }else{
                $('#valid_noticeFixed').text('');
            }
            if(resp.data.hasOwnProperty('valid_noticeTitle')){
                $('#valid_noticeTitle').text(resp.data.valid_noticeTitle);
                $('#valid_noticeTitle').css('color','red');
            }else{
                $('#valid_noticeTitle').text('');
            }
            if(resp.data.hasOwnProperty('valid_noticeContents')){
                $('#valid_noticeContents').text(resp.data.valid_noticeContents);
                $('#valid_noticeContents').css('color','red');
            }else{
                $('#valid_noticeContents').text('');
            }
        }
    });
}