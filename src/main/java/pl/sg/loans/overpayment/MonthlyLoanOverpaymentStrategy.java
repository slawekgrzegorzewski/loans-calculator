package pl.sg.loans.overpayment;

import java.math.BigDecimal;

public class MonthlyLoanOverpaymentStrategy implements LoanOverpaymentStrategy {
    private final BigDecimal overpaymentMonthlyBudget;

    public MonthlyLoanOverpaymentStrategy(BigDecimal overpaymentMonthlyBudget) {

        this.overpaymentMonthlyBudget = overpaymentMonthlyBudget;
    }

    @Override
    public BigDecimal calculateOverpayment(int installmentNumber, BigDecimal installmentAmount) {
        BigDecimal result = overpaymentMonthlyBudget.subtract(installmentAmount);
        return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
    }
}
