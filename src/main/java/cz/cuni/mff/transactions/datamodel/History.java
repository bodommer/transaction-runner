package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.*;

public class History {

    private final List<HistoryEvent> events = new ArrayList<>();
    private final Set<Transaction> transactions = new HashSet<>();

    public void addEvent(Transaction transaction, Transaction.Action action, int index) {
        transactions.add(transaction);
        events.add(new HistoryEvent(action, index, transaction));
    }

    public boolean isSerializable() {
        if (transactions.size() < 2) {
            return false;
        }

        int index = 0;
        Map<Transaction, Integer> transactionMap = new HashMap<>();
        for (var transaction : transactions) {
            transactionMap.put(transaction, index);
        }

        // RW WR WW
        ConflictType[][] incidenceMatrix = new ConflictType[transactions.size()][transactions.size()];
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                HistoryEvent eventOne = events.get(i);
                HistoryEvent eventTwo = events.get(j);
                if (eventOne.action == Transaction.Action.COMMIT || eventTwo.action == Transaction.Action.COMMIT) {
                    continue;
                }
                if (eventOne.transaction != eventTwo.transaction
                        && !(isRead(eventOne.action) && isRead(eventTwo.action))
                        && eventOne.index == eventTwo.index) {
                    incidenceMatrix[transactionMap.get(eventTwo.transaction)][transactionMap.get(eventOne.transaction)] = getType(eventOne, eventTwo);
                }
            }
        }

        List<Integer> emptyRows = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            if (Arrays.stream(incidenceMatrix[i]).allMatch(Objects::isNull)) {
                emptyRows.add(i);
            }
        }

        if (emptyRows.isEmpty()) {
            return false;
        }

        for (int start : emptyRows) {
            Deque<Integer> stack = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            stack.push(start);
            while (!stack.isEmpty()) {
                int element = stack.pop();
                visited.add(element);
                for (int i = 0; i < transactions.size(); i++) {
                    if (element != i && incidenceMatrix[i][element] != null) {
                        if (visited.contains(i)) {
                            return false;
                        } else {
                            stack.add(i);
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean isRead(Transaction.Action action) {
        return action == Transaction.Action.READ;
    }

    private static ConflictType getType(HistoryEvent event1, HistoryEvent event2) {
        if (event1.action == Transaction.Action.READ) {
            return ConflictType.RW;
        } else if (event2.action == Transaction.Action.WRITE) {
            return ConflictType.WW;
        }
        return ConflictType.WR;
    }

    private static class HistoryEvent {
        private final Transaction.Action action;
        private final int index;
        private final Transaction transaction;

        private HistoryEvent(Transaction.Action action, int index, Transaction transaction) {
            this.action = action;
            this.index = index;
            this.transaction = transaction;
        }
    }

    private enum ConflictType {
        RW,
        WR,
        WW
    }
}
