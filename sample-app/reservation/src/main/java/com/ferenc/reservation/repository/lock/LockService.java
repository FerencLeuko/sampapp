package com.ferenc.reservation.repository.lock;

public interface LockService {

    void acquireLock(String licencePlate, String userId);

    void releaseLock(String licencePlate, String userId);
}
