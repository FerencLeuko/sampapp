package com.ferenc.messaging;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.ferenc.messaging")
class ArchRuleTest {

    @ArchTest
    static final ArchRule flows_should_reside_in_integration_package = classes()
            .that().haveNameMatching(".*Flow")
            .should().resideInAPackage("..integration");

    @ArchTest
    static final ArchRule handlers_should_reside_in_handler_package = classes()
            .that().haveNameMatching(".*Handler")
            .should().resideInAPackage("..handler");

    @ArchTest
    static final ArchRule handlers_should_only_be_accessed_by_integration = classes()
            .that().resideInAPackage("..handler..")
            .should().onlyBeAccessed().byAnyPackage("..integration..", "..handler..");

    @ArchTest
    static final ArchRule handlers_should_not_depend_on_flows = classes()
            .that().haveNameMatching(".*Handler")
            .should().dependOnClassesThat().haveNameNotMatching(".*Flow");

}
