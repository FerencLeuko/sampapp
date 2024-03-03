package com.ferenc.reservation.repository.lock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PessimisticLock implements Persistable<String> {

    @Id
    private String id;

    private String userId;

    @CreatedDate
    private Date createdDate;

    @Override
    public boolean isNew() {
        return this.createdDate == null;
    }
}
