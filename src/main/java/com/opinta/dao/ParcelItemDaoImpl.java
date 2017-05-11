package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParcelItemDaoImpl implements ParcelItemDao{
    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelItemDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<ParcelItem> getAll(long parcelId) {
        Session session = this.sessionFactory.getCurrentSession();
        Parcel parcel = (Parcel) session.get(Parcel.class,parcelId);
        if(parcel==null){
            return null;
        }
        return parcel.getParcelItems();
    }

    @Override
    public ParcelItem getById(long id) {
        Session session = this.sessionFactory.getCurrentSession();
        return (ParcelItem) session.get(ParcelItem.class, id);
    }

    @Override
    public ParcelItem save(ParcelItem parcelItem) {
        Session session = this.sessionFactory.getCurrentSession();
        return (ParcelItem) session.merge(parcelItem);
    }

    @Override
    public void update(ParcelItem parcelItem) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(parcelItem);
    }

    @Override
    public void delete(ParcelItem parcelItem) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(parcelItem);
    }
}