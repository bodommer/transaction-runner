package cz.cuni.mff.transactions.datamodel.structure;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Checkpoint {
    private final Map<ITransaction, Integer> deadlockEntries;
    private final Map<ITransaction, Integer> transactionIndexes;
    private final List<ITransaction> exclusiveLocks;
    private final List<List<ITransaction>> sharedLocks;
    private final Map<Integer, Integer> cache;
    private final Map<ITransaction, Set<Integer>> writtenPages;

    public Checkpoint(Map<ITransaction, Integer> deadlocks, Map<ITransaction, Integer> transactionIndexes,
                      List<ITransaction> exclusives, List<List<ITransaction>> shared,
                      Map<Integer, Integer> cache, Map<ITransaction, Set<Integer>> writtenPages) {
        deadlockEntries = deadlocks;
        this.transactionIndexes = transactionIndexes;
        this.exclusiveLocks = exclusives;
        this.sharedLocks = shared;
        this.cache = cache;
        this.writtenPages = writtenPages;
    }

    public Map<ITransaction, Integer> getDeadlockEntries() {
        return deadlockEntries;
    }

    public Map<ITransaction, Integer> getTransactionIndexes() {
        return transactionIndexes;
    }

    public List<ITransaction> getExclusiveLocks() {
        return exclusiveLocks;
    }

    public List<List<ITransaction>> getSharedLocks() {
        return sharedLocks;
    }

    public Map<Integer, Integer> getCache() {
        return cache;
    }

    public Map<ITransaction, Set<Integer>> getWrittenPages() {
        return writtenPages;
    }
}
