package cz.cuni.mff.transactions.model;

import cz.cuni.mff.transactions.datamodel.DataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Transaction implements Callable<Object> {

    private final String name;
    private final List<Action> actions;
    private final List<Integer> actionIndex;
    private final int[] checkArray;
    private final Map<Integer, Integer> actualValues = new HashMap<>();
    private DataManager connection;
    private boolean isExpectedArrayComputed = false;

    public Transaction(String name, int arrayLength, List<Action> actions, List<Integer> actionIndex) {
        this.name = name;
        this.actions = actions;
        this.actionIndex = actionIndex;
        this.checkArray = new int[arrayLength];
    }

    public void setConnection(DataManager dataManager) {
        this.connection = dataManager;
    }

    @Override
    public Object call() {
        if (connection == null) {
            throw new IllegalStateException("No connection provided");
        }

        for (int i = 0; i < actions.size(); i++) {
            int index = actionIndex.get(i);
            switch (actions.get(i)) {
                case READ:
                    actualValues.put(index, connection.get(index, this));
                    break;
                case WRITE:
                    performWrite(index);
                    break;
                default:
                    connection.commit(this);
            }
        }
        isExpectedArrayComputed = true;
        return null;
    }

    public int[] getExpectedArray() {
        return checkArray;
    }

    @Override
    public String toString() {
        return name;
    }

    private void performWrite(int index) {
        int currentValue = actualValues.getOrDefault(index, 0);
        int addition = currentValue % 2 == 0 ? 1 : 3;
        if (!isExpectedArrayComputed) {
            checkArray[index] = checkArray[index] + addition;
        }
        int newValue = currentValue + addition;
        connection.put(index, newValue, this);
        actualValues.put(index, newValue);
    }

    public enum Action {
        READ,
        WRITE,
        COMMIT
    }
}
