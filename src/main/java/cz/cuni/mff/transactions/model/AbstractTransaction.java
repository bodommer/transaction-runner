package cz.cuni.mff.transactions.model;

import cz.cuni.mff.transactions.datamodel.DataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTransaction implements ITransaction {

    protected final String name;
    protected final List<TransactionAction> actions;
    protected final List<Integer> actionIndex;
    protected int[] checkArray;
    protected final Map<Integer, Integer> actualValues = new HashMap<>();
    protected DataManager connection;
    protected boolean isExpectedArrayComputed = false;
    protected final int arrayLength;

    protected AbstractTransaction(String name, int arrayLength, List<TransactionAction> actions, List<Integer> actionIndex) {
        this.name = name;
        this.actions = actions;
        this.actionIndex = actionIndex;
        this.checkArray = new int[arrayLength];
        this.arrayLength = arrayLength;
    }

    public void setConnection(DataManager dataManager) {
        this.connection = dataManager;
    }

    public int[] getExpectedArray() {
        return checkArray;
    }

    public int getId() {
        return Integer.parseInt(name.substring(2));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call() {
        if (connection == null) {
            throw new IllegalStateException("No connection provided");
        }

        preProcess();

        try {
            for (int i = 0; i < actions.size(); i++) {
                int index = actionIndex.get(i);
                switch (actions.get(i)) {
                    case READ:
                        performRead(index);
                        actualValues.put(index, connection.get(index, this));
                        break;
                    case WRITE:
                        performWrite(index);
                        break;
                    default:
                        performCommit();
                }
            }
        } catch (IllegalStateException e) {
            // no recovery
            reset();
            System.err.println("Deadlock for transaction " + toString());
            return null;
        }

        isExpectedArrayComputed = true;
        postProcess();
        return null;
    }

    protected void performRead(int index) {
        actualValues.put(index, connection.get(index, this));
    }

    protected void performWrite(int index) {
        int currentValue = actualValues.getOrDefault(index, 0);
        int addition = currentValue % 2 == 0 ? 1 : 3;
        if (!isExpectedArrayComputed) {
            checkArray[index] = checkArray[index] + addition;
        }
        int newValue = currentValue + addition;
        connection.put(index, newValue, this);
        actualValues.put(index, newValue);
    }

    protected void performCommit() {
        connection.commit(this);
    }

    protected void preProcess() {
        //NOP
    }

    protected void postProcess() {
        // NOP
    }

    public void reset() {
        // NOP
    }
}
