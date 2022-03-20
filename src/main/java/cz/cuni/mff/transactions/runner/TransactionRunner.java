package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.ITransaction;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public enum TransactionRunner implements IRunner {
    SERIAL {
        @Override
        public void run(Collection<ITransaction> transactions, DataManager dataManager) throws Exception {
            for (ITransaction transaction : transactions) {
                transaction.setConnection(dataManager);
                transaction.call();
            }
        }
    },

    PARALLEL {
        @Override
        @SuppressWarnings("squid:S2142") // handle interrupted exception
        public void run(Collection<ITransaction> transactions, DataManager dataManager) {
            transactions.forEach(transaction -> transaction.setConnection(dataManager));
            try {
                ExecutorService service = Executors.newFixedThreadPool(transactions.size());
                service.invokeAll(transactions, 10, TimeUnit.SECONDS);
            } catch (IllegalStateException | InterruptedException e) {
                System.out.println("ERROR");
                transactions.forEach(ITransaction::reset);
                e.printStackTrace();
            }
        }
    }
}
