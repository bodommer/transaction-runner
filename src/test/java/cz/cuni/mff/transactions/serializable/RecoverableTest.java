package cz.cuni.mff.transactions.serializable;

import cz.cuni.mff.transactions.datamodel.History;
import cz.cuni.mff.transactions.model.Transaction;
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

class RecoverableTest {

    @ParameterizedTest
    @CsvSource({"three_short_transactions, 3, false"})
    void serializableTest(String fileName, int transactionCount, boolean expected) throws IOException {
        // prepare
        History history = new History();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < transactionCount + 1; i++) {
            Transaction transaction = mock(Transaction.class);
            when(transaction.toString()).thenReturn("TR" + i);
            transactions.add(transaction);
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.format("src/test/resources/recoverable/%s.txt",
                             fileName)))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] elements = line.split(" ");
                Transaction transaction = transactions.get(Integer.parseInt(elements[0]) - 1);
                Transaction.Action action;
                switch (elements[1]) {
                    case "W":
                        action = Transaction.Action.WRITE;
                        break;
                    case "R":
                        action = Transaction.Action.READ;
                        break;
                    default:
                        action = Transaction.Action.COMMIT;
                }
                int index = Integer.parseInt(elements[2]);
                history.addEvent(transaction, action, index);
            }
        }

        // act
        boolean isRecoverable = history.isRecoverable();

        //assert
        assertEquals(expected, isRecoverable);
    }
}
