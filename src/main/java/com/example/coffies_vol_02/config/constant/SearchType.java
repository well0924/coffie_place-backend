package com.example.coffies_vol_02.config.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SearchType {
    t,
    c,
    w,
    i,
    e,
    n,
    p,
    a,
    all;

    public static SearchType toType(String type){
        return switch (type){
          case "title" ->t;
          case "contents"->c;
          case "writer"->w;
          case "userId"->i;
          case "userEmail"->e;
          case "userName"->n;
          case "placeName"->p;
          case "placeAddress"->a;
            default -> all;
        };
    }
}
