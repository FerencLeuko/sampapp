package com.ferenc.commons.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailDeliveryEvent implements Serializable {

    private int bookingId;

    private LocalDateTime emailSent;
}
