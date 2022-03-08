package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.Transaction;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public enum TransactionRunner implements IRunner {
    SERIAL {
        @Override
        public void run(Collection<Transaction> transactions, DataManager dataManager) {
            for (Transaction transaction : transactions) {
                transaction.setConnection(dataManager);
                transaction.call();
            }
        }
    },

    PARALLEL {
        @Override
        @SuppressWarnings("squid:S2142") // handle interrupted exception
        public void run(Collection<Transaction> transactions, DataManager dataManager) {
            transactions.forEach(transaction -> transaction.setConnection(dataManager));
            try {
                ExecutorService service = Executors.newFixedThreadPool(transactions.size());
                service.invokeAll(transactions, 10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
