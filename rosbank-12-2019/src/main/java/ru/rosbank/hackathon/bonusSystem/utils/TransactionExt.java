package ru.rosbank.hackathon.bonusSystem.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;

import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionExt extends Transaction {
    private String firstName;
    private String lastName;

    public static TransactionExt convertToExt(Transaction transaction, Map<UUID, Client> clients) {
        TransactionExt transactionExt = new TransactionExt();
        transactionExt.setUuid(transaction.getUuid());
        transactionExt.setClientId(transaction.getClientId());
        transactionExt.setAmount(transaction.getAmount());
        transactionExt.setCurrency(transaction.getCurrency());
        transactionExt.setMarketId(transaction.getMarketId());
        transactionExt.setMcc(transaction.getMcc());
        transactionExt.setTime(transaction.getTime());
        Client client = clients.get(transaction.getClientId());
        if (client != null) {
            transactionExt.setFirstName(client.getFirstName());
            transactionExt.setLastName(client.getLastName());
        }
        return transactionExt;
    }
}
