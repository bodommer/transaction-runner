package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.datamodel.manager.DatabaseManager;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.Collection;

public class PrettyPrinter {

    private PrettyPrinter() {
        // NOP
    }

    public static void printResults(int arrayLength, Collection<ITransaction> transactions, DatabaseManager... results) {
//        System.out.println("-".repeat(10 + 5 * arrayLength));
//        System.out.println("RESULTS:");
//
//        for (DatabaseManager dataManager : results) {
//            System.out.println(dataManager.listValues());
//        }
//
//        System.out.println("-".repeat(10 + 5 * arrayLength));
//        System.out.println("TRANSACTION SUMMARY:");
//
//        for (ITransaction transaction : transactions) {
//            System.out.print(transaction.toString());
//            int[] arr = transaction.getExpectedArray();
//            System.out.print(" ".repeat(12 - transaction.toString().length()));
//            for (int i = 0; i < arrayLength; i++) {
//                System.out.printf("%3d  ", arr[i]);
//            }
//            System.out.println();
//        }
    }
}
