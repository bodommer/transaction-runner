package cz.cuni.mff.transactions.transaction;

import cz.cuni.mff.transactions.datamodel.TransactionAction;

public interface ITransaction {
    
    TransactionAction getAction(int index);
    
    int getActionIndex(int index);
    
    void increment();
    
    void setIndexTo(int index);
    
    int getIndex();
    
    boolean hasNext();
    
    int getId();
    
}
