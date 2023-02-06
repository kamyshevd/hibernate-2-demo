package com.javarush;

import com.javarush.dao.*;
import com.javarush.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Main {
    private final SessionFactory sessionFactory;
    private final ActorDAO actorDAO;
    private final AddressDAO addressDAO;
    private final CategoryDAO categoryDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final CustomerDAO customerDAO;
    private final FeatureDAO featureDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;
    private final InventoryDAO inventoryDAO;
    private final LanguageDAO languageDAO;
    private final PaymentDAO paymentDAO;
    private final RatingDAO ratingDAO;
    private final RentalDAO rentalDAO;
    private final StaffDAO staffDAO;
    private final StoreDAO storeDAO;

    public Main() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "admin");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS,"thread");
        properties.put(Environment.HBM2DDL_AUTO,"validate");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Feature.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rating.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .buildSessionFactory();
        actorDAO = new ActorDAO(sessionFactory);
        addressDAO = new AddressDAO(sessionFactory);
        categoryDAO = new CategoryDAO(sessionFactory);
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        customerDAO = new CustomerDAO(sessionFactory);
        featureDAO = new FeatureDAO(sessionFactory);
        filmDAO = new FilmDAO(sessionFactory);
        filmTextDAO = new FilmTextDAO(sessionFactory);
        inventoryDAO = new InventoryDAO(sessionFactory);
        languageDAO = new LanguageDAO(sessionFactory);
        paymentDAO = new PaymentDAO(sessionFactory);
        ratingDAO = new RatingDAO(sessionFactory);
        rentalDAO = new RentalDAO(sessionFactory);
        staffDAO = new StaffDAO(sessionFactory);
        storeDAO = new StoreDAO(sessionFactory);

    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.customerReturnInventoryToStore();
        main.customerRentInventory(customer);
        main.newFilmWasMade();
    }

    private void newFilmWasMade() {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Language language = languageDAO.getItems(0,20).stream().unordered().findAny().get();
            List<Category> categories = categoryDAO.getItems(0,5);
            List<Actor> actors = actorDAO.getItems(0,20);

            Film film = new Film();
            film.setActors(new HashSet<>(actors));
            film.setCategories(new HashSet<>(categories));
            film.setSpecialFeatures(Set.of(Feature.BEHINDTHESCENES,Feature.TRAILERS));
            film.setLanguage(language);
            film.setLength((short) 123);
            film.setDescription("new scary film");
            film.setTitle("scary movie");
            film.setRentalDuration((byte) 44);
            film.setOriginalLanguage(language);
            film.setYear(Year.now());
            film.setRating(Rating.NC17);
            film.setRentalRate(BigDecimal.ZERO);
            film.setReplacementCost(BigDecimal.TEN);
            filmDAO.Save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setFilm_id(film.getFilm_id());
            filmText.setDescription("new scary film");
            filmText.setTitle("scary movie");
            filmTextDAO.Save(filmText);

            session.getTransaction().commit();
        }
    }

    private void customerRentInventory(Customer customer) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Film film = filmDAO.getFirstAvailableFilmForRent();
            Store store = storeDAO.getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.Save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDAO.Save(rental);

            Payment payment = new Payment();

            payment.setCustomer(customer);
            payment.setAmount(BigDecimal.valueOf(55.77));
            payment.setPaymentDate(LocalDateTime.now());
            payment.setRental(rental);
            payment.setStaff(staff);

            paymentDAO.Save(payment);

            session.getTransaction().commit();
        }
    }

    private void customerReturnInventoryToStore() {

        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setRentalDate(LocalDateTime.now());
            rentalDAO.Save(rental);
            session.getTransaction().commit();
            }
    }

    private Customer createCustomer() {
        try(Session session = sessionFactory.getCurrentSession()) {
        session.beginTransaction();
            Store store = storeDAO.getItems(0, 1).get(0);

            City city = cityDAO.getByName("Acua");
            Address address = new Address();
            address.setAddress("Indep str,48");
            address.setPhone("89332832983");
            address.setCity(city);
            address.setDistrict("Hamovniki");
            addressDAO.Save(address);

            Customer customer = new Customer();
            customer.setActive(true);
            customer.setAddress(address);
            customer.setEmail("cool@yandex.ru");
            customer.setFirstName("Vanya");
            customer.setLastName("Pirozhkov");
            customer.setStore(store);
            customerDAO.Save(customer);

            session.getTransaction().commit();
             return customer;
        }
    }
}