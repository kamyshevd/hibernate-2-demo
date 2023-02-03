package com.javarush.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "film_text",schema = "movie")
public class FilmText {
    @Id
    @Column(name = "film_id")
    private Short film_id;

    @OneToOne
    @Column(name = "film_id")
    private Film film;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    @Type(type = "text")
    private String description;

    public Short getFilm_id() {
        return film_id;
    }

    public void setFilm_id(Short film_id) {
        this.film_id = film_id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
