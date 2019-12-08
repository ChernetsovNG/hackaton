package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    @Query("SELECT b.amount FROM BonusEntity b JOIN ClientEntity c ON b.clientId = c.uuid")
    List<BigDecimal> getClientBonusesAmount();

    @Modifying
    @Query("update ClientEntity c set c.tariffPlanId = :tariffPlanId WHERE c.uuid = :clientId")
    void linkClientToTariffPlan(@Param("clientId") UUID clientId, @Param("tariffPlanId") UUID tariffPlanId);
}
