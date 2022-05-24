package cz.cuni.mff.transactions.runner;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.List;

public class SerialActionProvider extends AbstractActionProvider {
    
    private int currentTransaction = 0;
    private final int transactionCount;
    
    public SerialActionProvider(List<ITransaction> transactions) {
        super(transactions);
        this.transactionCount = transactions.size();
    }

    @Override
    public ITransaction next() {
        while (currentTransaction < transactionCount) {
            if (transactions.get(currentTransaction).hasNext()) {
                return transactions.get(currentTransaction);
            } else {
                currentTransaction++;
            }
        }
        return null;
    }

}
