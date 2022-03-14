package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

public class DataManager {

    private final int[] data;
    private final int length;
    private final String name;
    private final History history = new History();

    public DataManager(String name, int dataLength) {
        this.length = dataLength;
        data = new int[dataLength];
        this.name = name;
    }

    public int get(int index) {
        if (index < 0 || index >= length) {
            return 0;
        }
        return data[index];
    }

    public int get(int index, Transaction transaction) {
        System.out.println(transaction.toString() + ": Reading " + get(index) + " from " + index);
        history.addEvent(transaction, Transaction.Action.READ, index);
        return get(index);
    }

    public void put(int index, int value) {
        if (!isInvalidIndex(index)) {
            data[index] = value;
        }
    }

    public void put(int index, int value, Transaction transaction) {
        System.out.println(transaction.toString() + ": Writing " + value + " to " + index);
        history.addEvent(transaction, Transaction.Action.WRITE, index);
        put(index, value);
    }

    public void commit(Transaction transaction) {
        history.addEvent(transaction, Transaction.Action.COMMIT, 0);
    }

    @SuppressWarnings("unused")
    public int getLength() {
        return length;
    }

    public String listValues() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(" ".repeat(Math.max(0, 12 - name.length())));
        for (int i = 0; i < length; i++) {
            builder.append(String.format("%3d  ", data[i]));
        }
        builder.append(isSerializable() ? "  (SERIALIZABLE, " : "(NOT SERIALIZABLE, ");
        builder.append(isRecoverable() ? "RECOVERABLE)" : "IRRECOVERABLE)");
        return builder.toString();
    }

    public boolean isSerializable() {
        return history.isSerializable();
    }

    public boolean isRecoverable() {
        return history.isRecoverable();
    }

    @SuppressWarnings("unused")
    public void printHistory() {
        history.printHistory();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(i);
            builder.append(" : ");
            builder.append(data[i]);
            builder.append("\n");
        }
        return builder.toString();
    }

    private boolean isInvalidIndex(int index) {
        return index < 0 || index >= length;
    }
}
