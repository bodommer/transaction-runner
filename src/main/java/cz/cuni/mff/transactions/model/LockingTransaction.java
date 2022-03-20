package cz.cuni.mff.transactions.model;

import cz.cuni.mff.transactions.datamodel.LockManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockingTransaction extends AbstractTransaction {

    private static final int SLEEP_DURATION = 150;

    private final Set<Integer> sharedLocks = new HashSet<>();
    private final Set<Integer> exclusiveLocks = new HashSet<>();
    private LockManager lockManager;

    public LockingTransaction(String name, int arrayLength, List<TransactionAction> actions,
                              List<Integer> actionIndex) {
        super(name, arrayLength, actions, actionIndex);
    }

    @Override
    protected void performRead(int index) {
        int repeats = 0;
        while (repeats < 10) {
            if (sharedLocks.contains(index) || lockManager.getSharedLock(index, this)) {
                sharedLocks.add(index);
                super.performRead(index);
                return;
            } else {
                try {
                    Thread.sleep(SLEEP_DURATION);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            repeats++;
        }
        throw new IllegalStateException("Deadlock for transaction " + toString() + " and read variable " + index +
                ".\nLock Manager state:\n" + lockManager.toString());
    }

    @Override
    protected void performWrite(int index) {
        int repeats = 0;
        while (repeats < 10) {
            if (exclusiveLocks.contains(index) || lockManager.getExclusiveLock(index, this)) {
                exclusiveLocks.add(index);
                super.performWrite(index);
                return;
            } else {
                try {
                    System.out.println(toString() + " Sleep");
                    Thread.sleep(SLEEP_DURATION);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            repeats++;
        }
        throw new IllegalStateException("Deadlock for transaction " + toString() + " and written variable " + index
                + ".\nLock Manager state:\n" + lockManager.toString());
    }

    @Override
    protected void preProcess() {
        if (this.connection == null) {
            throw new IllegalArgumentException("No connection is set up for the locking transaction " + toString());
        }
        lockManager = connection.getLockManager();
    }

    @Override
    protected void postProcess() {
        reset();
    }

    @Override
    public void reset() {
        if (lockManager != null) {
            exclusiveLocks.forEach(lock -> lockManager.releaseExclusiveLock(lock, this));
            sharedLocks.forEach(lock -> lockManager.releaseSharedLock(lock, this));
            exclusiveLocks.clear();
            sharedLocks.clear();
        }
        if (!isExpectedArrayComputed) {
            this.checkArray = new int[arrayLength];
        }
        actualValues.clear();
    }
}
