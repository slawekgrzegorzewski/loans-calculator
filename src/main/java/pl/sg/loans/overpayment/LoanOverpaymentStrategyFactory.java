package pl.sg.loans.overpayment;

import pl.sg.loans.model.LoanCalculationParams;

public interface LoanOverpaymentStrategyFactory {
    LoanOverpaymentStrategy create(LoanCalculationParams loanCalculationParams);
}
