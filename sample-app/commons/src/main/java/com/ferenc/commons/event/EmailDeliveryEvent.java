package com.ferenc.commons.event;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
public class EmailDeliveryEvent implements Serializable {

        private int bookingId;

        private LocalDateTime emailSent;
}
