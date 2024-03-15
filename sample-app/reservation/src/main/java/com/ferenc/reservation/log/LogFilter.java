package com.ferenc.reservation.log;

import java.util.Arrays;
import java.util.Set;

import jakarta.annotation.Nullable;

import org.slf4j.Marker;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter used to define a custom filtering to exclude unnecessary packages from log stack traces
 */
@Component
public class LogFilter extends TurboFilter {

    private final Set<String> excludedPackages = Set.of("org.springframework","org.apache");

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format,
            final Object[] params, @Nullable final Throwable throwable) {
        filterOutStackTrace(throwable);
        return FilterReply.NEUTRAL;
    }

    private void filterOutStackTrace(final Throwable throwable) {
        if (throwable != null) {
            final StackTraceElement[] filteredStackTrace = Arrays.stream(throwable.getStackTrace())
                    .filter(element -> !startsWithAny(element.getClassName(), excludedPackages))
                    .toArray(StackTraceElement[]::new);
            throwable.setStackTrace(filteredStackTrace);
            filterOutStackTrace(throwable.getCause());
        }
    }

    private boolean startsWithAny(final String target, final Set<String> prefixes) {
        for (String prefix : prefixes) {
            if (target.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    protected Set<String> getExcludedPackages() {
        return excludedPackages;
    }
}
