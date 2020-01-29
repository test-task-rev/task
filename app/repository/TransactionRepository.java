package repository;

import dto.TransactionDTO;
import exception.AccountNotFoundException;
import exception.DebitException;
import models.Account;
import models.Transaction;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TransactionRepository {

    private final JPAApi jpaApi;

    @Inject
    public TransactionRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public TransactionDTO createTransaction(Long senderId, Long receiverId, BigDecimal amount) {
        return jpaApi.withTransaction(entityManager -> {
            try {
                Account sender = entityManager.find(Account.class, senderId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Account receiver = entityManager.find(Account.class, receiverId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                if (sender == null) {
                    throw new AccountNotFoundException("The account id " + senderId + " hasn't been found.");
                }
                if (receiver == null) {
                    throw new AccountNotFoundException("The account id " + receiverId + " hasn't been found.");
                }
                if (sender.getBalance().compareTo(amount) < 0) {
                    throw new DebitException("The account with id " + senderId + " doesn't have enough money");
                }

                sender.debit(amount);
                receiver.credit(amount);

                entityManager.merge(sender);
                entityManager.merge(receiver);

                Transaction transaction = new Transaction();
                transaction.setSender(sender);
                transaction.setReceiver(receiver);
                transaction.setDate(new Date());
                transaction.setAmount(amount);
                entityManager.persist(transaction);

                entityManager.flush();

                return new TransactionDTO(transaction);
            } catch (OptimisticLockException ex) {
                throw new IllegalArgumentException("During processing the transaction happened error. Please, try again");
            }
        });
    }

    public TransactionDTO getTransaction(Long id) {
        return jpaApi.withTransaction(
                entityManager -> {
                    return new TransactionDTO(entityManager.find(Transaction.class, id));
                });
    }

    public List<TransactionDTO> getAll() {
        return jpaApi.withTransaction(
                entityManager -> {
                    TypedQuery<Transaction> query = entityManager.createQuery("SELECT a FROM " + Transaction.class.getName() +
                            " a INNER JOIN FETCH a.receiver receiver INNER JOIN FETCH a.sender sender", Transaction.class);
                    List<Transaction> transactionEntities = query.getResultList();
                    return transactionEntities.stream().map(TransactionDTO::new).collect(Collectors.toList());
                });
    }
}
