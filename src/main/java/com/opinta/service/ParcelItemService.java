package com.opinta.service;

import com.opinta.entity.ParcelItem;

import java.util.List;

public interface ParcelItemService {
    List<ParcelItem> getAll(long parcelId);

    ParcelItem getById(long id);

    ParcelItem save(long parcelId, ParcelItem parcelItem);

    ParcelItem update(long id, ParcelItem parcelItem);

    boolean delete(long id);
}