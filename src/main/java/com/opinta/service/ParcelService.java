package com.opinta.service;


import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;

import java.util.List;

public interface ParcelService {
    List<Parcel> getAllEntities(long shipmentId);

    Parcel getEntityById(long id);

    Parcel saveEntity(Parcel parcel);

    List<ParcelDto> getAll(long shipmentId);

    ParcelDto getById(long id);

    ParcelDto save(long shipmentId, ParcelDto parcelDto);

    ParcelDto update(long shipmentId, long id, ParcelDto parcelDto);

    boolean delete(long shipmentId, long id);
}