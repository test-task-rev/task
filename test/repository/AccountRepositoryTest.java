package repository;

import dto.AccountDTO;
import models.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.db.jpa.DefaultJPAApi;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountRepositoryTest {

    private AccountRepository accountRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Account> accountEntityTypedQuery;

    @Before
    public void setUp() {
        DefaultJPAApi jpaApi = spy(new DefaultJPAApi(null));
        when(jpaApi.em("default")).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        accountRepository = new AccountRepository(jpaApi);
    }

    @Test
    public void shouldGetAllAccountsAndConvertToDTO() {
        Account firstAccount = new Account();
        Account secondAccount = new Account();
        firstAccount.setId(1L);
        secondAccount.setId(2L);

        when(entityManager.createQuery(anyString(), eq(Account.class))).thenReturn(accountEntityTypedQuery);
        when(accountEntityTypedQuery.getResultList()).thenReturn(Arrays.asList(firstAccount, secondAccount));

        List<AccountDTO> result = accountRepository.getAll();

        assertEquals(2, result.size());
        assertEquals(Long.valueOf(1), result.get(0).getId());
        assertEquals(Long.valueOf(2L), result.get(1).getId());
    }

    @Test
    public void shouldGetAccountAndConvertToDTO() {
        Account account = new Account();
        account.setId(1L);
        account.setName("test");
        account.setEmail("test@test.com");
        account.setBalance(BigDecimal.valueOf(30));

        when(entityManager.find(eq(Account.class), eq(1L))).thenReturn(account);

        AccountDTO result = accountRepository.getAccount(account.getId());

        assertEquals(result.getBalance(), account.getBalance());
        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getName(), account.getName());
        assertEquals(result.getId(), account.getId());
    }

    @Test
    public void shouldSetDeletedColumnTrueAndUpdateAccount() {
        Account account = new Account();
        account.setDeleted(false);

        when(entityManager.find(eq(Account.class), eq(1L))).thenReturn(account);

        accountRepository.deleteAccount(1L);

        verify(entityManager, times(1)).find(Account.class, 1L);
        verify(entityManager, times(1)).merge(account);

        assertEquals(true, account.getDeleted());
    }

    @Test
    public void shouldCreateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setName("account");
        accountDTO.setEmail("account@test.com");
        accountDTO.setBalance(BigDecimal.valueOf(15));

        AccountDTO result = accountRepository.createAccount(accountDTO);

        verify(entityManager, times(1)).persist(any());

        assertEquals(accountDTO.getName(), result.getName());
        assertEquals(accountDTO.getEmail(), result.getEmail());
        assertEquals(accountDTO.getBalance(), result.getBalance());
    }
}
