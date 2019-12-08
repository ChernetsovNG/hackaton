package ru.rosbank.hackathon.bonusSystem.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.ClientResponse;
import ru.rosbank.hackathon.bonusSystem.service.BonusService;
import ru.rosbank.hackathon.bonusSystem.service.ClientService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    private ClientController(ClientService clientService) {
        this.clientService = clientService;
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

    @GetMapping
    public List<ClientResponse> getAll() {
        List<BigDecimal> amounts = clientService.getAllBonuses();
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty() || amounts.isEmpty()) return Collections.emptyList();

        List<ClientResponse> response = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++) {
            BigDecimal currAmount = amounts.get(i);
            Client currClient = clients.get(i);
            response.add(currClient.toClientResponse(currAmount));
        }
        return response;
    }
}
