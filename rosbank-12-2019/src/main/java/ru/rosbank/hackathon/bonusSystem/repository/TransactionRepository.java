package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findAllByClientId(UUID clientId);

    List<TransactionEntity> findAllByTimeGreaterThanEqualAndTimeLessThanEqual(OffsetDateTime fromTime, OffsetDateTime toTime);
}
