package ru.rosbank.hackathon.bonusSystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.properties.DataProperties;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Service
@Slf4j
public class FileService {

    private final DataProperties dataProperties;

    private final TransactionRepository transactionRepository;

    public FileService(DataProperties dataProperties, TransactionRepository transactionRepository) {
        this.dataProperties = dataProperties;
        this.transactionRepository = transactionRepository;
    }

//    @Scheduled(fixedRateString = "${data.scan-frequency-ms}")
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
            List<Transaction> transactions = OBJECT_MAPPER.readValue(
                    file.toFile(), new TypeReference<List<Transaction>>() {
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
    public void saveTransactionsInDatabase(List<Transaction> transactions) {
        List<TransactionEntity> transactionEntities = transactions.stream()
                .map(Transaction::toEntity)
                .collect(Collectors.toList());
        transactionRepository.saveAll(transactionEntities);
    }
}
