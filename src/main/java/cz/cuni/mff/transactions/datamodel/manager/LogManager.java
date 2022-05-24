package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.structure.Checkpoint;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.*;

import static cz.cuni.mff.transactions.datamodel.TransactionAction.FETCH;

public class LogManager {

    private final List<HistoryEvent> events = new ArrayList<>();
    private final Map<ITransaction, HistoryEvent> latestEvents = new HashMap<>();
    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();

    private final LogFlush logFlush;

    public LogManager(LogFlush logFlush) {
        this.logFlush = logFlush;
    }

    public void addEvent(ITransaction transaction, TransactionAction action, int index, int oldValue, int newValue) {
        HistoryEvent event = new HistoryEvent(action, index, transaction, oldValue, newValue,
                latestEvents.getOrDefault(transaction, null));
        events.add(event);
        latestEvents.put(transaction, event);
    }

    public List<HistoryEvent> abortTransaction(ITransaction transaction) {
        Queue<HistoryEvent> transactionEvents = new ArrayDeque<>();
        HistoryEvent lastEvent = latestEvents.getOrDefault(transaction, null);
        while (lastEvent != null) {
            transactionEvents.add(lastEvent);
            events.remove(lastEvent);
            lastEvent = lastEvent.getPreviousEvent();
        }
        latestEvents.remove(transaction);
        return new ArrayList<>(transactionEvents);
    }

    public void flushLog(ITransaction transaction) {
        events.stream().filter(event -> event.getTransaction() != null && event.getTransaction().equals(transaction))
                .filter(event -> event.getAction() != FETCH).forEach(logFlush::flush);
    }
    
    public LogFlush getLogFlush() {
        return logFlush;
    }
    
    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }
    
    public Checkpoint getLastCheckpoint() {
        if (checkpoints.isEmpty()) {
            return null;
        }
        return checkpoints.get(checkpoints.size() - 1);
    }

}
