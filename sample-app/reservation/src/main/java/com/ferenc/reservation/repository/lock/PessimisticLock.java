package com.ferenc.reservation.repository.lock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PessimisticLock {

    @Id
    private String lisencePlate;

    @CreatedDate
    private Date createdDate;
}
