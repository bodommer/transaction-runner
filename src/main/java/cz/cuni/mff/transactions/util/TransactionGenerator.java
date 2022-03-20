package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.model.AbstractTransaction;
import cz.cuni.mff.transactions.model.ITransaction;
import cz.cuni.mff.transactions.model.TransactionAction;

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

    public static <T extends AbstractTransaction> ITransaction generate(Class<T> targetType, int arrayLength,
                                                                        int transactionLength) {
        Boolean[] isRead = new Boolean[arrayLength];
        List<TransactionAction> actions = new ArrayList<>();
        List<Integer> actionIndexes = new ArrayList<>();

        for (int i = 0; i < transactionLength; i++) {
            int index = random.nextInt(arrayLength);
            actionIndexes.add(index);
            // decide whether create a commit operation or a read/write (3.33% change of commit action)
            // or: every transaction's last operation must be a commit
            if (random.nextInt(30) == 0 || i == transactionLength - 1) {
                actions.add(TransactionAction.COMMIT);
            } else {
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
        }
        try {
            return targetType.getConstructor(String.class, int.class, List.class, List.class).newInstance(
                    TRANSACTION_CODE + generatedTransactions++,
                    arrayLength, actions,
                    actionIndexes);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
