package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.model.Transaction;

public class DataManager {

    private final int[] data;
    private final int length;
    private final String name;

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
        return get(index);
    }

    public void put(int index, int value) {
        if (!isInvalidIndex(index)) {
            data[index] = value;
        }
    }

    public void put(int index, int value, Transaction transaction) {
        System.out.println(transaction.toString() + ": Writing " + value + " to " + index);
        put(index, value);
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
        return builder.toString();
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
