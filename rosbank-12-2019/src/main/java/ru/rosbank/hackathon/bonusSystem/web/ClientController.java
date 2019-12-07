package ru.rosbank.hackathon.bonusSystem.web;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.service.ClientService;

import java.util.List;

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
}
