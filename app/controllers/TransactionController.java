package controllers;

import dto.TransactionDTO;
import repository.TransactionRepository;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

@Singleton
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @Inject
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Result create(Http.Request request) {
        Optional<TransactionDTO> transactionEntity = request.body().parseJson(TransactionDTO.class);
        return transactionEntity
                .map(s-> ok(Json.toJson(transactionRepository.createTransaction(
                        s.getSender().getId(),
                        s.getReceiver().getId(),
                        s.getAmount()))))
                .orElse(badRequest());
    }

    public Result get(Long id) {
        return ok(Json.toJson(transactionRepository.getTransaction(id)));
    }

    public Result list() {
        return ok(Json.toJson(transactionRepository.getAll()));
    }
}
