package cz.cuni.mff.transactions;

import cz.cuni.mff.transactions.transaction.Transaction;

public class Main {

    public static final int ARRAY_LENGTH = 3;
    public static final int TRANSACTION_COUNT = 3;
    public static final int TRANSACTION_LENGTH = 20;
    //public static final Class<? extends AbstractTransaction> TRANSACTION_TYPE = BasicTransaction.class;
    public static final Class<? extends Transaction> TRANSACTION_TYPE = LockingTransaction.class;

    public static void main(String[] args) {
        try {
            TransactionBroker.run(TRANSACTION_TYPE, TRANSACTION_COUNT, ARRAY_LENGTH, TRANSACTION_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
