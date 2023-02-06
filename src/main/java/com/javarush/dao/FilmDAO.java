package com.javarush.dao;

import com.javarush.domain.Film;
import com.javarush.domain.Rental;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class FilmDAO extends GenericDAO<Film> {
    public FilmDAO( SessionFactory sessionFactory) {
        super(Film.class, sessionFactory);
    }

    public Film getFirstAvailableFilmForRent() {
        Query<Film> query = getCurrentSession().createQuery("select f from Film f" +
                " where f.film_id not in (select distinct film.film_id from Inventory ) ",Film.class);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
}
