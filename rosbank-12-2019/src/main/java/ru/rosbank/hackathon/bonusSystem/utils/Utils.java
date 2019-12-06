package ru.rosbank.hackathon.bonusSystem.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.rosbank.hackathon.bonusSystem.dto.TransactionDto;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

public class Utils {

    public static void main(String[] args) {
        createTransactionsFile();
    }

    public static void createTransactionsFile() {
        TransactionDto transaction1 = new TransactionDto();
        transaction1.setUuid(UUID.randomUUID());
        transaction1.setClientId(UUID.randomUUID());
        transaction1.setAmount(new BigDecimal("123.11"));
        transaction1.setCurrency("RUB");
        transaction1.setMarketId(UUID.randomUUID());
        transaction1.setMcc(1234);
        transaction1.setTime(ZonedDateTime.now());

        TransactionDto transaction2 = new TransactionDto();
        transaction2.setUuid(UUID.randomUUID());
        transaction2.setClientId(UUID.randomUUID());
        transaction2.setAmount(new BigDecimal("126.11"));
        transaction2.setCurrency("EUR");
        transaction2.setMarketId(UUID.randomUUID());
        transaction2.setMcc(2234);
        transaction2.setTime(ZonedDateTime.now());

        TransactionDto transaction3 = new TransactionDto();
        transaction3.setUuid(UUID.randomUUID());
        transaction3.setClientId(UUID.randomUUID());
        transaction3.setAmount(new BigDecimal("323.11"));
        transaction3.setCurrency("USD");
        transaction3.setMarketId(UUID.randomUUID());
        transaction3.setMcc(3234);
        transaction3.setTime(ZonedDateTime.now());

        List<TransactionDto> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        ObjectWriter writer = OBJECT_MAPPER.writer(new DefaultPrettyPrinter());

        try {
            writer.writeValue(new File("/home/nchernetsov/Education/hackaton/rosbank-12-2019/src/main/resources/test-file.json"), transactions);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
