package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class History {

    private final List<HistoryEvent> events = new ArrayList<>();
    private final Set<Transaction> transactions = new HashSet<>();

    public void addEvent(Transaction transaction, Transaction.Action action, int index) {
        transactions.add(transaction);
        events.add(new HistoryEvent(action, index, transaction));
    }

    public boolean isSerializable() {
        return SerializabilityEvaluator.isSerializable(transactions, events);
    }

    public void printHistory() {
        for (HistoryEvent event : events) {
            System.out.println(event.transaction.toString().substring(2) + " " + getActionCode(event.action) + " "
                    + event.getIndex());
        }
    }

    private static String getActionCode(Transaction.Action action) {
        switch (action) {
            case READ:
                return "R";
            case WRITE:
                return "W";
            default:
                return "C";
        }
    }

    public enum ConflictType {
        RW,
        WR,
        WW
    }

    public static class HistoryEvent {
        private final Transaction.Action action;
        private final int index;
        private final Transaction transaction;

        private HistoryEvent(Transaction.Action action, int index, Transaction transaction) {
            this.action = action;
            this.index = index;
            this.transaction = transaction;
        }

        public Transaction.Action getAction() {
            return action;
        }

        public int getIndex() {
            return index;
        }

        public Transaction getTransaction() {
            return transaction;
        }
    }
}
