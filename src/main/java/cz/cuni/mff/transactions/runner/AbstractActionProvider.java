package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractActionProvider {
    protected List<ITransaction> transactions;

    protected AbstractActionProvider(List<ITransaction> transactions) {
        this.transactions = transactions;
    }

    public abstract ITransaction next();
    
    public List<ITransaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
