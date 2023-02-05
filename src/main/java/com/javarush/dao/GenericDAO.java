package com.javarush.dao;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;


public abstract class GenericDAO<T>  {

    private final Class<T> clazz;
    private SessionFactory sessionFactory;

    public GenericDAO(final Class<T> clazzToSet, SessionFactory sessionFactory) {
        this.clazz = clazzToSet;
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T getById(final Serializable id) {
        return (T) getCurrentSession().get(this.clazz, id);
    }
    public List <T> getItems(int offSet, int count){
        Query query = getCurrentSession().createQuery("from " + clazz.getName(),clazz);
        query.setFirstResult(offSet);
        query.setMaxResults(count);
        return query.getResultList();
    }

    public T Save (final T entity) {
         getCurrentSession().saveOrUpdate(entity);
         return entity;
    }


    public T Update(final T entity) {
       return (T) getCurrentSession().merge(entity);
    }

    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    public void deleteById(final int entityId) {
        final T entity = getById(entityId);
        delete(entity);
        }

    public List<T> findAll() {
        return getCurrentSession().createQuery("from" + clazz.getName(),clazz).list();
    }
}