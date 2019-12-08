package ru.rosbank.hackathon.bonusSystem.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.aggregate.ClientAggregate;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.service.ClientService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    private ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAll() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public Client get(@PathVariable("id") UUID id) {
        return clientService.get(id);
    }

    @PutMapping("/link")
    public ResponseEntity<?> linkClientToTariffPlan(@RequestParam("clientId") UUID clientId,
                                                    @RequestParam("tariffPlanId") UUID tariffPlanId) {
        clientService.linkToTariffPlan(clientId, tariffPlanId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/aggregates")
    public List<ClientAggregate> getClientsWithData() {
        List<ClientAggregate> clientAggregates = clientService.getAllClientAggregates();
        clientService.addTariffPlanToClients(clientAggregates);
        return clientAggregates;
    }
}
