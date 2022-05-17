package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.transaction.Transaction;
import cz.cuni.mff.transactions.transaction.ITransaction;
import cz.cuni.mff.transactions.datamodel.TransactionAction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionGenerator {

    private static final String TRANSACTION_CODE = "TR";

    private static Random random = new Random();

    private static int generatedTransactions = 1;

    private TransactionGenerator() {
        // NOP
    }

    public static void defineRandom(int seed) {
        random = new Random(seed);
    }

    public static <T extends Transaction> ITransaction generate(Class<T> targetType, int arrayLength,
                                                                int transactionLength) {
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
        
        // last operation either commit (98% chance) or abort (2% chance)
        if (random.nextInt(50) == 0) {
            actions.add(TransactionAction.ABORT);
            actionIndexes.add(0);
        } else {
            actions.add(TransactionAction.COMMIT);
            actionIndexes.add(0);
        }
        
        try {
            return targetType.getConstructor(String.class, int.class, List.class, List.class).newInstance(
                    TRANSACTION_CODE + generatedTransactions++,
                    arrayLength, actions,
                    actionIndexes);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
