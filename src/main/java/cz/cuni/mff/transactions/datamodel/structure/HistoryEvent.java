package cz.cuni.mff.transactions.datamodel.structure;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.transaction.ITransaction;

public class HistoryEvent {
    private final TransactionAction action;
    private final int index;
    private final ITransaction transaction;
    private final int oldValue;
    private final int newValue;
    private final HistoryEvent previousEvent;

    public HistoryEvent(TransactionAction action, int index, ITransaction transaction, int oldValue,
                        int newValue, HistoryEvent previousEvent) {
        this.action = action;
        this.index = index;
        this.transaction = transaction;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.previousEvent = previousEvent;
    }

    public TransactionAction getAction() {
        return action;
    }

    public int getIndex() {
        return index;
    }

    public ITransaction getTransaction() {
        return transaction;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }

    public HistoryEvent getPreviousEvent() {
        return previousEvent;
    }
}
