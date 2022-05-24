package cz.cuni.mff.transactions.datamodel.manager;

import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LockManager {

    private final List<ITransaction> exclusiveLocks;
    private final List<List<ITransaction>> sharedLocks;
    private final int dataSize;

    public LockManager(int dataSize) {
        this.dataSize = dataSize;
        exclusiveLocks = new ArrayList<>(dataSize);
        sharedLocks = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            sharedLocks.add(new ArrayList<>());
            exclusiveLocks.add(null);
        }
    }

    public boolean getExclusiveLock(int index, ITransaction transaction) {
        if (outOfRange(index)) {
            return false;
        }

        if ((exclusiveLocks.get(index) == null && canLockExclusively(index, transaction))
                || exclusiveLocks.get(index) == transaction) {
            // either give lock or keep it to the transaction
            exclusiveLocks.set(index, transaction);
            return true;
        }
        return false;
    }

    public void releaseExclusiveLock(int index, ITransaction transaction) {
        if (outOfRange(index)) {
            return;
        }

        if (exclusiveLocks.get(index) == transaction) {
            System.out.println("Exclusive lock " + index + " opened for " + transaction);
            exclusiveLocks.set(index, null);
        } else {
            System.err.println("Error exclusive unlock of item at [" + index + "] by " + transaction.toString());
        }
    }

    public boolean getSharedLock(int index, ITransaction transaction) {
        if (outOfRange(index)) {
            return false;
        }

        if (exclusiveLocks.get(index) != null) {
            return false;
        }

        sharedLocks.get(index).add(transaction);
        return true;
    }

    public void releaseSharedLock(int index, ITransaction transaction) {
        if (outOfRange(index)) {
            return;
        }

        sharedLocks.get(index).remove(transaction);
    }

    public void releaseAllLocks(ITransaction transaction) {
        exclusiveLocks.replaceAll(tr -> tr != null && tr.equals(transaction) ? null : tr);
        sharedLocks.forEach(li -> li.remove(transaction));
    }

    public boolean canWrite(int index, ITransaction transaction) {
        return !outOfRange(index) && exclusiveLocks.get(index) == transaction;
    }

    public boolean canRead(int index, ITransaction transaction) {
        return !outOfRange(index) && sharedLocks.get(index).contains(transaction);
    }

    public List<ITransaction> getExclusives() {
        return new ArrayList<>(exclusiveLocks);
    }
    
    public List<List<ITransaction>> getShared() {
        return sharedLocks.stream().map(ArrayList::new).collect(Collectors.toList());
    }
    
    public void restore(List<ITransaction> exclusive, List<List<ITransaction>> shared) {
        sharedLocks.clear();
        shared.forEach(element -> sharedLocks.add(new ArrayList<>(element)));
        exclusiveLocks.clear();
        exclusiveLocks.addAll(exclusive);
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Exclusive locks:\n");
        for (int i = 0; i < dataSize; i++) {
            ITransaction tr = exclusiveLocks.get(i);
            stringBuilder.append("  ");
            stringBuilder.append(i);
            stringBuilder.append(" :\n    ");
            stringBuilder.append(tr == null ? "null" : tr);
            stringBuilder.append("\n");
        }
        stringBuilder.append("Shared locks:\n");
        for (int i = 0; i < dataSize; i++) {
            List<ITransaction> list = sharedLocks.get(i);
            stringBuilder.append("  ");
            stringBuilder.append(i);
            stringBuilder.append(" :\n");
            for (var element : list) {
                stringBuilder.append("    ");
                stringBuilder.append(element);
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private boolean canLockExclusively(int index, ITransaction transaction) {
        return sharedLocks.get(index).isEmpty()
                || (sharedLocks.get(index).size() == 1 && sharedLocks.get(index).get(0) == transaction);
    }

    private boolean outOfRange(int index) {
        return index < 0 || index >= dataSize;
    }
}
