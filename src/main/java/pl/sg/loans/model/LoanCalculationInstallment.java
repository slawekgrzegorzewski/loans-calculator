package pl.sg.loans.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanCalculationInstallment(LocalDate paymentFrom,
                                         LocalDate paymentTo,
                                         BigDecimal remainingCapitalAtTheBeginning,
                                         BigDecimal installment,
                                         BigDecimal repaidCapital,
                                         BigDecimal paidInterest,
                                         BigDecimal overpayment
) {
}
