package controllers;

import dto.AccountDTO;
import dto.TransactionDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.mvc.Results.badRequest;
import static play.test.Helpers.contentAsString;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void shouldReturnTransaction() {
        TransactionDTO transactionDTO = createTransactionDTO(1L, BigDecimal.valueOf(5),
                createAccountDTO(2L, "test2", "test2@test.com", 31),
                createAccountDTO(1L, "test", "test@test.com", 21));
        ;

        when(transactionRepository.getTransaction(1L)).thenReturn(transactionDTO);

        Result result = transactionController.get(1L);

        String str = contentAsString(result);

        assertEquals("{\"id\":1,\"sender\":{\"id\":2,\"name\":\"test2\",\"email\":\"test2@test.com\"," +
                "\"balance\":31},\"receiver\":{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":21},\"amount\":5,\"date\":null}", str);
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    @Test
    public void shouldCreateTransaction() {
        TransactionDTO transactionDTO = createTransactionDTO(1L, BigDecimal.valueOf(5),
                createAccountDTO(2L, "test2", "test2@test.com", 31),
                createAccountDTO(1L, "test", "test@test.com", 21));
        ;

        Http.Request request = new Http.RequestBuilder().bodyJson(Json.toJson(transactionDTO)).build();
        when(transactionRepository.createTransaction(
                transactionDTO.getSender().getId(),
                transactionDTO.getReceiver().getId(),
                transactionDTO.getAmount())).thenReturn(transactionDTO);

        Result result = transactionController.create(request);

        assertEquals("{\"id\":1,\"sender\":{\"id\":2,\"name\":\"test2\",\"email\":\"test2@test.com\"" +
                ",\"balance\":31},\"receiver\":{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":21},\"amount\":5,\"date\":null}", contentAsString(result));
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    @Test
    public void shouldReturnBadRequestIfPayloadIsEmpty() {
        Http.Request request = new Http.RequestBuilder().build();

        Result result = transactionController.create(request);

        assertEquals(badRequest().status(), result.status());
    }

    @Test
    public void shouldReturnAllTransactions() {
        AccountDTO receiver = createAccountDTO(1L, "test", "test@test.com", 20);
        AccountDTO sender = createAccountDTO(2L, "second_test", "second_test@test.com", 19);

        when(transactionRepository.getAll()).thenReturn(Arrays.asList(
                createTransactionDTO(1L, BigDecimal.valueOf(8), sender, receiver),
                createTransactionDTO(2L, BigDecimal.valueOf(3), sender, receiver)));

        Result result = transactionController.list();

        assertEquals("[{\"id\":1,\"sender\":{\"id\":2,\"name\":\"second_test\",\"email\":\"second_test@test.com\",\"balance\":19}," +
                "\"receiver\":{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":2E+1},\"amount\":8,\"date\":null}," +
                "{\"id\":2,\"sender\":{\"id\":2,\"name\":\"second_test\",\"email\":\"second_test@test.com\",\"balance\":19}," +
                "\"receiver\":{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"balance\":2E+1},\"amount\":3,\"date\":null}]", contentAsString(result));
        assertTrue(result.contentType().isPresent());
        assertEquals("application/json", result.contentType().get());
        assertEquals(OK, result.status());
    }

    private TransactionDTO createTransactionDTO(Long id, BigDecimal amount, AccountDTO sender, AccountDTO receiver) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(id);
        transactionDTO.setAmount(amount);
        transactionDTO.setSender(sender);
        transactionDTO.setReceiver(receiver);
        return transactionDTO;
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
