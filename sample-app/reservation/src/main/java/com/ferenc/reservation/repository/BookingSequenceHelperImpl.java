package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.model.BookingSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingSequenceHelperImpl implements BookingSequenceHelper {

    private static final String BOOKING_SEQUENCE_KEY = "LastBooking";
    private final BookingSequenceRepository bookingSequenceRepository;
    
    @Transactional
    public Integer getNextSequence(){
        BookingSequence bookingSequence = bookingSequenceRepository.findByKey(BOOKING_SEQUENCE_KEY).get();
        int sequence = bookingSequence.getSequence();
        bookingSequence.setSequence(++sequence);
        bookingSequenceRepository.save(bookingSequence);
        return sequence;
    }

    public String getBookingSequenceKey(){
        return BOOKING_SEQUENCE_KEY;
    }
}
