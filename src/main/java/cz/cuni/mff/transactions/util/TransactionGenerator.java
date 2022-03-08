package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionGenerator {

    private static final String TRANSACTION_CODE = "TR";

    private static Random random = new Random();

    private static int generatedTransactions = 1;

    public static void defineRandom(int seed) {
        random = new Random(seed);
    }

    public static Transaction generate(int arrayLength, int transactionLength) {
        Boolean[] isRead = new Boolean[arrayLength];
        List<Transaction.Action> actions = new ArrayList<>();
        List<Integer> actionIndexes = new ArrayList<>();

        for (int i = 0; i < transactionLength; i++) {
            int index = random.nextInt(arrayLength);
            actionIndexes.add(index);
            if (Boolean.TRUE.equals(isRead[index])) {
                if (random.nextBoolean()) {
                    actions.add(Transaction.Action.WRITE);
                } else {
                    actions.add(Transaction.Action.READ);
                }
            } else {
                isRead[index] = true;
                actions.add(Transaction.Action.READ);
            }
        }
        return new Transaction(TRANSACTION_CODE + generatedTransactions++, arrayLength, actions, actionIndexes);
    }

    private TransactionGenerator() {
        // NOP
    }
}
