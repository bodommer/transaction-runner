package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.HashMap;
import java.util.Map;

public class DeadlockManager {

    private final Map<ITransaction, Integer> lockedTransactions = new HashMap<>();

    public boolean watchTransaction(ITransaction transaction) {
        if (!lockedTransactions.containsKey(transaction)) {
            lockedTransactions.put(transaction, 1);
            return false;
        } else {
            lockedTransactions.put(transaction, lockedTransactions.get(transaction) + 1);
        }
        return lockedTransactions.get(transaction) > 99;
    }
    
    public void unwatchTransaction(ITransaction transaction) {
        lockedTransactions.remove(transaction);
    }
    
    public Map<ITransaction, Integer> getLockedTransactions() {
        return new HashMap<>(lockedTransactions);    
    }
    
    public void reset(Map<ITransaction, Integer> map) {
        lockedTransactions.putAll(map);
    }
    
}
