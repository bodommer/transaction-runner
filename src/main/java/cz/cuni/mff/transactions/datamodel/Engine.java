package cz.cuni.mff.transactions.datamodel;

import cz.cuni.mff.transactions.datamodel.manager.*;
import cz.cuni.mff.transactions.datamodel.structure.Checkpoint;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.runner.AbstractActionProvider;
import cz.cuni.mff.transactions.transaction.ITransaction;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Engine {

    private final CacheManager cacheManager;
    private final LockManager lockManager;
    private final DatabaseManager databaseManager;
    private final AbstractActionProvider actionProvider;
    private final LogManager logManager;
    private final DeadlockManager deadlockManager = new DeadlockManager();
    public static Random systemShutdown = new Random(12);

    public Engine(int dataSize, DatabaseManager databaseManager, AbstractActionProvider actionProvider,
                  LogManager logManager) {
        this.actionProvider = actionProvider;
        this.databaseManager = databaseManager;
        this.logManager = logManager;
        this.cacheManager = new CacheManager(databaseManager, logManager, dataSize);
        this.lockManager = new LockManager(dataSize);
        if (logManager.getLastCheckpoint() != null) {
            restore(logManager.getLastCheckpoint());
        }
    }

    public boolean run() {
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

            // deadlock prevention
            if (result) {
                // go to next transaction
                nextTransaction.increment();
                deadlockManager.unwatchTransaction(nextTransaction);
                
                // create a checkpoint
                doCheckpoint();
            } else {
                if (deadlockManager.watchTransaction(nextTransaction)) {
                    // if 100 times failed to perform action -> abort, reset and remove from deadlock manager
                    doAbort(nextTransaction);
                    nextTransaction.setIndexTo(0);
                    deadlockManager.unwatchTransaction(nextTransaction);
                }
            }

            // shutdown randomly - to test reset and checkpoints
            int val = systemShutdown.nextInt(30000);
            if (val == 0) {
                throw new RuntimeException("The system has unexpectedly shut down!");
            }

            // continue with the next one
            nextTransaction = actionProvider.next();
        }
        return true;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LogFlush getLogFlush() {
        return logManager.getLogFlush();
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

    private void doCheckpoint() {
        logManager.addCheckpoint(
                new Checkpoint(
                        deadlockManager.getLockedTransactions(),
                        actionProvider.getTransactions()
                                .stream()
                                .collect(Collectors.toMap(Function.identity(), ITransaction::getIndex)),
                        lockManager.getExclusives(),
                        lockManager.getShared(),
                        cacheManager.snapshotCache(),
                        cacheManager.getDirtyPages()));
    }

    private void restore(Checkpoint checkpoint) {
        deadlockManager.reset(checkpoint.getDeadlockEntries());
        checkpoint.getTransactionIndexes().forEach(ITransaction::setIndexTo);
        lockManager.restore(checkpoint.getExclusiveLocks(), checkpoint.getSharedLocks());
        cacheManager.restore(checkpoint.getCache(), checkpoint.getWrittenPages());
    }

}
