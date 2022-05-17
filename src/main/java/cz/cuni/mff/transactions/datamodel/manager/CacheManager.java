package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.io.IOException;
import java.util.*;

public class CacheManager {

    private final Map<Integer, Integer> cache = new HashMap<>();
    private final Map<ITransaction, Set<Integer>> writtenPages = new HashMap<>();
    private final int dataLength;
    private final DatabaseManager databaseManager;
    
    private final LogManager logManager;

    public CacheManager(DatabaseManager databaseManager, LogFlush logFlush, int dataLength) {
        this.dataLength = dataLength;
        this.databaseManager = databaseManager;
        this.logManager = new LogManager(logFlush);
    }

    public void doWrite(ITransaction transaction, int index) {
        if (index < 0 || index >= dataLength) {
            System.err.printf("Invalid index %d for a write operation in transaction %s (operation %d)\n", index, 
                    transaction.toString(), transaction.getIndex() + 1);
            return;
        }
        
        if (!cache.containsKey(index)) {
            cache.put(index, databaseManager.read(index));
        }
        
        logManager.addEvent(transaction, TransactionAction.WRITE, index, cache.get(index), cache.get(index)+1);
        cache.put(index, cache.get(index)+1);
        // update written pages
        writtenPages.computeIfAbsent(transaction, k -> new HashSet<>());
        writtenPages.get(transaction).add(index);
    }
    
    public void doCommit(ITransaction transaction) {
        // commit to persistent storage
        for (int page : writtenPages.getOrDefault(transaction, new HashSet<>())) {
            databaseManager.write(page, cache.get(page));
        }
        // flush
        logManager.flushLog(transaction);
        // cleanup
        for (int page : writtenPages.getOrDefault(transaction, new HashSet<>())) {
            cache.remove(page);
        }
        writtenPages.remove(transaction);
    }

    public boolean doAbort(ITransaction transaction) {
        List<HistoryEvent> events = logManager.abortTransaction(transaction);
        // undo
        for (HistoryEvent event : events) {
            cache.put(event.getIndex(), event.getOldValue());
        }
        return true;
    }
    
    public void createCheckpoint() {

    }

}
