package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionGenerator {

    private static final String TRANSACTION_CODE = "TR";

    private static Random random = new Random();

    private TransactionGenerator() {
        // NOP
    }

    public static void defineRandom(int seed) {
        random = new Random(seed);
    }

    public static Transaction generate(int arrayLength, int transactionLength, int transactionId) {
        Boolean[] isRead = new Boolean[arrayLength];
        List<TransactionAction> actions = new ArrayList<>();
        List<Integer> actionIndexes = new ArrayList<>();

        for (int i = 0; i < transactionLength - 1; i++) {
            int index = random.nextInt(arrayLength);
            actionIndexes.add(index);
            if (Boolean.TRUE.equals(isRead[index])) {
                if (random.nextBoolean()) {
                    actions.add(TransactionAction.WRITE);
                } else {
                    actions.add(TransactionAction.READ);
                }
            } else {
                isRead[index] = true;
                actions.add(TransactionAction.READ);
            }
        }

        // last operation either commit (99% chance) or abort (1% chance)
        if (random.nextInt(100) == 0) {
            actions.add(TransactionAction.ABORT);
            actionIndexes.add(0);
        } else {
            actions.add(TransactionAction.COMMIT);
            actionIndexes.add(0);
        }

        return new Transaction(TRANSACTION_CODE + transactionId, actions, actionIndexes);
    }
}
