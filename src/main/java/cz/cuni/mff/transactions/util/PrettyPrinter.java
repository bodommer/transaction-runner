package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.datamodel.Engine;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.Collection;

public class PrettyPrinter {

    private PrettyPrinter() {
        // NOP
    }

    @SuppressWarnings("unused")
    public static void printResults(int arrayLength, Collection<ITransaction> transactions, Engine... results) {
        System.out.println("-".repeat(10 + 5 * arrayLength));
        System.out.println("RESULTS:");

        for (Engine engine : results) {
            System.out.println(engine.getDatabaseManager().listValues(engine.getLogFlush()));
        }

        System.out.println("-".repeat(10 + 5 * arrayLength));
        System.out.println("TRANSACTION SUMMARY:");

        results[0].getLogFlush().printHistoryInLanes();
        results[4].getLogFlush().printHistoryInLanes();
    }
}
