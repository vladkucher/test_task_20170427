package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class ParcelItem {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long quantity;
    private float weight;
    private float price;

    public ParcelItem(String name, long quantity, float weight, float price) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
    }
}