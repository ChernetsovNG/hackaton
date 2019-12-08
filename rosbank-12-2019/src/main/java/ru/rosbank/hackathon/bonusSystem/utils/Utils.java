package ru.rosbank.hackathon.bonusSystem.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;
import static ru.rosbank.hackathon.bonusSystem.utils.TransactionExt.convertToExt;

public class Utils {

    private static final String FILE_NAME = "/home/nchernetsov/Education/hackaton/rosbank-12-2019/src/main/resources/test-file.json";

    private static final AtomicInteger index = new AtomicInteger(0);

    public static void main(String[] args) {
        createTransactionsFile();
    }

    public static void createTransactionsFile() {
        List<Transaction> transactions = new ArrayList<>();
        Map<UUID, Client> clients = new HashMap<>();
        Client client1 = createClient("Nikita1", "Chernetsov1");
        Client client2 = createClient("Ilon1", "Mask1");
        clients.put(client1.getUuid(), client1);
        clients.put(client2.getUuid(), client2);
        for (int i = 0; i < 10; i++) {
            transactions.add(createTransaction(client1));
        }
        for (int i = 0; i < 20; i++) {
            transactions.add(createTransaction(client2));
        }
        List<TransactionExt> transactionExts = transactions.stream()
                .map(transaction -> convertToExt(transaction, clients))
                .collect(Collectors.toList());

        ObjectWriter writer = OBJECT_MAPPER.writer(new DefaultPrettyPrinter());

        try {
            writer.writeValue(new File(FILE_NAME), transactionExts);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Client createClient(String firstName, String lastName) {
        Client client = new Client();
        client.setUuid(UUID.randomUUID());
        client.setFirstName(firstName);
        client.setLastName(lastName);
        return client;
    }

    private static Transaction createTransaction(Client client) {
        Transaction transaction = new Transaction();
        transaction.setUuid(UUID.randomUUID());
        transaction.setClientId(client.getUuid());

        transaction.setAmount(BigDecimal.valueOf(randomDouble(0.0, 5000.0)));
        transaction.setCurrency("RUB");
        transaction.setMarketId(UUID.randomUUID());
        transaction.setMcc(randomMCC());
        transaction.setTime(getTransactionTime(index.getAndIncrement(), 5));
        return transaction;
    }

    private static OffsetDateTime getTransactionTime(int index, int deltaMin) {
        return OffsetDateTime.now().minusHours(10).plusMinutes(index * deltaMin);
    }

    private static double randomDouble(double min, double max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return min + (max - min) * random.nextDouble();
    }

    private static int randomMCC() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10000);
    }
}
