package com.example.coffies_vol_02.board.domain.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto implements Serializable {
    @NotBlank(message = "제목을 작성해 주세요.")
    private String boardTitle;
    @NotBlank(message = "내용을 입력해 주세요.")
    private String boardContents;
    private String boardAuthor;
    private Integer readCount;
    private String passWd;
    private String fileGroupId;
    private List<MultipartFile> files;
}
