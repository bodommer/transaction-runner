package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeadlockManager {

    private final Map<ITransaction, List<ITransaction>> deadlockEdges = new HashMap<>();
    
    public void addEdges(ITransaction waitingTransaction, List<ITransaction> waitingFor) {
        // add new edges or create a new K-V pair
    }
    
    public ITransaction detectCycle(ITransaction startingTransaction) {
        // run DFS, detect first cycle and determine the 'youngest' transaction to kill
        // should be run until returns null (no cycle)
        return null;
    }
    
    public void removeTransaction(ITransaction transaction) {
        // remove the transaction from keys and value lists
    }
    
}
