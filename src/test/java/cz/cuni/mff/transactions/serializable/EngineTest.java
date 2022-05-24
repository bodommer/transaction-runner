package cz.cuni.mff.transactions.serializable;

import cz.cuni.mff.transactions.datamodel.Engine;
import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.manager.DatabaseManager;
import cz.cuni.mff.transactions.datamodel.manager.LogManager;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.runner.PseudoRandomActionProvider;
import cz.cuni.mff.transactions.transaction.ITransaction;
import cz.cuni.mff.transactions.util.TransactionGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineTest {

    // 12 abort
    // 11 ok abort

    @ParameterizedTest
    @CsvSource({
            "3,3,20,11", // non-aborted execution
            "3,3,20,13", // aborted execution
            "6,10,25,13", // a larger example with many 'crashes'
            "6, 25, 25, 13"
    })
    void simpleTransactions(int transactionCount, int arrayLength, int transactionLength, int abortSeed) {
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
        Engine.systemShutdown = new Random(abortSeed);
        while (!result) {
            try {
                result = engine.run();
            } catch (RuntimeException e) {
                System.out.println("Engine crashed!");
                engine = new Engine(arrayLength, databaseManager, new PseudoRandomActionProvider(new ArrayList<>(transactionList), transactionLength), logManager);
            }
        }

        List<Integer> simulated = simulateFlushedLog(arrayLength, logManager.getLogFlush());

        logManager.getLogFlush().printHistoryInLanes();
        
        System.out.println("DATABASE: " + databaseManager.listValues(logManager.getLogFlush()));
        System.out.println("LOG DATA: " + toString(simulated));
        for (int i = 0; i < arrayLength; i++) {
            assertEquals(simulated.get(i), databaseManager.read(i));
        }
    }

    private static List<Integer> simulateFlushedLog(int arrayLength, LogFlush logFlush) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < arrayLength; i++) {
            values.add(0);
        }
        for (var write : logFlush.getLog()) {
            if (write.getAction() == TransactionAction.WRITE) {
                values.set(write.getIndex(), values.get(write.getIndex()) + 1);
            }
        }
        return values;
    }

    private static String toString(List<Integer> values) {
        StringBuilder builder = new StringBuilder();
        for (Integer val : values) {
            builder.append(String.format("%3d", val));
            builder.append("  ");
        }
        return builder.toString();
    }

}
