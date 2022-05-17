package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.datamodel.manager.CacheManager;
import cz.cuni.mff.transactions.datamodel.manager.DatabaseManager;
import cz.cuni.mff.transactions.datamodel.manager.LockManager;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.runner.AbstractActionProvider;
import cz.cuni.mff.transactions.transaction.ITransaction;

public class Engine {

    private final CacheManager cacheManager;
    private final LockManager lockManager;
    private final DatabaseManager databaseManager;
    private final AbstractActionProvider actionProvider;
    private final LogFlush logFlush = new LogFlush();

    public Engine(int dataSize, AbstractActionProvider actionProvider) {
        this.actionProvider = actionProvider;
        this.databaseManager = new DatabaseManager(dataSize);
        this.cacheManager = new CacheManager(databaseManager, logFlush, dataSize);
        this.lockManager = new LockManager(dataSize);
    }

    public void run() {
        int actionCounter = 0;
        ITransaction nextTransaction = actionProvider.next();
        while (nextTransaction != null) {
            int index = nextTransaction.getIndex();
            TransactionAction action = nextTransaction.getAction(index);
            boolean result;
            switch (action) {
                case READ:
                    result = doRead(nextTransaction, nextTransaction.getActionIndex(index));
                    break;
                case WRITE:
                    result = doWrite(nextTransaction, nextTransaction.getActionIndex(index));
                    break;
                case COMMIT:
                    result = doCommit(nextTransaction);
                    break;
                case ABORT:
                    result = doAbort(nextTransaction);
                    break;
                default:
                    result = true;
            }

            if (result) {
                // go to next transaction
                nextTransaction.increment();
            }

            if (actionCounter == 10) {
                actionCounter = 0;
                cacheManager.createCheckpoint();
            }
            actionCounter++;
            // continue with the next one
            nextTransaction = actionProvider.next();
        }

    }

    private boolean doRead(ITransaction transaction, int index) {
        if (lockManager.canRead(index, transaction)) {
            return true;
        }
        return lockManager.getSharedLock(index, transaction);
    }

    private boolean doWrite(ITransaction transaction, int index) {
        if (!lockManager.canWrite(index, transaction) && !lockManager.getExclusiveLock(index, transaction)) {
            return false;
        }
        cacheManager.doWrite(transaction, index);
        return true;
    }

    private boolean doCommit(ITransaction transaction) {
        cacheManager.doCommit(transaction);
        lockManager.releaseAllLocks(transaction);
        return true;
    }

    private boolean doAbort(ITransaction transaction) {
        cacheManager.doAbort(transaction);
        lockManager.releaseAllLocks(transaction);
        return true;
    }


}
