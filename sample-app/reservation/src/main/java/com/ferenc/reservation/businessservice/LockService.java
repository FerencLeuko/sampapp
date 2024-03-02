package com.ferenc.reservation.businessservice;

public interface LockService {

    void acquireLock(String licencePlate);

    void releaseLock(String licencePlate);
}
