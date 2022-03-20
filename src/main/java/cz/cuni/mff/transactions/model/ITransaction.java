package cz.cuni.mff.transactions.model;

import cz.cuni.mff.transactions.datamodel.DataManager;

import java.util.concurrent.Callable;

public interface ITransaction extends Callable<Object> {

    void setConnection(DataManager dataManager);

    int[] getExpectedArray();

    int getId();

    void reset();
}
