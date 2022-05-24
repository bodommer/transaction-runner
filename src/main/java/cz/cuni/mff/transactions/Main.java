package cz.cuni.mff.transactions;

public class Main {

    public static final int ARRAY_LENGTH = 3;
    public static final int TRANSACTION_COUNT = 3;
    public static final int TRANSACTION_LENGTH = 20;

    public static void main(String[] args) {
        try {
            TransactionBroker.run(TRANSACTION_COUNT, ARRAY_LENGTH, TRANSACTION_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
