package cz.cuni.mff.transactions.datamodel.structure;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.evaluator.RecoverabilityEvaluator;
import cz.cuni.mff.transactions.datamodel.evaluator.SerializabilityEvaluator;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogFlush {

    private final List<HistoryEvent> log = new ArrayList<>();
    private final Set<ITransaction> transactions = new HashSet<>();

    public void flush(List<HistoryEvent> events) {
        events.forEach(this::flush);
    }
    
    public void flush(HistoryEvent event) {
        log.add(event);
        transactions.add(event.getTransaction());
    }

    public boolean isSerializable() {
        return SerializabilityEvaluator.isSerializable(transactions, log);
    }

    public boolean isRecoverable() {
        return RecoverabilityEvaluator.isRecoverable(transactions, log);
    }

    public void printHistory() {
        for (HistoryEvent event : log) {
            System.out.println(event.getTransaction().toString().substring(2) + " " + getActionCode(event.getAction())
                    + " " + event.getIndex());
        }
    }

    public void printHistoryInLanes() {
        for (HistoryEvent event : log) {
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
}
