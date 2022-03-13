package cz.cuni.mff.transactions;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.Transaction;
import cz.cuni.mff.transactions.runner.TransactionRunner;
import cz.cuni.mff.transactions.util.PrettyPrinter;
import cz.cuni.mff.transactions.util.TransactionGenerator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final int ARRAY_LENGTH = 5;
    public static final int TRANSACTION_COUNT = 3;
    public static final int TRANSACTION_LENGTH = 200;

    public static void main(String[] args) {

        TransactionGenerator.defineRandom(123);

        List<Transaction> transactionList = new ArrayList<>();
        for (int i = 0; i < TRANSACTION_COUNT; i++) {
            transactionList.add(TransactionGenerator.generate(ARRAY_LENGTH, TRANSACTION_LENGTH));
        }

        DataManager parallelDataManager = new DataManager("PARALLEL", ARRAY_LENGTH);
        TransactionRunner.PARALLEL.run(transactionList, parallelDataManager);

        DataManager serialDataManager = new DataManager("SERIAL", ARRAY_LENGTH);
        TransactionRunner.SERIAL.run(transactionList, serialDataManager);

        PrettyPrinter.printResults(ARRAY_LENGTH, transactionList, parallelDataManager, serialDataManager);


        System.exit(0);
    }

}
