package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParcelDaoImpl implements ParcelDao{
    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Parcel> getAll(long shipmentId) {
        Session session = this.sessionFactory.getCurrentSession();
        Shipment shipment = (Shipment) session.get(Shipment.class, shipmentId);
        if (shipment == null) {
            return null;
        }
        return shipment.getParcels();
    }

    @Override
    public Parcel getById(long id) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Parcel) session.get(Parcel.class, id);
    }

    @Override
    public Parcel save(Parcel parcel) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Parcel) session.merge(parcel);
    }

    @Override
    public void update(Parcel parcel) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(parcel);
    }

    @Override
    public void delete(Parcel parcel) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(parcel);
    }
}