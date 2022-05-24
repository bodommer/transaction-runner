package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.*;
import java.util.stream.Collectors;

public class CacheManager {

    private final Map<Integer, Integer> cache = new HashMap<>();
    private final Map<ITransaction, Set<Integer>> writtenPages = new HashMap<>();
    private final int dataLength;
    private final DatabaseManager databaseManager;

    private final LogManager logManager;

    public CacheManager(DatabaseManager databaseManager, LogManager logManager, int dataLength) {
        this.dataLength = dataLength;
        this.databaseManager = databaseManager;
        this.logManager = logManager;
    }

    public void doWrite(ITransaction transaction, int index) {
        if (index < 0 || index >= dataLength) {
            System.err.printf("Invalid index %d for a write operation in transaction %s (operation %d)%n", index,
                    transaction.toString(), transaction.getIndex() + 1);
            return;
        }

        if (!cache.containsKey(index)) {
            cache.put(index, databaseManager.read(index));
            logManager.addEvent(transaction, TransactionAction.FETCH, index, 0, 0);
        }

        logManager.addEvent(transaction, TransactionAction.WRITE, index, cache.get(index), cache.get(index) + 1);
        cache.put(index, cache.get(index) + 1);
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
            if (event.getAction() == TransactionAction.WRITE) {
                cache.put(event.getIndex(), event.getOldValue());
            } else if (event.getAction() == TransactionAction.FETCH) {
                cache.remove(event.getIndex());
            }
        }
        return true;
    }

    public Map<Integer, Integer> snapshotCache() {
        return new HashMap<>(cache);
    }

    public Map<ITransaction, Set<Integer>> getDirtyPages() {
        return writtenPages.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new HashSet<>(entry.getValue())));
    }

    public void restore(Map<Integer, Integer> cacheMap, Map<ITransaction, Set<Integer>> written) {
        cache.putAll(cacheMap);
        written.forEach((key, value) -> writtenPages.put(key, new HashSet<>(value)));
    }

}
