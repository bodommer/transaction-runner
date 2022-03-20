package cz.cuni.mff.transactions.model;

import java.util.List;

public class BasicTransaction extends AbstractTransaction {

    public BasicTransaction(String name, int arrayLength, List<TransactionAction> actions, List<Integer> actionIndex) {
        super(name, arrayLength, actions, actionIndex);
    }

    // No overwrite of either read or write
}
