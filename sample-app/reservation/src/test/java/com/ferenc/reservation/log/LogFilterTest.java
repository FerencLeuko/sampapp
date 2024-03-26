package com.ferenc.reservation.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Marker;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.exception.NoSuchBookingException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;

@ExtendWith({ MockitoExtension.class })
class LogFilterTest extends AbstractTest {

    @InjectMocks
    LogFilter logFilter;

    @Test
    void decide() {
        final Marker marker = PODAM_FACTORY.manufacturePojo(Marker.class);
        final Logger logger = PODAM_FACTORY.manufacturePojo(Logger.class);
        final Level level = PODAM_FACTORY.manufacturePojo(Level.class);
        final String format = PODAM_FACTORY.manufacturePojo(String.class);
        final Object[] params = PODAM_FACTORY.manufacturePojo(Object[].class);
        final String exceptionMessage = PODAM_FACTORY.manufacturePojo(String.class);
        final String causeMessage = PODAM_FACTORY.manufacturePojo(String.class);
        final List<StackTraceElement> mockedStackTraceElements = PODAM_FACTORY.manufacturePojo(List.class, StackTraceElement.class);

        final Throwable throwable = new NoSuchBookingException(exceptionMessage, new RuntimeException(causeMessage));
        final StackTraceElement[] expectedStackTrace = mockedStackTraceElements.toArray(StackTraceElement[]::new);
        addExcludedPackageLinesToStackTrace(mockedStackTraceElements);
        final StackTraceElement[] fullStackTrace = mockedStackTraceElements.toArray(StackTraceElement[]::new);
        throwable.setStackTrace(fullStackTrace);
        throwable.getCause().setStackTrace(fullStackTrace);

        assertThat(logFilter.decide(marker, logger, level, format, params, throwable)).isEqualTo(
                FilterReply.NEUTRAL);
        assertThat(throwable).isExactlyInstanceOf(NoSuchBookingException.class).hasMessage(exceptionMessage)
                .hasCauseExactlyInstanceOf(RuntimeException.class).hasRootCauseMessage(causeMessage);
        assertThat(throwable.getStackTrace()).isEqualTo(expectedStackTrace);
        assertThat(throwable.getCause().getStackTrace()).isEqualTo(expectedStackTrace);
    }

    private void addExcludedPackageLinesToStackTrace(final List<StackTraceElement> stackTrace) {
        final List<StackTraceElement> excludedStackTraceElementList = logFilter.getExcludedPackages().stream()
                .map(packageName -> new StackTraceElement(
                        packageName.concat(PODAM_FACTORY.manufacturePojo(String.class)),
                        PODAM_FACTORY.manufacturePojo(String.class),
                        PODAM_FACTORY.manufacturePojo(String.class),
                        PODAM_FACTORY.manufacturePojo(Integer.class)))
                .collect(Collectors.toList());
        stackTrace.addAll(excludedStackTraceElementList);
    }

}
