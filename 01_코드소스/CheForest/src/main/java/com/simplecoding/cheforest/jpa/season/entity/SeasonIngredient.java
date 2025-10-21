package com.simplecoding.cheforest.jpa.season.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SEASON_INGREDIENT")
public class SeasonIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_ingredient_seq")
    @SequenceGenerator(
            name = "season_ingredient_seq",
            sequenceName = "SEASON_INGREDIENT_SEQ",
            allocationSize = 1
    )
    private Long ingredientId;

    private String name;

    private String seasons;

    private String seasonDetail;

    private String description;

    private String effects;

    private String imageUrl;
}
