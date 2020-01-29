package models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Test
    public void shouldSubstractMoneyDuringDebit() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(30));

        account.debit(BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(25), account.getBalance());
    }

    @Test
    public void shouldAddMoneyDuringCredit() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(30));

        account.credit(BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(35), account.getBalance());
    }
}
