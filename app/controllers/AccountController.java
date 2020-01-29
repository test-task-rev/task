package controllers;

import dto.AccountDTO;
import repository.AccountRepository;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Optional;

import static play.mvc.Results.*;

@Singleton
public class AccountController {

    private final AccountRepository accountRepository;

    @Inject
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result get(Long id) {
        return ok(Json.toJson(accountRepository.getAccount(id)));
    }

    public Result delete(Long id) {
        accountRepository.deleteAccount(id);
        return noContent();
    }

    public Result create(Http.Request request) {
        Optional<AccountDTO> accountEntity = request.body().parseJson(AccountDTO.class);
        return accountEntity.map(s -> ok(Json.toJson(accountRepository.createAccount(s)))).orElse(badRequest());
    }

    public Result list() {
        return ok(Json.toJson(accountRepository.getAll()));
    }
}
