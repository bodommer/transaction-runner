package cz.cuni.mff.transactions.datamodel;

public enum TransactionAction {
    READ,
    WRITE,
    COMMIT,
    ABORT,
    /**
     * An auxiliary value for cache when performing ABORT correctly. Never flushed into log.
     */
    FETCH,
    NOP
}
