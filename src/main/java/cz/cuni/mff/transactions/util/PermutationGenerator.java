package cz.cuni.mff.transactions.util;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;

public class PermutationGenerator {

    private PermutationGenerator() {
        // NOP
    }

    public static void generatePermutations(List<List<ITransaction>> result,
                                            List<ITransaction> transactions,
                                            int permutationSize,
                                            List<ITransaction> permutation) {
        if (permutation == null) {
            if (!(permutationSize > 0 && permutationSize <= transactions.size())) {
                throw new IllegalArgumentException();
            }
            permutation = new ArrayList<>(permutationSize);
        }
        for (ITransaction i : transactions) {
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

    public static String getRunName(List<ITransaction> transactions) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (ITransaction t : transactions) {
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
