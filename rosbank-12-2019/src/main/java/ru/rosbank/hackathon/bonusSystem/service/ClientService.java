package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        List<ClientEntity> client = clientRepository.findAll();
        return client.stream()
                .map(ClientEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkToTariffPlan(UUID clientId, UUID tariffPlanId) {
        clientRepository.linkClientToTariffPlan(clientId, tariffPlanId);
    }
}
