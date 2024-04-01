package com.ferenc.reservation.it;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAspectJAutoProxy
@Profile("aspectEnabled")
class AspectConfig {

}