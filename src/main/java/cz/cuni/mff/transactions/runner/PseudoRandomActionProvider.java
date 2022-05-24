package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.List;
import java.util.Random;

public class PseudoRandomActionProvider extends AbstractActionProvider {

    private final Random random;
    
    public PseudoRandomActionProvider(List<ITransaction> transactions, int seed) {
        super(transactions);
        this.random = new Random(seed);
    }

    @Override
    public ITransaction next() {
        while (!transactions.isEmpty()) {
            ITransaction transaction = transactions.get(random.nextInt(transactions.size()));
            if (transaction.hasNext()) {
                return transaction;
            }
            transactions.remove(transaction);
        }
        return null;
    }
}
