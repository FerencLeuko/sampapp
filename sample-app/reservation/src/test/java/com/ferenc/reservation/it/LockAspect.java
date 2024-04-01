package com.ferenc.reservation.it;

import java.time.Instant;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("aspectEnabled")
class LockAspect {

    @Before("execution(* com.ferenc.reservation.repository.lock.LockService.acquireLock(..))")
    public void beforeAcquireLock(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String licencePlate = (String) args[0];
        String userId = (String) args[1];

        System.out.println("Acquiring lock for licencePlate: " + licencePlate + " and userId: " + userId);
        System.out.println(Instant.now().toEpochMilli());
    }

    @AfterReturning("execution(* com.ferenc.reservation.repository.lock.LockService.acquireLock(..))")
    public void afterAcquireLock(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String licencePlate = (String) args[0];
        String userId = (String) args[1];

        System.out.println("Lock acquired for licencePlate: " + licencePlate + " and userId: " + userId);
        System.out.println(Instant.now().toEpochMilli());
    }

    @Before("execution(* com.ferenc.reservation.repository.lock.LockService.releaseLock(..))")
    public void beforeReleaseLock(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String licencePlate = (String) args[0];
        String userId = (String) args[1];

        System.out.println("Releasing lock for licencePlate: " + licencePlate + " and userId: " + userId);
        System.out.println(Instant.now().toEpochMilli());
    }

    @AfterReturning("execution(* com.ferenc.reservation.repository.lock.LockService.releaseLock(..))")
    public void afterReleaseLock(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String licencePlate = (String) args[0];
        String userId = (String) args[1];

        System.out.println("Lock released for licencePlate: " + licencePlate + " and userId: " + userId);
        System.out.println(Instant.now().toEpochMilli());
    }

    @Before("execution(* com.ferenc.reservation.repository.lock.LockService.acquireLock(..)) && @annotation(org.springframework.retry.annotation.Retryable)")
    public void beforeRetry(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String licencePlate = (String) args[0];
        String userId = (String) args[1];

        System.out.println("Retry attempt for licencePlate: " + licencePlate + " and userId: " + userId);
        System.out.println(Instant.now().toEpochMilli());
    }
}