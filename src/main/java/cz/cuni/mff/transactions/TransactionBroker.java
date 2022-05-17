package cz.cuni.mff.transactions;

import cz.cuni.mff.transactions.datamodel.Engine;
import cz.cuni.mff.transactions.datamodel.manager.DatabaseManager;
import cz.cuni.mff.transactions.transaction.Transaction;
import cz.cuni.mff.transactions.transaction.ITransaction;
import cz.cuni.mff.transactions.util.PermutationGenerator;
import cz.cuni.mff.transactions.util.PrettyPrinter;
import cz.cuni.mff.transactions.util.TransactionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionBroker {

    public static <T extends Transaction> void run(Class<T> targetType, int transactionCount,
                                                   int arrayLength,
                                                   int transactionLength) throws Exception {
        TransactionGenerator.defineRandom(123);

        List<ITransaction> transactionList = new ArrayList<>();
        for (int i = 0; i < transactionCount; i++) {
            transactionList.add(TransactionGenerator.generate(targetType, arrayLength, transactionLength));
        }
        transactionList = transactionList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        Engine engine = new Engine("PARALLEL", arrayLength, TransactionRunner.PSEUDO_PARALLEL);

        List<List<ITransaction>> transactionPermutations = new ArrayList<>();
        PermutationGenerator.generatePermutations(transactionPermutations, transactionList, transactionList.size(),
                null);

        DatabaseManager[] dataManagers = new DatabaseManager[1 + transactionPermutations.size()];
        dataManagers[0] = parallelDataManager;

        for (int i = 0; i < transactionPermutations.size(); i++) {
            List<ITransaction> transactions = transactionPermutations.get(i);
            DatabaseManager dataManager = new DatabaseManager("SERIAL " + PermutationGenerator.getRunName(transactions),
                    arrayLength);
            engine.run();
            dataManagers[i + 1] = dataManager;
        }

        PrettyPrinter.printResults(arrayLength, transactionList, dataManagers);
    }

    private TransactionBroker() {
        // NOP
    }
}
