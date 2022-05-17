package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.*;

public class LogManager {

    private final List<HistoryEvent> events = new ArrayList<>();
    private final Map<ITransaction, HistoryEvent> latestEvents = new HashMap<>();

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
        List<HistoryEvent> transactionEvents = new ArrayList<>();
        HistoryEvent lastEvent = latestEvents.getOrDefault(transaction, null);
        while (lastEvent != null) {
            transactionEvents.add(0, lastEvent);
            transactionEvents.remove(lastEvent);
            lastEvent = lastEvent.getPreviousEvent();
        }
        return transactionEvents;
    }

    public void flushLog(ITransaction transaction) {
        events.stream().filter(tr -> tr.getTransaction().equals(transaction)).forEach(logFlush::flush);
    }
}
