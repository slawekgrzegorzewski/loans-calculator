package pl.sg.loans.overpayment;

import java.math.BigDecimal;

public interface LoanOverpaymentStrategy {
    BigDecimal calculateOverpayment(int installmentNumber, BigDecimal installmentAmount);
}
