package repository;

import dto.AccountDTO;
import models.Account;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AccountRepository {

    private final JPAApi jpaApi;

    @Inject
    public AccountRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public List<AccountDTO> getAll() {
        return jpaApi.withTransaction(
                entityManager -> {
                    TypedQuery<Account> query = entityManager.createQuery("from " + Account.class.getName() + " a", Account.class);
                    List<Account> accountEntities = query.getResultList();
                    return accountEntities.stream().map(AccountDTO::new).collect(Collectors.toList());
                });
    }

    public AccountDTO getAccount(Long id) {
        return jpaApi.withTransaction(
                entityManager -> {
                    return new AccountDTO(entityManager.find(Account.class, id));
                });
    }

    public void deleteAccount(Long id) {
        jpaApi.withTransaction(entityManager -> {
            Account account = entityManager.find(Account.class, id);
            account.setDeleted(true);
            entityManager.merge(account);
        });
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        return jpaApi.withTransaction(entityManager -> {
            Account account = new Account();
            account.setName(accountDTO.getName());
            account.setEmail(accountDTO.getEmail());
            account.setBalance(accountDTO.getBalance());
            entityManager.persist(account);

            return new AccountDTO(account);
        });
    }
}
