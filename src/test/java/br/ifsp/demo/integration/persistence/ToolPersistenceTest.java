package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.annotation.IntegrationTest;
import br.ifsp.demo.annotation.PersistenceTest;
import br.ifsp.demo.domain.model.ToolStatus;
import br.ifsp.demo.infrastructure.persistence.entity.MaintenanceRecordJpaEntity;
import br.ifsp.demo.infrastructure.persistence.entity.ToolJpaEntity;
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
class ToolPersistenceTest {

    @Autowired
    private ToolJpaRepository toolRepository;

    @AfterEach
    void tearDown() {
        toolRepository.deleteAll();
    }

    @Test
    @DisplayName("should persist and retrieve tool with all fields")
    void shouldPersistAndRetrieveToolWithAllFields() {
        ToolJpaEntity tool = buildTool("Drill", ToolStatus.AVAILABLE);
        toolRepository.save(tool);

        ToolJpaEntity found = toolRepository.findById(tool.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Drill");
        assertThat(found.getStatus()).isEqualTo(ToolStatus.AVAILABLE);
        assertThat(found.getDailyRate()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    @Transactional
    @DisplayName("should persist maintenance record via cascade on tool save")
    void shouldPersistToolWithMaintenanceRecordCascade() {
        ToolJpaEntity tool = buildTool("Saw", ToolStatus.MAINTENANCE);
        MaintenanceRecordJpaEntity record = new MaintenanceRecordJpaEntity(
                UUID.randomUUID().toString(), tool.getId(), LocalDate.now(), null, false);
        tool.getMaintenanceHistory().add(record);

        toolRepository.save(tool);

        ToolJpaEntity found = toolRepository.findById(tool.getId()).orElseThrow();
        assertThat(found.getMaintenanceHistory()).hasSize(1);
        assertThat(found.getMaintenanceHistory().get(0).isClosed()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("should remove maintenance record from DB when removed from tool (orphanRemoval)")
    void shouldDeleteMaintenanceRecordWhenRemovedFromToolOrphanRemoval() {
        ToolJpaEntity tool = buildTool("Grinder", ToolStatus.MAINTENANCE);
        MaintenanceRecordJpaEntity record = new MaintenanceRecordJpaEntity(
                UUID.randomUUID().toString(), tool.getId(), LocalDate.now(), null, false);
        tool.getMaintenanceHistory().add(record);
        toolRepository.save(tool);

        ToolJpaEntity saved = toolRepository.findById(tool.getId()).orElseThrow();
        saved.getMaintenanceHistory().clear();
        toolRepository.save(saved);

        ToolJpaEntity updated = toolRepository.findById(tool.getId()).orElseThrow();
        assertThat(updated.getMaintenanceHistory()).isEmpty();
    }

    @Test
    @DisplayName("should persist multiple tools and find all")
    void shouldPersistMultipleToolsAndFindAll() {
        toolRepository.save(buildTool("Tool A", ToolStatus.AVAILABLE));
        toolRepository.save(buildTool("Tool B", ToolStatus.AVAILABLE));
        toolRepository.save(buildTool("Tool C", ToolStatus.RENTED));

        List<ToolJpaEntity> all = toolRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("should not find tool after deletion")
    void shouldDeleteToolAndNotFindItAfterwards() {
        ToolJpaEntity tool = buildTool("Jigsaw", ToolStatus.AVAILABLE);
        toolRepository.save(tool);

        toolRepository.deleteById(tool.getId());

        assertThat(toolRepository.findById(tool.getId())).isEmpty();
    }

    private ToolJpaEntity buildTool(String name, ToolStatus status) {
        return new ToolJpaEntity(
                UUID.randomUUID().toString(), name, status,
                new BigDecimal("10.00"), new BigDecimal("8.00"), new BigDecimal("6.00"),
                new ArrayList<>());
    }
}
