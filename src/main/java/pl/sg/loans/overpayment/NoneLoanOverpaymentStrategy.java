package pl.sg.loans.overpayment;

import java.math.BigDecimal;

public class NoneLoanOverpaymentStrategy implements LoanOverpaymentStrategy {
    @Override
    public BigDecimal calculateOverpayment(int installmentNumber, BigDecimal installmentAmount) {
        return BigDecimal.ZERO;
    }
}
