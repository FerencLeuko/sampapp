package com.ferenc.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.auth.SecurityUtils;
import com.ferenc.reservation.businessservice.BookingBusinessService;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.mapper.BookingMapper;
import com.ferenc.reservation.mapper.CarMapper;
import com.ferenc.reservation.repository.model.Booking;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingController.class)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarMapper carMapper;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private BookingBusinessService bookingBusinessService;

    private static Booking getMockBooking(Integer bookingId) {
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setUserId(USER_ID);
        return booking;
    }

    @AfterEach
    public void verifyAll() {
        Mockito.verifyNoMoreInteractions(carMapper, bookingMapper, bookingBusinessService);
    }

    @Test
    void testPostBooking_WithPresentDates_For201() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            BookingRequest bookingRequest = getValidBookingRequest();
            Mockito.when(bookingBusinessService
                            .createBooking(
                                    USER_ID,
                                    bookingRequest.getLicencePlate(),
                                    bookingRequest.getDateRange().getStartDate(),
                                    bookingRequest.getDateRange().getEndDate()
                            ))
                    .thenReturn(getMockBooking(BOOKING_ID));
            mockMvc.perform(post("/bookings")
                            .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                    .andExpect(status().isCreated());
            Mockito.verify(bookingBusinessService)
                    .createBooking(
                            USER_ID,
                            bookingRequest.getLicencePlate(),
                            bookingRequest.getDateRange().getStartDate(),
                            bookingRequest.getDateRange().getEndDate()
                    );
        }
    }

    @Test
    void testPostBooking_WithFutureDates_For201() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            BookingRequest bookingRequest = getValidBookingRequest();
            bookingRequest.getDateRange().setStartDate(LocalDate.now().plusDays(5));
            bookingRequest.getDateRange().setEndDate(LocalDate.now().plusDays(6));
            Mockito.when(bookingBusinessService
                            .createBooking(
                                    USER_ID,
                                    bookingRequest.getLicencePlate(),
                                    bookingRequest.getDateRange().getStartDate(),
                                    bookingRequest.getDateRange().getEndDate()
                            ))
                    .thenReturn(getMockBooking(BOOKING_ID));
            mockMvc.perform(post("/bookings")
                            .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                    .andExpect(status().isCreated());
            Mockito.verify(bookingBusinessService)
                    .createBooking(
                            USER_ID,
                            bookingRequest.getLicencePlate(),
                            bookingRequest.getDateRange().getStartDate(),
                            bookingRequest.getDateRange().getEndDate()
                    );
        }
    }

    @Test
    void testPostBooking_WithPastDate_For400() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            BookingRequest bookingRequest = getValidBookingRequest();
            LocalDate pastDate = LocalDate.now().minusDays(1);
            bookingRequest.getDateRange().setStartDate(pastDate);
            mockMvc.perform(post("/bookings")
                            .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testPostBooking_WithEarlierEndDate_For400() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            BookingRequest bookingRequest = getValidBookingRequest();
            bookingRequest.getDateRange().setStartDate(LocalDate.now().plusDays(5));
            bookingRequest.getDateRange().setEndDate(LocalDate.now().plusDays(4));
            mockMvc.perform(post("/bookings")
                            .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testPostBooking_WithEmptyBody_For400() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            mockMvc.perform(post("/bookings")
                            .contentType("application/json").content(objectMapper.writeValueAsString("")))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testGetBooking_For200() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            Mockito.when(bookingBusinessService.getBooking(BOOKING_ID)).thenReturn(getMockBooking(BOOKING_ID));
            mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID))
                    .andExpect(status().isOk());
            Mockito.verify(bookingBusinessService).getBooking(BOOKING_ID);
        }
    }

    @Test
    void testUpdateBooking_For200() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            Mockito.when(bookingBusinessService.getBooking(BOOKING_ID)).thenReturn(getMockBooking(BOOKING_ID));
            UpdateRequest updateRequest = getValidUpdateRequest();
            mockMvc.perform(put("/bookings/{bookingId}", BOOKING_ID)
                            .contentType("application/json").content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk());
            Mockito.verify(bookingBusinessService)
                    .updateBooking(BOOKING_ID, updateRequest.getDateRange().getStartDate(), updateRequest.getDateRange().getEndDate());
        }
    }

    @Test
    void testUpdateBooking_WithInvalidDates_For400() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            UpdateRequest updateRequest = getValidUpdateRequest();
            updateRequest.getDateRange().setStartDate(LocalDate.now().minusDays(1));
            mockMvc.perform(put("/bookings/{bookingId}", BOOKING_ID)
                            .contentType("application/json").content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testDeleteBooking_For200() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(USER_ID);
            Mockito.when(bookingBusinessService.getBooking(BOOKING_ID)).thenReturn(getMockBooking(BOOKING_ID));
            mockMvc.perform(delete("/bookings/{bookingId}", BOOKING_ID))
                    .andExpect(status().isOk());
            Mockito.verify(bookingBusinessService).deleteBooking(BOOKING_ID);
        }
    }

}
