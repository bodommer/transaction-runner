package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.Transaction;

import java.util.Collection;

public interface IRunner {
    void run(Collection<Transaction> transactions, DataManager dataManager);
}
