package com.ferenc.reservation.repository.lock;

public interface LockService {

    void acquireLock(String licencePlate);

    void releaseLock(String licencePlate);
}
