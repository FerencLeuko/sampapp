package com.ferenc.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferenc.reservation.auth.SecurityUtils;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.DateRange;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.mapper.BookingMapper;
import com.ferenc.reservation.mapper.CarMapper;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.businessservice.BookingBusinessService;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingController.class)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {
	
        private static final String TEST_USER_ID = "abc@google.com";
        private static final int TEST_BOOKING_ID = 0;
        
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

        @AfterEach
        public void verifyAll(){
                Mockito.verifyNoMoreInteractions(carMapper, bookingMapper, bookingBusinessService);
        }
        
        @Test
        void testPostBooking_WithPresentDates_For201() throws Exception {
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                        BookingRequest bookingRequest = getValidBookingRequest();
                        Mockito.when(bookingBusinessService
                                .createBooking(
                                        TEST_USER_ID,
                                        bookingRequest.getLicencePlate(),
                                        bookingRequest.getDateRange().getStartDate(),
                                        bookingRequest.getDateRange().getEndDate()
                                ))
                                .thenReturn(getMockBooking(TEST_BOOKING_ID));
                        mockMvc.perform(post("/bookings")
                                        .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                                .andExpect(status().isCreated());
                        Mockito.verify(bookingBusinessService)
                                .createBooking(
                                        TEST_USER_ID,
                                        bookingRequest.getLicencePlate(),
                                        bookingRequest.getDateRange().getStartDate(),
                                        bookingRequest.getDateRange().getEndDate()
                                );
                }
        }

        @Test
        void testPostBooking_WithFutureDates_For201() throws Exception {
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                        BookingRequest bookingRequest = getValidBookingRequest();
                        bookingRequest.getDateRange().setStartDate(LocalDate.now().plusDays(5));
                        bookingRequest.getDateRange().setEndDate(LocalDate.now().plusDays(6));
                        Mockito.when(bookingBusinessService
                                        .createBooking(
                                                TEST_USER_ID,
                                                bookingRequest.getLicencePlate(),
                                                bookingRequest.getDateRange().getStartDate(),
                                                bookingRequest.getDateRange().getEndDate()
                                        ))
                                .thenReturn(getMockBooking(TEST_BOOKING_ID));
                        mockMvc.perform(post("/bookings")
                                        .contentType("application/json").content(objectMapper.writeValueAsString(bookingRequest)))
                                .andExpect(status().isCreated());
                        Mockito.verify(bookingBusinessService)
                                .createBooking(
                                        TEST_USER_ID,
                                        bookingRequest.getLicencePlate(),
                                        bookingRequest.getDateRange().getStartDate(),
                                        bookingRequest.getDateRange().getEndDate()
                                );
                }
        }

        @Test
        void testPostBooking_WithPastDate_For400() throws Exception {
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
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
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
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
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                        mockMvc.perform(post("/bookings")
                                        .contentType("application/json").content(objectMapper.writeValueAsString("")))
                                .andExpect(status().isBadRequest());
                }
        }

        @Test
        void testGetBooking_For200() throws Exception {
               try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                       utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                       Mockito.when(bookingBusinessService.getBooking(TEST_BOOKING_ID)).thenReturn(getMockBooking(TEST_BOOKING_ID));
                       mockMvc.perform(get("/bookings/{bookingId}", "0"))
                               .andExpect(status().isOk());
                       Mockito.verify(bookingBusinessService).getBooking(TEST_BOOKING_ID);
               }
        }

        @Test
        void testUpdateBooking_For200() throws Exception {
               try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                       utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                       Mockito.when(bookingBusinessService.getBooking(TEST_BOOKING_ID)).thenReturn(getMockBooking(TEST_BOOKING_ID));
                       UpdateRequest updateRequest = getValidUpdateRequest();
                       mockMvc.perform(put("/bookings/{bookingId}", "0")
                                       .contentType("application/json").content(objectMapper.writeValueAsString(updateRequest)))
                               .andExpect(status().isOk());
                       Mockito.verify(bookingBusinessService).updateBooking(TEST_BOOKING_ID,updateRequest.getDateRange().getStartDate(), updateRequest.getDateRange().getEndDate());
               }
        }

        @Test
        void testUpdateBooking_WithInvalidDates_For400() throws Exception {
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                        UpdateRequest updateRequest = getValidUpdateRequest();
                        updateRequest.getDateRange().setStartDate(LocalDate.now().minusDays(1));
                        mockMvc.perform(put("/bookings/{bookingId}", "0")
                                        .contentType("application/json").content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isBadRequest());
                }
        }

        @Test
        void testDeleteBooking_For200() throws Exception {
                try(MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
                        utilities.when(SecurityUtils::getUserEmailFromToken).thenReturn(TEST_USER_ID);
                        Mockito.when(bookingBusinessService.getBooking(TEST_BOOKING_ID)).thenReturn(getMockBooking(TEST_BOOKING_ID));
                        mockMvc.perform(delete("/bookings/{bookingId}", "0"))
                                .andExpect(status().isOk());
                        Mockito.verify(bookingBusinessService).deleteBooking(TEST_BOOKING_ID);
                }
        }
        
        private static BookingRequest getValidBookingRequest(){
                BookingRequest bookingRequest = new BookingRequest();
                bookingRequest.setLicencePlate("ABC123");
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = LocalDate.now();
                DateRange dateRange = new DateRange(startDate,endDate);
                bookingRequest.setDateRange( dateRange );
                return bookingRequest;
        }

        private static UpdateRequest getValidUpdateRequest(){
                UpdateRequest updateRequest = new UpdateRequest();
                DateRange dateRange = new DateRange(LocalDate.now(),LocalDate.now());
                updateRequest.setDateRange(dateRange);
                return updateRequest;
        }

        private static Booking getMockBooking(Integer bookingId){
                Booking booking = new Booking();
                booking.setBookingId(bookingId);
                booking.setUserId(TEST_USER_ID);
                return booking;
        }

}
