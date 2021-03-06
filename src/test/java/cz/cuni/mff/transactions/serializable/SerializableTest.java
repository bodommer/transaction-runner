package cz.cuni.mff.transactions.serializable;

import cz.cuni.mff.transactions.datamodel.TransactionAction;
import cz.cuni.mff.transactions.datamodel.structure.HistoryEvent;
import cz.cuni.mff.transactions.datamodel.structure.LogFlush;
import cz.cuni.mff.transactions.transaction.Transaction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SerializableTest {

    //formatter:off
    @ParameterizedTest
    @CsvSource({
            "three_short_transactions, 3, false",
            "input_1, 3, true",
            "input_2, 3, true",
            "input_3, 3, true",
            "input_4, 3, true",
            "input_5, 3, true",
            "input_6, 3, false",
            "input_7, 3, true",
            "input_8, 3, false",
            "input_9, 3, false",
            "input_10, 3, true",
            "input_11, 3, true",
            "input_12, 3, true",
            "input_13, 3, true",
            "input_14, 3, false",
            "input_15, 3, true",
            "input_16, 3, false",
            "input_17, 3, false",
            "input_18, 3, true",
            "input_19, 3, true",
            "input_20, 3, true",
            "input_21, 3, false",
            "input_22, 3, true",
            "input_23, 3, true",
            "input_24, 3, true",
            "input_25, 3, false",
            "input_26, 3, false",
            "input_27, 3, true",
            "input_28, 3, false",
            "input_29, 3, false",
            "input_30, 3, true",
            "input_31, 3, false",
            "input_32, 3, false",
            "input_33, 3, false",
            "input_34, 3, true",
            "input_35, 3, true",
            "input_36, 3, true",
            "input_37, 3, false",
            "input_38, 3, true",
            "input_39, 3, false",
            "input_40, 3, true",
            "input_41, 3, true",
            "input_42, 3, false",
            "input_43, 3, true",
            "input_44, 3, false",
            "input_45, 3, true",
            "input_46, 3, true",
            "input_47, 3, false",
            "input_48, 3, true",
            "input_49, 3, false",
            "input_50, 3, false",
            "input_51, 3, false",
            "input_52, 3, false",
            "input_53, 3, true",
            "input_54, 3, true",
            "input_55, 3, true",
            "input_56, 3, true",
            "input_57, 3, true",
            "input_58, 3, false",
            "input_59, 3, true",
            "input_60, 3, true",
            "input_61, 3, true",
            "input_62, 3, true",
            "input_63, 3, true",
            "input_64, 3, true",
            "input_65, 3, true",
            "input_66, 3, true",
            "input_67, 3, true",
            "input_68, 3, true"})
    //formatter:on
    void serializableTest(String fileName, int transactionCount, boolean expected) throws IOException {
        // prepare
        LogFlush logFlush = new LogFlush();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < transactionCount + 1; i++) {
            Transaction transaction = mock(Transaction.class);
            when(transaction.toString()).thenReturn("TR" + i);
            when(transaction.getId()).thenReturn(i);
            transactions.add(transaction);
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.format("src/test/resources/history/%s.txt",
                             fileName)))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] elements = line.split(" ");
                Transaction transaction = transactions.get(Integer.parseInt(elements[0]) - 1);
                TransactionAction action;
                switch (elements[1]) {
                    case "W":
                        action = TransactionAction.WRITE;
                        break;
                    case "R":
                        action = TransactionAction.READ;
                        break;
                    default:
                        action = TransactionAction.COMMIT;
                }
                int index = Integer.parseInt(elements[2]);
                logFlush.flush(new HistoryEvent(action, index, transaction, 0, 1, null));
            }
        }

        // act
        boolean isSerializable = logFlush.isSerializable();
        //logFlush.printHistoryInLanes();

        //assert
        assertEquals(expected, isSerializable);
    }

}
