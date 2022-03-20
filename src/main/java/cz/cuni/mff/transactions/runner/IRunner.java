package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.ITransaction;

import java.util.Collection;

public interface IRunner {
    void run(Collection<ITransaction> transactions, DataManager dataManager) throws Exception;
}
