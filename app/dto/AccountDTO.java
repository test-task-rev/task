package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Account;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.balance = account.getBalance();
    }
}
