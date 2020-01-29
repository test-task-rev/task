package models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "transaction")
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sender must be present")
    @OneToOne
    @JoinColumn(name = "sender_id")
    private Account sender;

    @NotNull(message = "Receiver must be present")
    @OneToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    @NotNull(message = "Transaction Amount should be filled")
    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private Date date;

    @Version
    private Long version;
}
