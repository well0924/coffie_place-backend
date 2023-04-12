package com.example.coffies_vol_02.FavoritePlace.domain;

import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_favorite_place")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ToString.Exclude
    @ManyToOne
    private Place place;
    @ToString.Exclude
    @ManyToOne
    private Member member;
    private String fileGroupId;

    @Builder
    public FavoritePlace(Place place,Member member,String fileGroupId){
        this.place = place;
        this.member = member;
        this.fileGroupId = fileGroupId;
    }
}
