package com.example.coffies_vol_02.config.constant;

import lombok.Getter;

public enum SearchType {
    t("제목"),
    c("내용"),
    w("작성자"),
    p("가게명"),
    a("주소명"),
    all("전부");
    @Getter
    private final String value;

    SearchType(String value){
        this.value = value;
    }
}
