package com.mysalonsolutions.persistence;

import com.mysalonsolutions.entity.ClientServices;
import com.mysalonsolutions.entity.Reservation;
import com.mysalonsolutions.entity.Role;
import com.mysalonsolutions.entity.User;
import com.mysalonsolutions.test.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gdata.data.DateTime;

/**
 * This class will verify whether the methods of the Reservation entity class function properly
 */
public class GenericReservationDaoTest {

    GenericDao reservationDao;

    @BeforeEach
    void setUp() {
        Database database = Database.getInstance();
        database.runSQL("cleandb.sql");
        reservationDao = new GenericDao<>(Reservation.class);
    }

    /**
     * Fetch Reservation successfully
     */
    @Test
    void getByIdSuccess() {
        GenericDao reservationDao = new GenericDao(Reservation.class);
        Reservation reservationToFetch = (Reservation) reservationDao.getById(3);
        assertNotNull(reservationToFetch);

        int nextId = reservationDao.getAll().size();

        Reservation reservationToCompare = new Reservation();
        reservationToCompare.setResId(nextId);
        DateTime resStartTime = DateTime.parseDateTime("2020-04-17, 9:00:00");
        reservationToCompare.setResDateTime(resStartTime);
        reservationToCompare.setResSalonId(3);
        reservationToCompare.setResServiceId(3);

        assertTrue(reservationToCompare.equals(reservationToFetch));
    }

    /**
     * Insert reservation successfully
     */
    @Test
    void insertSuccess() {
        GenericDao userDao = new GenericDao(User.class);
        User user = (User) userDao.getById(5);

        GenericDao clientServicesDao = new GenericDao((ClientServices.class));
        List <ClientServices> clientServicesToFetch =  clientServicesDao.getByPropertyLike("clientId", String.valueOf(user.getId()));
        ClientServices clientServiceToSchedule = new ClientServices();
        clientServiceToSchedule = (ClientServices) clientServicesToFetch.get(2);

        GenericDao reservationDao = new GenericDao((Reservation.class));
        Reservation reservationToInsert = new Reservation();
        reservationToInsert.setResSalonId(clientServiceToSchedule.getClientId());
        reservationToInsert.setResServiceId(clientServiceToSchedule.getClientServiceId());

        DateTime resStartTime = DateTime.parseDateTime("2020-04-17, 9:00:00");
        reservationToInsert.setResDateTime(resStartTime);

        int id = reservationDao.insert(reservationToInsert);
        Reservation insertedReservation = (Reservation) reservationDao.getById(id);
        assertNotEquals(0, id);
        assertTrue(reservationToInsert.equals(insertedReservation.getResSalonId()));

    }

    /**
     * Delete a reservation successfully
     */
    @Test
    void deleteSuccess() {
        Reservation reservationToDelete = (Reservation) reservationDao.getById(3);

        reservationDao.delete(reservationToDelete);

        assertNull(reservationDao.getById(3));
    }

    /**
     * Retrieve all reservations successfully
     */
    @Test
    void getAllSuccess() {
        List<Reservation> reservationList = reservationDao.getAll();
        assertEquals(5, reservationList.size());
    }

    /**
     * Get by property (matching equal values) successfully
     */
    @Test
    void getByPropertyEqualSuccess() {
        List<Reservation> reservationListEqualled = reservationDao.getByPropertyEqual("resSalonId", String.valueOf(3));
        assertEquals(2, reservationListEqualled.size());
        assertEquals(1, reservationListEqualled.get(0).getResServiceId());
    }

    /**
     * Get by property (matching similar values) successfully
     */
    @Test
    void getByPropertyLikeSuccess() {
        List<Reservation> reservationListLike = reservationDao.getByPropertyLike("resDateTime", "2020-04-17, 9:00:00");
        assertEquals(1, reservationListLike.size());
        assertEquals(1, reservationListLike.get(0).getResServiceId());
    }
}

