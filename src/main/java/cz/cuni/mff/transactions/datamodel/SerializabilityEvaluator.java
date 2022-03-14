package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.*;

public class SerializabilityEvaluator {

    private SerializabilityEvaluator() {
        // NOP
    }

    public static boolean isSerializable(Set<Transaction> transactions, List<History.HistoryEvent> events) {
        // 1. Trivial case
        if (transactions.size() < 2) {
            return true;
        }

        // 2. Transaction to index in incidence matrix
        Map<Transaction, Integer> transactionMap = createTransactionMap(transactions);

        // 3. Find conflicts: RW WR WW
        History.ConflictType[][] incidenceMatrix = detectConflicts(transactions, events, transactionMap);

        // 4. Find starting points for DFS
        List<Integer> emptyRows = getEmptyRows(transactions, incidenceMatrix);

        // 5. No starting points = all transactions are in loops
        if (emptyRows.isEmpty()) {
            return false;
        }

        // 6. Check for inner loops
        return runDFS(emptyRows, transactions, incidenceMatrix);

    }

    private static Map<Transaction, Integer> createTransactionMap(Set<Transaction> transactions) {
        int index = 0;
        Map<Transaction, Integer> map = new HashMap<>();
        for (var transaction : transactions) {
            map.put(transaction, index);
            index++;
        }
        return map;
    }

    private static History.ConflictType[][] detectConflicts(Set<Transaction> transactions,
                                                            List<History.HistoryEvent> events, Map<Transaction,
            Integer> transactionMap) {
        History.ConflictType[][] incidenceMatrix = new History.ConflictType[transactions.size()][transactions.size()];
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                History.HistoryEvent eventOne = events.get(i);
                History.HistoryEvent eventTwo = events.get(j);
                if (eventOne.getAction() == Transaction.Action.COMMIT || eventTwo.getAction() == Transaction.Action.COMMIT) {
                    continue;
                }
                if (eventOne.getTransaction() != eventTwo.getTransaction()
                        && !(isReadAction(eventOne.getAction()) && isReadAction(eventTwo.getAction()))
                        && eventOne.getIndex() == eventTwo.getIndex()) {
                    incidenceMatrix[transactionMap.get(eventTwo.getTransaction())][transactionMap.get(eventOne.getTransaction())] =
                            getType(eventOne, eventTwo);
                }
            }
        }
        return incidenceMatrix;
    }

    private static List<Integer> getEmptyRows(Set<Transaction> transactions,
                                              History.ConflictType[][] incidenceMatrix) {
        List<Integer> emptyRows = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            if (Arrays.stream(incidenceMatrix[i]).allMatch(Objects::isNull)) {
                emptyRows.add(i);
            }
        }
        return emptyRows;
    }

    @SuppressWarnings("squid:S3776") // doesn't make sense to reduce complexity
    private static boolean runDFS(List<Integer> emptyRows, Set<Transaction> transactions,
                                  History.ConflictType[][] incidenceMatrix) {
        for (int start : emptyRows) {
            Deque<Path> stack = new ArrayDeque<>();
            stack.push(new Path(start));
            while (!stack.isEmpty()) {
                Path element = stack.pop();
                for (int i = 0; i < transactions.size(); i++) {
                    if (element.next != i && incidenceMatrix[i][element.next] != null) {
                        if (element.items.contains(i)) {
                            return false;
                        }
                        stack.add(element.yieldNew(i));
                    }
                }
            }
        }
        return true;
    }

    private static boolean isReadAction(Transaction.Action action) {
        return action == Transaction.Action.READ;
    }

    private static History.ConflictType getType(History.HistoryEvent event1, History.HistoryEvent event2) {
        if (event1.getAction() == Transaction.Action.READ) {
            return History.ConflictType.RW;
        } else if (event2.getAction() == Transaction.Action.WRITE) {
            return History.ConflictType.WW;
        }
        return History.ConflictType.WR;
    }

    private static class Path {
        private final Set<Integer> items;
        private final int next;

        public Path(int next) {
            this.next = next;
            items = new HashSet<>();
        }

        public Path(int next, Set<Integer> items) {
            this.next = next;
            this.items = items;
        }

        public Path yieldNew(int next) {
            Path p = new Path(next, new HashSet<>(items));
            p.items.add(next);
            return p;
        }
    }
}
