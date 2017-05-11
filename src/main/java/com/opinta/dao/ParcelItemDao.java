package com.opinta.dao;


import com.opinta.entity.ParcelItem;

import java.util.List;

public interface ParcelItemDao {
    List<ParcelItem> getAll(long parcelId);

    ParcelItem getById(long id);

    ParcelItem save(ParcelItem parcelItem);

    void update(ParcelItem parcelItem);

    void delete(ParcelItem parcelItem);
}
