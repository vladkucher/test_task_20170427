package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService{
    private final ParcelDao parcelDao;
    private final ShipmentDao shipmentDao;
    private final ParcelMapper parcelMapper;
    private final ShipmentService shipmentService;


    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ShipmentDao shipmentDao, ParcelMapper parcelMapper, ShipmentService shipmentService) {
        this.parcelDao = parcelDao;
        this.shipmentDao = shipmentDao;
        this.parcelMapper = parcelMapper;
        this.shipmentService = shipmentService;
    }

    @Override
    @Transactional
    public List<Parcel> getAllEntities(long shipmentId) {
        return parcelDao.getAll(shipmentId);
    }

    @Override
    @Transactional
    public Parcel getEntityById(long id) {
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        return parcelDao.save(parcel);
    }

    @Override
    @Transactional
    public List<ParcelDto> getAll(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if(shipment==null){
            return null;
        }
        return parcelMapper.toDto(getAllEntities(shipmentId));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(long shipmentId, ParcelDto parcelDto) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if(shipment==null){
            return null;
        }
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        Parcel saved=parcelDao.save(parcel);
        shipment.getParcels().add(saved);
        shipment.setPrice(shipmentService.calculatePrice(shipment));
        shipmentDao.update(shipment);
        return parcelMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParcelDto update(long shipmentId, long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if(target==null){
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        parcelDao.update(target);
        Shipment shipment = shipmentDao.getById(shipmentId);
        shipment.setPrice(shipmentService.calculatePrice(shipment));
        shipmentDao.update(shipment);
        return parcelMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long shipmentId, long id) {
        Parcel parcel = parcelDao.getById(id);
        if(parcel==null) {
            return false;
        }
        parcelDao.delete(parcel);
        Shipment shipment = shipmentDao.getById(shipmentId);
        shipment.getParcels().remove(parcel);
        shipment.setPrice(shipmentService.calculatePrice(shipment));
        shipmentDao.update(shipment);
        return true;
    }
}