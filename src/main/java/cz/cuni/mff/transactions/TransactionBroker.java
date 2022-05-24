package cz.cuni.mff.transactions;

import cz.cuni.mff.transactions.datamodel.Engine;
import cz.cuni.mff.transactions.datamodel.manager.DatabaseManager;
import cz.cuni.mff.transactions.datamodel.manager.LogManager;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.runner.PseudoRandomActionProvider;
import cz.cuni.mff.transactions.runner.SerialActionProvider;
import cz.cuni.mff.transactions.transaction.ITransaction;
import cz.cuni.mff.transactions.transaction.Transaction;
import cz.cuni.mff.transactions.util.PermutationGenerator;
import cz.cuni.mff.transactions.util.PrettyPrinter;
import cz.cuni.mff.transactions.util.TransactionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionBroker {

    public static void run(int transactionCount,
                                                   int arrayLength,
                                                   int transactionLength) {
        TransactionGenerator.defineRandom(123);

        List<ITransaction> transactionList = new ArrayList<>();
        for (int i = 0; i < transactionCount; i++) {
            transactionList.add(TransactionGenerator.generate(arrayLength, transactionLength, i+1));
        }
        transactionList = transactionList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        LogManager logManager = new LogManager(new LogFlush());
        boolean result = false;
        DatabaseManager databaseManager = new DatabaseManager(arrayLength);
        Engine engine = new Engine(arrayLength, databaseManager, new PseudoRandomActionProvider(new ArrayList<>(transactionList), transactionLength), logManager);
        while (!result) {
            try {
                result = engine.run();
            } catch (RuntimeException e) {
                engine = new Engine(arrayLength, databaseManager, new PseudoRandomActionProvider(new ArrayList<>(transactionList), transactionLength), logManager);
            }
        }

        List<List<ITransaction>> transactionPermutations = new ArrayList<>();
        PermutationGenerator.generatePermutations(transactionPermutations, transactionList, transactionList.size(),
                null);

        Engine[] engines = new Engine[1 + transactionPermutations.size()];
        engines[0] = engine;

        for (int i = 0; i < transactionPermutations.size(); i++) {
            List<ITransaction> transactions = transactionPermutations.get(i);
            for (ITransaction transaction : transactions) {
                transaction.setIndexTo(0);
            }
            LogManager log = new LogManager(new LogFlush());
            DatabaseManager databaseManager1 = new DatabaseManager(arrayLength);
            Engine serialEngine = new Engine(arrayLength, databaseManager1, new SerialActionProvider(transactions), log);
            result = false;
            while (!result) {
                try {
                    result = engine.run();
                } catch (RuntimeException e) {
                    serialEngine = new Engine(arrayLength, databaseManager1, new SerialActionProvider(transactions), log);
                }
            }
            serialEngine.run();
            engines[i + 1] = serialEngine;
        }
        PrettyPrinter.printResults(arrayLength, transactionList, engines);
    }

    private TransactionBroker() {
        // NOP
    }
}
