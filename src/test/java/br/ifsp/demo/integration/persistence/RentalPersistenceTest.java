package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.annotation.IntegrationTest;
import br.ifsp.demo.annotation.PersistenceTest;
import br.ifsp.demo.domain.model.RentalStatus;
import br.ifsp.demo.domain.model.ToolStatus;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerJpaEntity;
import br.ifsp.demo.infrastructure.persistence.entity.RentalJpaEntity;
import br.ifsp.demo.infrastructure.persistence.entity.ToolJpaEntity;
import br.ifsp.demo.infrastructure.persistence.jpa.CustomerJpaRepository;
import br.ifsp.demo.infrastructure.persistence.jpa.RentalJpaRepository;
import br.ifsp.demo.infrastructure.persistence.jpa.ToolJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@IntegrationTest
@PersistenceTest
class RentalPersistenceTest {

    @Autowired private RentalJpaRepository rentalRepository;
    @Autowired private ToolJpaRepository toolRepository;
    @Autowired private CustomerJpaRepository customerRepository;

    @AfterEach
    void tearDown() {
        rentalRepository.deleteAll();
        toolRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("should persist rental with multiple tools via ManyToMany join table")
    void shouldPersistRentalWithManyToManyToolsInJoinTable() {
        CustomerJpaEntity customer = saveCustomer("Alice");
        ToolJpaEntity tool1 = saveTool("Drill");
        ToolJpaEntity tool2 = saveTool("Saw");

        RentalJpaEntity rental = new RentalJpaEntity(
                UUID.randomUUID().toString(), RentalStatus.ACTIVE,
                LocalDate.now(), null, customer, List.of(tool1, tool2));
        rentalRepository.save(rental);

        RentalJpaEntity found = rentalRepository.findById(rental.getId()).orElseThrow();
        assertThat(found.getTools()).hasSize(2);
    }

    @Test
    @Transactional
    @DisplayName("should persist rental and retrieve associated customer")
    void shouldPersistRentalAndRetrieveCustomer() {
        CustomerJpaEntity customer = saveCustomer("Bob");
        ToolJpaEntity tool = saveTool("Grinder");

        RentalJpaEntity rental = new RentalJpaEntity(
                UUID.randomUUID().toString(), RentalStatus.ACTIVE,
                LocalDate.now(), null, customer, List.of(tool));
        rentalRepository.save(rental);

        RentalJpaEntity found = rentalRepository.findById(rental.getId()).orElseThrow();
        assertThat(found.getCustomer().getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("should persist rental status transition to FINALIZED")
    void shouldPersistRentalStatusTransitionToFinalized() {
        CustomerJpaEntity customer = saveCustomer("Carol");
        ToolJpaEntity tool = saveTool("Router");

        RentalJpaEntity rental = new RentalJpaEntity(
                UUID.randomUUID().toString(), RentalStatus.ACTIVE,
                LocalDate.now(), null, customer, List.of(tool));
        rentalRepository.save(rental);

        rental.setStatus(RentalStatus.FINALIZED);
        rental.setEndDate(LocalDate.now().plusDays(5));
        rentalRepository.save(rental);

        RentalJpaEntity found = rentalRepository.findById(rental.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(RentalStatus.FINALIZED);
        assertThat(found.getEndDate()).isNotNull();
    }

    @Test
    @DisplayName("should persist rental status transition to CANCELLED")
    void shouldPersistRentalStatusTransitionToCanceled() {
        CustomerJpaEntity customer = saveCustomer("Dave");
        ToolJpaEntity tool = saveTool("Lathe");

        RentalJpaEntity rental = new RentalJpaEntity(
                UUID.randomUUID().toString(), RentalStatus.ACTIVE,
                LocalDate.now(), null, customer, List.of(tool));
        rentalRepository.save(rental);

        rental.setStatus(RentalStatus.CANCELLED);
        rentalRepository.save(rental);

        RentalJpaEntity found = rentalRepository.findById(rental.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(RentalStatus.CANCELLED);
    }

    @Test
    @DisplayName("should count all rentals associated to a customer")
    void shouldCountRentalsPerCustomer() {
        CustomerJpaEntity customer = saveCustomer("Eve");
        ToolJpaEntity tool1 = saveTool("Planer");
        ToolJpaEntity tool2 = saveTool("Sander");

        rentalRepository.save(new RentalJpaEntity(UUID.randomUUID().toString(),
                RentalStatus.ACTIVE, LocalDate.now(), null, customer, List.of(tool1)));
        rentalRepository.save(new RentalJpaEntity(UUID.randomUUID().toString(),
                RentalStatus.FINALIZED, LocalDate.now(), LocalDate.now().plusDays(2), customer, List.of(tool2)));

        long count = rentalRepository.findAll().stream()
                .filter(r -> r.getCustomer().getId().equals(customer.getId()))
                .count();

        assertThat(count).isEqualTo(2);
    }

    private CustomerJpaEntity saveCustomer(String name) {
        return customerRepository.save(new CustomerJpaEntity(UUID.randomUUID().toString(), name));
    }

    private ToolJpaEntity saveTool(String name) {
        return toolRepository.save(new ToolJpaEntity(UUID.randomUUID().toString(), name, ToolStatus.AVAILABLE,
                new BigDecimal("10.00"), new BigDecimal("8.00"), new BigDecimal("6.00"), new ArrayList<>()));
    }
}
