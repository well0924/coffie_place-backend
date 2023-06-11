package com.example.coffies_vol_02.config;

import com.example.coffies_vol_02.config.api.dto.KakaoApiResponseDto;
import com.example.coffies_vol_02.config.api.service.KakaoApiSearchService;
import com.example.coffies_vol_02.config.api.service.KakaoUriBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KakaoMapApiTest {
    @Autowired
    private KakaoUriBuilderService kakaoUriBuilderService;

    @Autowired
    private KakaoApiSearchService kakaoApiSearchService;

    @BeforeEach
    public void init(){
        this.kakaoUriBuilderService = new KakaoUriBuilderService();
    }

    @Test
    @DisplayName("api 호출 테스트")
    public void kakoApiTest(){

        String address = "서울 성북구";
        Charset charset = StandardCharsets.UTF_8;

        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
        String decodeURL = URLDecoder.decode(uri.toString(),charset);

        assertThat(decodeURL).isEqualTo("https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구");
    }

    @Test
    @DisplayName("주소를 검색시 결과가 없는 경우 null을 호출")
    public void kakaoApiReturnTest(){
        String address = null;

        KakaoApiResponseDto result = kakaoApiSearchService.requestAddressSearch(address);

        System.out.println(result);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("파라미터에 주소를 입력시 결과값이 나오는지 테스트")
    public void kakaoApiSearchTest(){

        String address = "서울특별시 강북구 번동 418-3 1층 103호";

        KakaoApiResponseDto result = kakaoApiSearchService.requestAddressSearch(address);

        System.out.println(result.getDocumentList().get(0).getAddressName());
        System.out.println(result.getDocumentList().get(0).getPlaceName());

        assertThat(result).isNotNull();
        assertThat(result.getDocumentList()).isNotEmpty();
        assertThat(result.getMetaDto()).isNotNull();
    }
}
