package com.opinta.service;


import com.opinta.dao.ParcelDao;
import com.opinta.dao.ParcelItemDao;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService{
    private final ParcelItemDao parcelItemDao;
    private final ParcelDao parcelDao;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelDao parcelDao) {
        this.parcelItemDao = parcelItemDao;
        this.parcelDao = parcelDao;
    }

    @Override
    @Transactional
    public List<ParcelItem> getAll(long parcelId) {
        Parcel parcel = parcelDao.getById(parcelId);
        if(parcel==null){
            return null;
        }
        return parcel.getParcelItems();
    }

    @Override
    @Transactional
    public ParcelItem getById(long id) {
        return parcelItemDao.getById(id);
    }

    @Override
    @Transactional
    public ParcelItem save(long parcelId, ParcelItem parcelItem) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel==null){
            return null;
        }
        ParcelItem saved = parcelItemDao.save(parcelItem);
        parcel.getParcelItems().add(saved);
        parcelDao.save(parcel);
        return saved;
    }

    @Override
    @Transactional
    public ParcelItem update(long id, ParcelItem parcelItem) {
        ParcelItem target = parcelItemDao.getById(id);
        if(target==null){
            return null;
        }
        try {
            copyProperties(target, parcelItem);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        parcelItemDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem==null){
            return false;
        }
        parcelItemDao.delete(parcelItem);
        return true;
    }
}