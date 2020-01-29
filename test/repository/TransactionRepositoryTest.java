package repository;

import dto.TransactionDTO;
import exception.AccountNotFoundException;
import exception.DebitException;
import models.Account;
import models.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.db.jpa.DefaultJPAApi;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Transaction> transactionEntityTypedQuery;

    @Before
    public void setUp() {
        DefaultJPAApi jpaApi = spy(new DefaultJPAApi(null));
        when(jpaApi.em("default")).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        transactionRepository = new TransactionRepository(jpaApi);
    }

    @Test(expected = DebitException.class)
    public void shouldThrowExceptionIfBalanceIsNotEnough() {
        Account sender = new Account();
        sender.setBalance(BigDecimal.valueOf(10));
        when(entityManager.find(eq(Account.class), eq(1L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(sender);
        when(entityManager.find(eq(Account.class), eq(2L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(new Account());
        transactionRepository.createTransaction(1L, 2L, BigDecimal.valueOf(20));
    }

    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowExceptionIfSenderIsAbsent() {
        when(entityManager.find(eq(Account.class), eq(1L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(null);
        when(entityManager.find(eq(Account.class), eq(2L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(new Account());
        transactionRepository.createTransaction(1L, 2L, BigDecimal.valueOf(20));
    }

    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowExceptionIfReceiverIsAbsent() {
        when(entityManager.find(eq(Account.class), eq(1L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(new Account());
        when(entityManager.find(eq(Account.class), eq(2L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(null);
        transactionRepository.createTransaction(1L, 2L, BigDecimal.valueOf(20));
    }

    @Test
    public void shouldCreateTransaction() {
        Account sender = new Account();
        Account receiver = new Account();
        sender.setBalance(BigDecimal.valueOf(20));
        receiver.setBalance(BigDecimal.valueOf(30));
        sender.setId(1L);
        receiver.setId(2L);
        BigDecimal transactionAmount = BigDecimal.valueOf(20);

        when(entityManager.find(eq(Account.class), eq(1L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(sender);
        when(entityManager.find(eq(Account.class), eq(2L), eq(LockModeType.OPTIMISTIC_FORCE_INCREMENT))).thenReturn(receiver);

        TransactionDTO result = transactionRepository.createTransaction(sender.getId(), receiver.getId(), transactionAmount);

        verify(entityManager, times(1)).merge(sender);
        verify(entityManager, times(1)).merge(receiver);
        verify(entityManager, times(1)).persist(any(Transaction.class));

        assertNotNull(result.getReceiver());
        assertNotNull(result.getSender());
        assertEquals(BigDecimal.valueOf(0), sender.getBalance());
        assertEquals(BigDecimal.valueOf(50), receiver.getBalance());
        assertEquals(transactionAmount, result.getAmount());
        assertEquals(sender.getId(), result.getSender().getId());
        assertEquals(receiver.getId(), result.getReceiver().getId());
    }

    @Test
    public void shouldGetAllTransactionsAndConvertToDTO() {
        Account sender = new Account();
        Account receiver = new Account();
        sender.setId(1L);
        receiver.setId(2L);
        Transaction transaction = createTransactionEntity(1L, BigDecimal.valueOf(10), sender, receiver);
        when(entityManager.createQuery(anyString(), eq(Transaction.class))).thenReturn(transactionEntityTypedQuery);
        when(transactionEntityTypedQuery.getResultList()).thenReturn(Collections.singletonList(transaction));

        List<TransactionDTO> result = transactionRepository.getAll();

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getReceiver());
        assertNotNull(result.get(0).getSender());
        assertEquals(result.get(0).getReceiver().getId(), transaction.getReceiver().getId());
        assertEquals(result.get(0).getSender().getId(), transaction.getSender().getId());
        assertEquals(result.get(0).getAmount(), transaction.getAmount());
    }

    @Test
    public void shouldConvertEntityToDTOAndReturnTransaction() {
        Account sender = new Account();
        Account receiver = new Account();
        sender.setId(1L);
        receiver.setId(2L);
        Transaction transaction = createTransactionEntity(2L, BigDecimal.valueOf(10), sender, receiver);
        when(entityManager.find(eq(Transaction.class), eq(1L))).thenReturn(transaction);

        TransactionDTO result = transactionRepository.getTransaction(1L);

        assertNotNull(result.getReceiver());
        assertNotNull(result.getSender());
        assertEquals(result.getReceiver().getId(), transaction.getReceiver().getId());
        assertEquals(result.getSender().getId(), transaction.getSender().getId());
        assertEquals(result.getAmount(), transaction.getAmount());
    }

    private Transaction createTransactionEntity(Long id, BigDecimal amount, Account sender, Account receiver) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        return transaction;
    }
}
