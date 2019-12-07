package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BonusRepository extends JpaRepository<BonusEntity, UUID> {

    List<BonusEntity> findAllByClientId(UUID clientId);

    List<BonusEntity> findAllByClientIdAndCreateTimeGreaterThanEqual(UUID clientId, OffsetDateTime fromDate);

    List<BonusEntity> findAllByClientIdAndCreateTimeLessThanEqual(UUID clientId, OffsetDateTime toDate);

    List<BonusEntity> findAllByClientIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(
            UUID clientId, OffsetDateTime fromDate, OffsetDateTime toDate);
}
