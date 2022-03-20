package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.ITransaction;
import cz.cuni.mff.transactions.model.TransactionAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class History {

    private final List<HistoryEvent> events = new ArrayList<>();
    private final Set<ITransaction> transactions = new HashSet<>();

    public void addEvent(ITransaction transaction, TransactionAction action, int index) {
        transactions.add(transaction);
        events.add(new HistoryEvent(action, index, transaction));
    }

    public boolean isSerializable() {
        return SerializabilityEvaluator.isSerializable(transactions, events);
    }

    public boolean isRecoverable() {
        return RecoverabilityEvaluator.isRecoverable(transactions, events);
    }

    public void printHistory() {
        for (HistoryEvent event : events) {
            System.out.println(event.transaction.toString().substring(2) + " " + getActionCode(event.action) + " "
                    + event.getIndex());
        }
    }

    public void printHistoryInLanes() {
        for (HistoryEvent event : events) {
            int transactionId = event.getTransaction().getId();
            System.out.println(" ".repeat(7 * (transactionId - 1)) + getActionCode(event.getAction()) + transactionId
                    + (event.getAction() == TransactionAction.COMMIT ? "" : " X" + event.getIndex()));
        }
    }

    private static String getActionCode(TransactionAction action) {
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
        private final TransactionAction action;
        private final int index;
        private final ITransaction transaction;

        private HistoryEvent(TransactionAction action, int index, ITransaction transaction) {
            this.action = action;
            this.index = index;
            this.transaction = transaction;
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
    }
}
