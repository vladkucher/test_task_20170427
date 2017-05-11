package com.opinta.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class ParcelDto {
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private float declaredPrice;
    private float price;

    public ParcelDto(float weight, float length, float declaredPrice, float price) {
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }
}