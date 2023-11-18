package com.ferenc.commons.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDeliveryEvent implements Serializable {

        private int bookingId;

        private LocalDateTime emailSent;

}
