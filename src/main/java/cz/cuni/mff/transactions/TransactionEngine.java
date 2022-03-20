package cz.cuni.mff.transactions;

import cz.cuni.mff.transactions.datamodel.DataManager;
import cz.cuni.mff.transactions.model.AbstractTransaction;
import cz.cuni.mff.transactions.model.ITransaction;
import cz.cuni.mff.transactions.runner.TransactionRunner;
import cz.cuni.mff.transactions.util.PermutationGenerator;
import cz.cuni.mff.transactions.util.PrettyPrinter;
import cz.cuni.mff.transactions.util.TransactionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionEngine {

    public static <T extends AbstractTransaction> void run(Class<T> targetType, int transactionCount,
                                                           int arrayLength,
                                                           int transactionLength) throws Exception {
        TransactionGenerator.defineRandom(123);

        List<ITransaction> transactionList = new ArrayList<>();
        for (int i = 0; i < transactionCount; i++) {
            transactionList.add(TransactionGenerator.generate(targetType, arrayLength, transactionLength));
        }
        transactionList = transactionList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        DataManager parallelDataManager = new DataManager("PARALLEL", arrayLength);
        TransactionRunner.PARALLEL.run(transactionList, parallelDataManager);

        List<List<ITransaction>> transactionPermutations = new ArrayList<>();
        PermutationGenerator.generatePermutations(transactionPermutations, transactionList, transactionList.size(),
                null);

        DataManager[] dataManagers = new DataManager[1 + transactionPermutations.size()];
        dataManagers[0] = parallelDataManager;

        for (int i = 0; i < transactionPermutations.size(); i++) {
            List<ITransaction> transactions = transactionPermutations.get(i);
            DataManager dataManager = new DataManager("SERIAL " + PermutationGenerator.getRunName(transactions),
                    arrayLength);
            TransactionRunner.SERIAL.run(transactions, dataManager);
            dataManagers[i + 1] = dataManager;
        }

        PrettyPrinter.printResults(arrayLength, transactionList, dataManagers);
    }

    private TransactionEngine() {
        // NOP
    }
}
