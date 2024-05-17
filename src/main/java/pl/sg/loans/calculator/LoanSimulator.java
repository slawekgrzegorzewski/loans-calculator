package pl.sg.loans.calculator;

import pl.sg.loans.model.LoanCalculationInstallment;
import pl.sg.loans.model.LoanCalculationParams;
import pl.sg.loans.overpayment.LoanOverpaymentStrategyFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;

public class LoanSimulator {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final LoanOverpaymentStrategyFactory loanOverpaymentStrategyFactory;
    private final LoanInstalmentCalculator loanInstalmentCalculator;
    private final int lessPreciseScale;
    private final int morePreciseScale;

    public LoanSimulator(
            LoanInstalmentCalculator loanInstalmentCalculator,
            LoanOverpaymentStrategyFactory loanOverpaymentStrategyFactory,
            int lessPreciseScale,
            int morePreciseScale) {
        this.loanInstalmentCalculator = loanInstalmentCalculator;
        this.loanOverpaymentStrategyFactory = loanOverpaymentStrategyFactory;
        this.lessPreciseScale = lessPreciseScale;
        this.morePreciseScale = morePreciseScale;
    }

    public List<LoanCalculationInstallment> simulate(LoanCalculationParams loanCalculationParams) {
        List<LoanCalculationInstallment> installments = new ArrayList<>();
        BigDecimal leftToRepay = loanCalculationParams.loanAmount();
        BigDecimal annualRate = loanCalculationParams.wibor().add(loanCalculationParams.rate()).divide(HUNDRED, morePreciseScale, HALF_DOWN);

        BigDecimal installmentAmount = loanInstalmentCalculator.calculateInstallmentAmount(leftToRepay, annualRate, loanCalculationParams.numberOfInstallments(), loanCalculationParams.repaymentStart());

        while (leftToRepay.compareTo(ZERO) > 0) {
            LocalDate installmentStartDate = installments.isEmpty()
                    ? loanCalculationParams.repaymentStart()
                    : installments.get(installments.size() - 1).paymentTo().plusDays(1);

            LocalDate installmentEndDate = installmentStartDate.plusMonths(1).withDayOfMonth(1);

            if (installmentAmount.compareTo(ZERO) == 0) {
                installmentAmount = loanInstalmentCalculator.calculateInstallmentAmount(leftToRepay, annualRate, loanCalculationParams.numberOfInstallments() - installments.size(), installmentStartDate);
            }

            BigDecimal interestToPay = loanInstalmentCalculator.calculateInterest(leftToRepay, annualRate, installmentStartDate, installmentEndDate);

            BigDecimal repaidCapital = installmentAmount.subtract(interestToPay);
            BigDecimal overpayment = loanOverpaymentStrategyFactory
                    .create(loanCalculationParams)
                    .calculateOverpayment(installments.size() + 1, installmentAmount);

            if (leftToRepay.subtract(repaidCapital).compareTo(ZERO) < 0) {
                installmentAmount = leftToRepay.add(interestToPay);
                repaidCapital = leftToRepay;
                overpayment = ZERO;
            } else if (leftToRepay.subtract(repaidCapital).subtract(overpayment).compareTo(ZERO) < 0) {
                overpayment = leftToRepay.subtract(repaidCapital);
            }

            installments.add(
                    new LoanCalculationInstallment(
                            installmentStartDate,
                            installmentEndDate,
                            leftToRepay.setScale(lessPreciseScale, HALF_DOWN),
                            installmentAmount.setScale(lessPreciseScale, HALF_DOWN),
                            interestToPay.setScale(lessPreciseScale, HALF_DOWN),
                            repaidCapital.setScale(lessPreciseScale, HALF_DOWN),
                            overpayment
                    )
            );

            leftToRepay = leftToRepay.subtract(repaidCapital).subtract(overpayment);
            if (leftToRepay.compareTo(ZERO) <= 0) {
                break;
            }
            if (overpayment.compareTo(ZERO) > 0) {
                installmentAmount = ZERO;
            }
        }
        if (leftToRepay.compareTo(ZERO) > 0) {
            LocalDate installmentStartDate = installments.isEmpty()
                    ? loanCalculationParams.repaymentStart()
                    : installments.get(installments.size() - 1).paymentTo().plusDays(1);
            LocalDate installmentEndDate = installmentStartDate.plusMonths(1).minusDays(1);

            BigDecimal interestToPay = loanInstalmentCalculator.calculateInterest(leftToRepay, annualRate, installmentStartDate, installmentEndDate);

            installments.add(
                    new LoanCalculationInstallment(
                            installmentStartDate,
                            installmentEndDate,
                            leftToRepay.setScale(lessPreciseScale, HALF_DOWN),
                            interestToPay.add(leftToRepay),
                            interestToPay.setScale(lessPreciseScale, HALF_DOWN),
                            leftToRepay.setScale(lessPreciseScale, HALF_DOWN),
                            ZERO
                    )
            );
        }
        return installments;
    }

}
