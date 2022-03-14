package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.*;

public class RecoverabilityEvaluator {

    public static boolean isRecoverable(Set<Transaction> transactions, List<History.HistoryEvent> events) {
        // serializable schedules are recoverable
        if (SerializabilityEvaluator.isSerializable(transactions, events)) {
            return true;
        }

        // 1. Trivial case
        if (transactions.size() < 2) {
            return true;
        }

        // 2. Transaction to index in incidence matrix
        Map<Transaction, Integer> transactionMap = createTransactionMap(transactions);

        // 3. Find conflicts: RW WR WW
        return detectConflicts(events, transactionMap);
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

    @SuppressWarnings("squid:S3776") // doesn't make sense to reduce complexity
    private static boolean detectConflicts(List<History.HistoryEvent> events,
                                           Map<Transaction, Integer> transactionMap) {
        Map<Integer, Set<DirtyRead>> goodCommits = new HashMap<>();
        Map<Integer, Set<DirtyRead>> errorCommits = new HashMap<>();

        for (int i = 0; i < events.size(); i++) {
            History.HistoryEvent eventOne = events.get(i);
            int transactionOneId = transactionMap.get(eventOne.getTransaction());
            if (eventOne.getAction() == Transaction.Action.COMMIT) {
                if (errorCommits.getOrDefault(transactionOneId, new HashSet<>()).isEmpty()) {
                    // correct commit order
                    // remove all 'errorCommit' instances from the other map
                    for (DirtyRead dirtyRead : goodCommits.getOrDefault(transactionOneId, new HashSet<>())) {
                        errorCommits.getOrDefault(dirtyRead.errorIfCommitsFirst, new HashSet<>()).remove(dirtyRead);
                    }
                    // also reset good commits at the transaction
                    goodCommits.put(transactionOneId, new HashSet<>());
                } else {
                    // error commit order - irrecoverable
                    return false;
                }
                continue;
            }
            for (int j = i + 1; j < events.size(); j++) {
                History.HistoryEvent eventTwo = events.get(j);

                // dirty read
                if (eventOne.getTransaction() != eventTwo.getTransaction()
                        && eventOne.getIndex() == eventTwo.getIndex()
                        && (eventOne.getAction() == Transaction.Action.WRITE && eventTwo.getAction() == Transaction.Action.READ)) {
                    // create a new dirty read
                    DirtyRead dirtyRead = new DirtyRead(transactionOneId,
                            transactionMap.get(eventTwo.getTransaction()));

                    // add expected correct commit order
                    Set<DirtyRead> goodDirtyReads = goodCommits.getOrDefault(transactionOneId,
                            new HashSet<>());
                    goodDirtyReads.add(dirtyRead);
                    goodCommits.put(transactionOneId, goodDirtyReads);

                    // add unexpected error commit order
                    Set<DirtyRead> errorDirtyReads = errorCommits.getOrDefault(dirtyRead.errorIfCommitsFirst,
                            new HashSet<>());
                    errorDirtyReads.add(dirtyRead);
                    errorCommits.put(dirtyRead.errorIfCommitsFirst, errorDirtyReads);
                }
            }
        }
        return true;
    }

    private RecoverabilityEvaluator() {
        // NOP
    }

    private static class DirtyRead {
        // unused, but it is complicated to replace this object with just an int
        private final int okIfCommitsFirst;
        private final int errorIfCommitsFirst;

        private DirtyRead(int ok, int error) {
            this.okIfCommitsFirst = ok;
            this.errorIfCommitsFirst = error;
        }
    }
}
