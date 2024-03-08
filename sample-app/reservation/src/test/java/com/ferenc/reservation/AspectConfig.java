package com.ferenc.reservation;

import org.junit.jupiter.api.Tag;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAspectJAutoProxy
@Profile("test")
@Tag("IntegrationTest")
public class AspectConfig {

}