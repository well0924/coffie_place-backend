let file = document.getElementById('fileGroupId').value;
console.log(file);

//주소찾기o.k
function AddressCode() {
    new daum.Postcode({
        oncomplete: function (data) {
            let extraName;
//주소
            let addr = '' + extraName;
            extraName = '';

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraName += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraName += (extraName !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraName !== '') {
                    extraName = ' (' + extraName + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("place_addr1").value = extraName;
            } else {
                document.getElementById("place_addr1").value = '';
            }

            document.getElementById("place_addr1").value = addr;
            console.log(addr);
        }
    }).open();
}

//가게 등록기능.
function placeRegister() {

    let placename = document.getElementById('place_name').value;
    let placelat = document.getElementById('place_lat').value;
    let placelng = document.getElementById('place_lng').value;
    let placeaddr = document.getElementById('place_addr1').value;
    let placeaddr2 = document.getElementById('place_addr2').value;
    let placephone = document.getElementById('place_phone').value;
    let placestart = document.getElementById('place_start').value;
    let placeclose = document.getElementById('place_close').value;
    let filegroupid = document.getElementById('fileGroupId').value;

    const formdate = new FormData();
    //폼데이터
    formdate.append("placeName", placename);
    formdate.append("placeLat", placelat);
    formdate.append("placeLng", placelng);
    formdate.append("placeAddr1", placeaddr);
    formdate.append("placeAddr2", placeaddr2);
    formdate.append("placePhone", placephone);
    formdate.append("placeStart", placestart);
    formdate.append("placeClose", placeclose);
    formdate.append("fileGroupId", filegroupid);
    //이미지.
    let images = $("input[type='file']");

    let files = images[0].files;

    for (let i = 0; i < files.length; i++) {
        console.log(files[i]);
        formdate.append("images", files[i]);
    }

    //가게 등록 기능
    $.ajax({
        url: '/api/place/register',
        type: 'post',
        dataType: 'json',
        data: formdate,
        processData: false,
        contentType: false,
        cache: false,
        enctype: 'multipart/form-data',
    }).done(function (data) {

        if (data.status == 200) {
            alert("가게가 등록이 되었습니다.");
            location.href = "/page/place/list";
        }

        if (data.status == 400) {
            if (resp.data.hasOwnProperty('valid_placeName')) {
                $('#valid_placeName').text(resp.data.valid_placeName).css('color', 'red');
            } else {
                $('#valid_placeName').text('');
            }
            if (resp.data.hasOwnProperty('valid_placeLat')) {
                $('#valid_placeLat').text(resp.data.valid_placeLat).css('color', 'red');
            } else {
                $('#valid_placeLat').text('');
            }
            if (resp.data.hasOwnProperty('valid_placeLng')) {
                $('#valid_placeLng').text(resp.data.valid_placeLng).css('color', 'red');
            } else {
                $('#valid_placeLng').text('');
            }
            if (resp.data.hasOwnProperty('valid_placeAddr1')) {
                $('#valid_placeAddr1').text(resp.data.valid_placeAddr1).css('color', 'red');
            } else {
                $('#valid_placeAddr1').text('');
            }
            if (resp.data.hasOwnProperty('valid_placeStart')) {
                $('#valid_placeStart').text(resp.data.valid_placeStart).css('color', 'red');
            } else {
                $('#valid_placeStart').text('');
            }
            if (resp.data.hasOwnProperty('valid_placeClose')) {
                $('#valid_placeClose').text(resp.data.valid_placeClose).css('color', 'red');
            } else {
                $('#valid_placeClose').text('');
            }
        }
    });
}

( /* att_zone : 이미지들이 들어갈 위치 id, btn : file tag id */
    imageView = function imageView(att_zone, btnAtt) {

        const attZone = document.getElementById(att_zone);
        var btnAtt = document.getElementById(btnAtt)
        const sel_files = [];

        // 이미지와 체크 박스를 감싸고 있는 div 속성
        const div_style = 'display:inline-block;position:relative;'
            + 'width:150px;height:120px;margin:5px;border:1px solid #00f;z-index:1';
        // 미리보기 이미지 속성
        const img_style = 'width:100%;height:100%;z-index:none';
        // 이미지안에 표시되는 체크박스의 속성
        const chk_style = 'width:30px;height:30px;position:absolute;font-size:24px;'
            + 'right:0px;bottom:0px;z-index:999;background-color:rgba(255,255,255,0.1);color:#f00';

        btnAtt.onchange = function (e) {
            //파일 정보를 가져온다.
            const files = e.target.files;
            const fileArr = Array.prototype.slice.call(files);

            //이미지 유효성 검사.
            if (files.length >= 6) {
                alert("이미지는 6장까지 입니다.");
                console.log(files);
                return;
            }

            for (f of fileArr) {
                //파일 배열만큼 이미지 미리보기
                imageLoader(f);
            }
        }

        // 탐색기에서 드래그앤 드롭 사용
        attZone.addEventListener('dragenter', function (e) {
            e.preventDefault();
            e.stopPropagation();
        }, false)

        attZone.addEventListener('dragover', function (e) {
            e.preventDefault();
            e.stopPropagation();

        }, false)

        attZone.addEventListener('drop', function (e) {
            let files = {};
            e.preventDefault();
            e.stopPropagation();
            const dt = e.dataTransfer;
            files = dt.files;

            for (f of files) {

                imageLoader(f);
            }

        }, false)

        /*첨부된 이미지를 배열에 넣고 미리보기 */
        imageLoader = function (file) {
            if (sel_files.length >= 6) {
                alert('드래그는 6장까지 입니다.');
                return;
            }
            sel_files.push(file);
            const reader = new FileReader();
            reader.onload = function (ee) {
                let img = document.createElement('img')
                img.setAttribute('style', img_style)
                img.src = ee.target.result;
                attZone.appendChild(makeDiv(img, file));
            }
            reader.readAsDataURL(file);
        }

        /*첨부된 파일이 있는 경우 checkbox와 함께 attZone에 추가할 div를 만들어 반환 */
        makeDiv = function (img, file) {
            const div = document.createElement('div');
            div.setAttribute('style', div_style)

            const btn = document.createElement('input');
            btn.setAttribute('type', 'button')
            btn.setAttribute('value', 'x')
            btn.setAttribute('delFile', file.name);
            btn.setAttribute('style', chk_style);
            btn.onclick = function (ev) {
                const ele = ev.srcElement;
                const delFile = ele.getAttribute('delFile');

                for (let i = 0; i < sel_files.length; i++) {
                    if (delFile == sel_files[i].name) {
                        sel_files.splice(i, 1);
                    }
                }

                dt = new DataTransfer();
                for (f in sel_files) {
                    const file = sel_files[f];
                    dt.items.add(file);
                }
                btnAtt.files = dt.files;
                const p = ele.parentNode;
                attZone.removeChild(p)
            }
            div.appendChild(img)
            div.appendChild(btn)
            return div
        }
    }
)('att_zone', 'btnAtt')