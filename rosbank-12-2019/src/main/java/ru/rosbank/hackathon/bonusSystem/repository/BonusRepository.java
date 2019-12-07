package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;

import java.util.UUID;

public interface BonusRepository extends JpaRepository<BonusEntity, UUID> {
}
