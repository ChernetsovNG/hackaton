package ru.rosbank.hackathon.bonusSystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.properties.DataProperties;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;
import ru.rosbank.hackathon.bonusSystem.repository.TariffPlanRepository;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;
import ru.rosbank.hackathon.bonusSystem.utils.TransactionExt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;
import static ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity.DEFAULT_TARIFF_PLAN_NAME;

@Service
@Slf4j
public class FileService {

    private final DataProperties dataProperties;

    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final TariffPlanRepository tariffPlanRepository;

    private final KafkaProducerService kafkaProducerService;

    public FileService(DataProperties dataProperties, TransactionRepository transactionRepository,
                       ClientRepository clientRepository, TariffPlanRepository tariffPlanRepository,
                       KafkaProducerService kafkaProducerService) {
        this.dataProperties = dataProperties;
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        this.tariffPlanRepository = tariffPlanRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Scheduled(fixedRateString = "${data.scan-frequency-ms}")
    public void readDataFile() {
        String folder = dataProperties.getFolder();
        log.debug("readDataFile: folder = {}", folder);
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            Map<String, List<Path>> filesByExtension = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.groupingBy(file ->
                            FilenameUtils.getExtension(file.getFileName().toString())));
            // Группируем файлы по расширениям
            List<Path> jsonFiles = filesByExtension.get("json");
            if (jsonFiles != null) {
                jsonFiles.forEach(this::processJsonFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processJsonFile(Path file) {
        try {
            List<TransactionExt> transactions = OBJECT_MAPPER.readValue(
                    file.toFile(), new TypeReference<List<TransactionExt>>() {
                    });
            log.debug("processJsonFile: transactions = {}", transactions);
            if (transactions != null && !transactions.isEmpty()) {
                saveTransactionsInDatabase(transactions);
            }
            // После обработки удаляем файл
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional
    public void saveTransactionsInDatabase(List<TransactionExt> transactions) {
        saveNewClients(transactions);
        List<TransactionEntity> transactionEntities = transactions.stream()
                .map(Transaction::toEntity)
                .collect(Collectors.toList());
        transactionRepository.saveAll(transactionEntities);
        // Отправляем в Kafka события о сохранении транзакций в БД
        for (Transaction transaction : transactions) {
            kafkaProducerService.sendTransactionEvent(transaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewClients(List<TransactionExt> transactions) {
        Set<Client> clientsFromTransactions = transactions.stream()
                .map(transactionExt -> {
                    Client client = new Client();
                    client.setUuid(transactionExt.getClientId());
                    client.setFirstName(transactionExt.getFirstName());
                    client.setLastName(transactionExt.getLastName());
                    return client;
                })
                .collect(Collectors.toSet());

        Map<UUID, Client> clientsMap = clientsFromTransactions.stream()
                .collect(Collectors.toMap(Client::getUuid, Function.identity()));
        clientsMap.forEach((clientId, client) -> {
            if (!clientRepository.existsById(clientId)) {
                saveNewClient(client);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveNewClient(Client client) {
        // такого клиента ещё не было, сохраняем его в БД
        ClientEntity clientEntity = client.toEntity();
        // назначаем ему default тарифный план
        List<TariffPlanEntity> defaultTariffPlans = tariffPlanRepository.findAllByTitle(DEFAULT_TARIFF_PLAN_NAME);
        if (defaultTariffPlans.size() > 0) {
            TariffPlanEntity defaultTariffPlan = defaultTariffPlans.get(0);
            clientEntity.setTariffPlanId(defaultTariffPlan.getUuid());
            clientEntity.setTariffPlan(defaultTariffPlan);
        }
        clientRepository.save(clientEntity);
    }
}
