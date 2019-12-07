package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;
import ru.rosbank.hackathon.bonusSystem.repository.TariffPlanRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TariffPlanService {

    private final TariffPlanRepository tariffPlanRepository;

    public TariffPlanService(TariffPlanRepository tariffPlanRepository) {
        this.tariffPlanRepository = tariffPlanRepository;
    }

    @Transactional(readOnly = true)
    public List<TariffPlan> getAll() {
        List<TariffPlanEntity> tariffPlans = tariffPlanRepository.findAll();
        return tariffPlans.stream()
                .map(TariffPlanEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public TariffPlan create(TariffPlan tariffPlan) {
        TariffPlanEntity tariffPlanEntity = tariffPlan.toEntity();
        TariffPlanEntity created = tariffPlanRepository.save(tariffPlanEntity);
        return created.toDomain();
    }

    @Transactional(readOnly = true)
    public TariffPlan get(UUID id) {
        return tariffPlanRepository.findById(id)
                .map(TariffPlanEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }
}
