package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.aggregate.ClientAggregate;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;
import ru.rosbank.hackathon.bonusSystem.repository.TariffPlanRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private static final String CLIENT_AGGREGATES_QUERY = "SELECT c.id AS client_id, c.first_name AS client_first_name, " +
            "c.last_name AS client_last_name, c.tariff_plan_id AS client_tariff_plan_id, sum(b.amount) as bonus_count FROM " +
            "clients c LEFT JOIN bonuses b ON b.client_id = c.id GROUP BY c.id";

    private final ClientRepository clientRepository;

    private final TariffPlanRepository tariffPlanRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ClientService(ClientRepository clientRepository, TariffPlanRepository tariffPlanRepository,
                         NamedParameterJdbcTemplate jdbcTemplate) {
        this.clientRepository = clientRepository;
        this.tariffPlanRepository = tariffPlanRepository;
        this.jdbcTemplate = jdbcTemplate;
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

    @Transactional(readOnly = true)
    public Client get(UUID id) {
        return clientRepository.findById(id)
                .map(ClientEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public List<ClientAggregate> getAllClientAggregates() {
        List<ClientAggregate> clientAggregates = jdbcTemplate.query(CLIENT_AGGREGATES_QUERY, (rs, rowNum) -> {
            ClientAggregate clientAggregate = new ClientAggregate();
            Client client = new Client();
            String clientId = rs.getString("client_id");
            String firstName = rs.getString("client_first_name");
            String lastName = rs.getString("client_last_name");
            String tariffPlanId = rs.getString("client_tariff_plan_id");
            Double bonusCount = rs.getDouble("bonus_count");
            client.setUuid(UUID.fromString(clientId));
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setTariffPlanId(UUID.fromString(tariffPlanId));
            clientAggregate.setClient(client);
            clientAggregate.setBonusCount(bonusCount);
            return clientAggregate;
        });
        addTariffPlanToClients(clientAggregates);
        return clientAggregates;
    }

    private void addTariffPlanToClients(List<ClientAggregate> clientAggregates) {
        if (clientAggregates.isEmpty()) {
            return;
        }
        Set<UUID> tariffPlanIds = clientAggregates.stream()
                .map(clientAggregate -> clientAggregate.getClient().getTariffPlanId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<TariffPlanEntity> tariffPlanEntities = tariffPlanRepository.findAllById(tariffPlanIds);
        Map<UUID, TariffPlan> tariffPlansMap = tariffPlanEntities.stream()
                .map(TariffPlanEntity::toDomain)
                .collect(Collectors.toMap(TariffPlan::getUuid, Function.identity()));

        for (ClientAggregate clientAggregate : clientAggregates) {
            UUID tariffPlanId = clientAggregate.getClient().getTariffPlanId();
            TariffPlan tariffPlan = tariffPlansMap.get(tariffPlanId);
            clientAggregate.getClient().setTariffPlan(tariffPlan);
        }
    }
}
