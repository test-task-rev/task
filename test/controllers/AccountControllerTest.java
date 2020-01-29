package controllers;

import dto.AccountDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.noContent;
import static play.test.Helpers.contentAsString;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void shouldReturnAccount() {
        AccountDTO accountDTO = createAccountDTO(1L, "test", "test@test.com", 3);

        when(accountRepository.getAccount(1L)).thenReturn(accountDTO);

        Result result = accountController.get(1L);

        String str = contentAsString(result);

        assertEquals("{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":3}", str);
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    @Test
    public void shouldDeleteAccount() {
        Result result = accountController.delete(1L);

        verify(accountRepository, times(1)).deleteAccount(1L);

        assertEquals(noContent().status(), result.status());
    }

    @Test
    public void shouldReturnAllAccounts() {
        AccountDTO firstAccount = createAccountDTO(1L, "test", "test@test.com", 20);
        AccountDTO secondAccount = createAccountDTO(2L, "second_test", "second_test@test.com", 20);

        List<AccountDTO> accounts = Arrays.asList(firstAccount, secondAccount);

        when(accountRepository.getAll()).thenReturn(accounts);

        Result result = accountController.list();

        assertEquals("[{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":2E+1},{\"id\":2,\"name\":\"second_test\"," +
                "\"email\":\"second_test@test.com\",\"balance\":2E+1}]", contentAsString(result));
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    @Test
    public void shouldCreateAccount() {
        AccountDTO accountDTO = createAccountDTO(null, "test", "test@test.com", 15);
        Http.Request request = new Http.RequestBuilder().bodyJson(Json.toJson(accountDTO)).build();
        AccountDTO savedAccount = createAccountDTO(1L, "test", "test@test.com", 15);
        when(accountRepository.createAccount(any())).thenReturn(savedAccount);

        Result result = accountController.create(request);

        assertEquals("{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":15}", contentAsString(result));
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    @Test
    public void shouldReturnBadRequestIfPayloadIsEmpty() {
        Http.Request request = new Http.RequestBuilder().build();

        Result result = accountController.create(request);

        assertEquals(badRequest().status(), result.status());
    }

    private AccountDTO createAccountDTO(Long id, String name, String email, int balance) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(id);
        accountDTO.setName(name);
        accountDTO.setEmail(email);
        accountDTO.setBalance(BigDecimal.valueOf(balance));
        return accountDTO;
    }
}
