package com.ferenc.reservation;

import java.time.LocalDate;

public class TestConstants {

    public static final int INITIAL_SEQUENCE = 0;
    public static final int BOOKING_ID = INITIAL_SEQUENCE + 1;
    public static final int BOOKING_ID_OTHER = BOOKING_ID + 1;
    public static final String LICENCE_PLATE = "ABC123";
    public static final String LICENCE_PLATE_OTHER = "ABC124";
    public static final LocalDate START_DATE = LocalDate.now();
    public static final LocalDate END_DATE = LocalDate.now().plusDays(1);
    public static final String USER_ID = "abc@google.com";
    public static final String USER_ID_OTHER = "foo" + USER_ID;
}
