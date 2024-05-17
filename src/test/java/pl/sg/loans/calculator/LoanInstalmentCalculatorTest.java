package pl.sg.loans.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class LoanInstalmentCalculatorTest {

    @Test
    void test() {

        LoanInstalmentCalculator loanInstalmentCalculator = new LoanInstalmentCalculator(2, 10);
        BigDecimal loanAmount = new BigDecimal("1078400.00");
        BigDecimal annualRate = new BigDecimal("0.0698");
        LocalDate firstInstallment = LocalDate.of(2023, 9, 7);
        BigDecimal installmentAmount = loanInstalmentCalculator.calculateInstallmentAmount(
                loanAmount,
                annualRate,
                360,
                firstInstallment
        );
        BigDecimal interest = loanInstalmentCalculator.calculateInterest(loanAmount, annualRate, firstInstallment, LocalDate.of(2023, 10, 1));
        System.out.println("installmentAmount = " + installmentAmount);
        System.out.println("interest = " + interest);
        loanAmount = loanAmount.subtract(installmentAmount).add(interest);
        System.out.println("loanAmount = " + loanAmount);

        installmentAmount = loanInstalmentCalculator.calculateInstallmentAmountAfterOverpayment(
                loanAmount,
                new BigDecimal("6210.09"),
                annualRate,
                359,
                LocalDate.of(2023, 10, 2),
                LocalDate.of(2023, 10, 3)
        );
        System.out.println("installmentAmount = " + installmentAmount);
    }
}