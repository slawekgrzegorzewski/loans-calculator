package pl.sg.loans.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanCalculationParams(BigDecimal loanAmount,
                                    LocalDate repaymentStart,
                                    BigDecimal rate,
                                    BigDecimal wibor,
                                    int numberOfInstallments,
                                    BigDecimal overpaymentMonthlyBudget,
                                    BigDecimal overpaymentYearlyBudget
) {
}
