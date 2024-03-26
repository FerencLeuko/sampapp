package com.ferenc.reservation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.ferenc.reservation", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchRuleTests {

    @ArchTest
    static final ArchRule layers =
            layeredArchitecture().consideringAllDependencies()

                    .layer("Controller").definedBy("..controller..")
                    .layer("BusinessService").definedBy("..businessservice..")
                    .optionalLayer("Service").definedBy("..service..")
                    .layer("Repository").definedBy("..repository..")
                    .optionalLayer("Mapper").definedBy("..mapper..")

                    .whereLayer("Controller").mayOnlyBeAccessedByLayers("Mapper")
                    .whereLayer("BusinessService").mayOnlyBeAccessedByLayers("Controller")
                    .whereLayer("Repository").mayOnlyBeAccessedByLayers("BusinessService", "Service", "Mapper");

    @ArchTest
    static final ArchRule apis_should_be_interfaces = classes()
            .that().haveNameMatching(".*Api")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule controllers_should_not_be_interfaces = classes()
            .that().haveNameMatching(".*Controller")
            .should().notBeInterfaces();

    @ArchTest
    static final ArchRule controllers_should_be_annotated = classes()
            .that().haveNameMatching(".*Controller")
            .should().beAnnotatedWith(Controller.class).orShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule controllers_should_reside_in_controller_package = classes()
            .that().haveNameMatching(".*Controller")
            .should().resideInAPackage("..controller");

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories = classes()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideOutsideOfPackage("..repository..");

    @ArchTest
    static final ArchRule business_services_should_reside_in_business_service_package = classes()
            .that().haveNameMatching(".*BusinessService*")
            .should().resideInAPackage("..businessservice");

    @ArchTest
    static final ArchRule business_services_should_only_be_accessed_by_controllers = classes()
            .that().resideInAPackage("..businessservice..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..businessservice..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers = classes()
            .that().resideInAnyPackage("..service..", "..businessservice..")
            .should().dependOnClassesThat().resideOutsideOfPackage("..controller..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_mappers = classes()
            .that().resideInAnyPackage("..service..", "..businessservice..")
            .should().dependOnClassesThat().resideOutsideOfPackage("..mapper..");

    @ArchTest
    static final ArchRule amqp_services_should_only_be_accessed_by_amqp_and_business_services = classes()
            .that().resideInAPackage("..amqp.service..")
            .should().onlyBeAccessed().byAnyPackage("..amqp..", "..businessservice..");

    @ArchTest
    static final ArchRule repositories_should_reside_in_repository_package = classes()
            .that().haveNameMatching(".*Repository")
            .should().resideInAPackage("..repository");
    @ArchTest
    static final ArchRule respositories_should_only_be_accessed_by_services = classes()
            .that().resideInAPackage("..repository")
            .should().onlyBeAccessed().byAnyPackage("..businessservice", "..service", "..repository..");
}
