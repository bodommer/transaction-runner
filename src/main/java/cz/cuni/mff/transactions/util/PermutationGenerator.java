package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PermutationGenerator {

    private PermutationGenerator() {
        // NOP
    }

    public static void generatePermutations(List<List<Transaction>> result,
                                            List<Transaction> transactions,
                                            int permutationSize,
                                            List<Transaction> permutation) {
        if (permutation == null) {
            if (!(permutationSize > 0 && permutationSize <= transactions.size())) {
                throw new IllegalArgumentException();
            }
            permutation = new ArrayList<>(permutationSize);
        }
        for (Transaction i : transactions) {
            if (permutation.contains(i)) {
                continue;
            }
            permutation.add(i);
            if (permutation.size() == permutationSize) {
                result.add(new ArrayList<>(permutation));
            }
            if (permutation.size() < permutationSize) {
                generatePermutations(result, transactions, permutationSize, permutation);
            }
            permutation.remove(permutation.size() - 1);
        }
    }

    public static String getRunName(List<Transaction> transactions) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Transaction t : transactions) {
            if (first) {
                first = false;
            } else {
                builder.append("-");
            }
            builder.append(t.toString().substring(2));
        }
        return builder.toString();
    }

}
