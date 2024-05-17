package pl.sg.loans.overpayment;

import pl.sg.loans.model.LoanCalculationParams;

import java.math.BigDecimal;

public class LoanOverpaymentStrategyFactoryImpl implements LoanOverpaymentStrategyFactory {

    @Override
    public LoanOverpaymentStrategy create(LoanCalculationParams loanCalculationParams) {
        boolean yearly = hasYearlyBudgetSet(loanCalculationParams);
        boolean monthly = hasMonthlyBudgetSet(loanCalculationParams);

        if (!yearly && monthly) {
            return new MonthlyLoanOverpaymentStrategy(loanCalculationParams.overpaymentMonthlyBudget());
        } else if (yearly) {
            return new YearlyLoanOverpaymentStrategy(loanCalculationParams.overpaymentYearlyBudget(), loanCalculationParams.overpaymentMonthlyBudget());
        } else {
            return new NoneLoanOverpaymentStrategy();
        }
    }

    private boolean hasYearlyBudgetSet(LoanCalculationParams loanCalculationParams) {
        return loanCalculationParams.overpaymentYearlyBudget().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean hasMonthlyBudgetSet(LoanCalculationParams loanCalculationParams) {
        return loanCalculationParams.overpaymentMonthlyBudget().compareTo(BigDecimal.ZERO) > 0;
    }
}
