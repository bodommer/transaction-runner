package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.datamodel.structure.LogFlush;

public class DatabaseManager {

    private final int[] data;
    private final int length;

    public DatabaseManager(int dataLength) {
        this.length = dataLength;
        data = new int[dataLength];
    }

    public void write(int index, int value) {
        if (isInvalidIndex(index)) {
            System.err.printf("Invalid persistent storage write to index %d: invalid index.%n", index);
            return;
        }
        data[index] = value;
    }

    public int read(int index) {
        if (isInvalidIndex(index)) {
            System.err.printf("Invalid persistent storage read from index %d: invalid index.%n", index);
            return 0;
        }
        return data[index];
    }

    @SuppressWarnings("unused")
    public int getLength() {
        return length;
    }

    public String listValues(LogFlush logFlush) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(String.format("%3d  ", data[i]));
        }
        builder.append(" (");
        builder.append(logFlush.isSerializable() ? "SERIALIZABLE" : "NOT SERIALIZABLE");
        builder.append(", ");
        builder.append(logFlush.isRecoverable() ? "RECOVERABLE" : "IRRECOVERABLE");
        builder.append(")");
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
