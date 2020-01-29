package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Transaction;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDTO {

    private Long id;
    private AccountDTO sender;
    private AccountDTO receiver;
    private BigDecimal amount;
    private Date date;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = new AccountDTO(transaction.getSender());
        this.receiver = new AccountDTO(transaction.getReceiver());
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
    }
}
