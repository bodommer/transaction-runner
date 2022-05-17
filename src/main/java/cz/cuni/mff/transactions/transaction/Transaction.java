package cz.cuni.mff.transactions.transaction;

import cz.cuni.mff.transactions.datamodel.TransactionAction;

import java.util.List;

public class Transaction implements ITransaction {

    private final String name;
    private final List<TransactionAction> actions;
    private final List<Integer> actionIndex;
    private int currentIndex = 0;

    protected Transaction(String name, List<TransactionAction> actions, List<Integer> actionIndex) {
        this.name = name;
        this.actions = actions;
        this.actionIndex = actionIndex;
    }

    @Override
    public TransactionAction getAction(int index) {
        return index >= 0 && index < actions.size() ? actions.get(index) : TransactionAction.NOP;
    }

    @Override
    public int getActionIndex(int index) {
        return index >= 0 && index < actionIndex.size() ? actionIndex.get(index) : 0;
    }

    @Override
    public void increment() {
        currentIndex++;
    }

    @Override
    public void setIndexTo(int index) {
        if (index >= 0 && index < actions.size()) {
            currentIndex = index;
        }
    }

    @Override
    public int getIndex() {
        return currentIndex;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < actions.size();
    }

    public int getId() {
        return Integer.parseInt(name.substring(2));
    }

    @Override
    public String toString() {
        return name;
    }
}
