package pl.sg.loans.calculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.time.temporal.ChronoUnit.DAYS;

public class LoanInstalmentCalculator {

    private final int lessPreciseScale;
    private final int morePreciseScale;

    public LoanInstalmentCalculator(int lessPreciseScale, int morePreciseScale) {
        this.lessPreciseScale = lessPreciseScale;
        this.morePreciseScale = morePreciseScale;
    }

    private record Calculation(LocalDate periodStart, BigDecimal sumOfFactors, BigDecimal multiplicationOfRates) {
    }

    public BigDecimal calculateInstallmentAmount(BigDecimal loanAmount, BigDecimal annualRate, int numberOfInstallments, LocalDate startOfRepayment) {
        if (numberOfInstallments == 0) {
            return loanAmount;
        }
        Calculation calculation = new Calculation(startOfRepayment, ZERO, ONE);
        for (int i = 1; i <= numberOfInstallments; i++) {
            LocalDate periodEnd = i == numberOfInstallments
                    ? calculation.periodStart().plusMonths(1).withDayOfMonth(startOfRepayment.getDayOfMonth())
                    : calculation.periodStart().plusMonths(1).withDayOfMonth(1);
            calculation = calculatePeriod(calculation, annualRate, periodEnd);
        }
        return loanAmount.divide(calculation.sumOfFactors(), lessPreciseScale, HALF_DOWN);
    }

    public BigDecimal calculateInstallmentAmountAfterOverpayment(
            BigDecimal loanAmountBeforeOverpayment,
            BigDecimal overpayment,
            BigDecimal annualRate,
            int numberOfInstallmentsLeft,
            LocalDate startOfRepayment,
            LocalDate overpaymentDate) {
        if (numberOfInstallmentsLeft == 0) {
            return loanAmountBeforeOverpayment.subtract(overpayment);
        }
        Calculation calculation = new Calculation(startOfRepayment, ZERO, ONE);
        for (int i = 1; i <= numberOfInstallmentsLeft; i++) {
            LocalDate periodEnd = i == numberOfInstallmentsLeft
                    ? calculation.periodStart().plusMonths(1).withDayOfMonth(startOfRepayment.getDayOfMonth())
                    : calculation.periodStart().plusMonths(1).withDayOfMonth(1);
            calculation = calculatePeriod(calculation, annualRate, periodEnd);
        }
        return loanAmountBeforeOverpayment.subtract(overpayment).divide(calculation.sumOfFactors(), lessPreciseScale, HALF_DOWN);
    }

    private Calculation calculatePeriod(
            Calculation calculation,
            BigDecimal annualRate,
            LocalDate periodEnd) {
        BigDecimal periodRate = calculatePeriodRate(annualRate, calculation.periodStart(), periodEnd);
        BigDecimal onePlusPeriodRate = periodRate.add(ONE);
        BigDecimal multiplicationOfRates = calculation.multiplicationOfRates().multiply(onePlusPeriodRate);
        BigDecimal sumOfFactors = calculation.sumOfFactors().add(ONE.divide(multiplicationOfRates, morePreciseScale, HALF_DOWN));
        return new Calculation(periodEnd.plusDays(1), sumOfFactors, multiplicationOfRates);
    }

    public BigDecimal calculateInterest(BigDecimal leftToRepay, BigDecimal annualRate, LocalDate installmentStartDate, LocalDate installmentEndDate) {
        return calculatePeriodRate(annualRate, installmentStartDate, installmentEndDate).multiply(leftToRepay).setScale(lessPreciseScale, HALF_DOWN);
    }

    private BigDecimal calculatePeriodRate(BigDecimal annualRate, LocalDate fromInclusive, LocalDate toInclusive) {
        if (theSameMonth(fromInclusive, toInclusive)) {
            BigDecimal daysBetween = BigDecimal.valueOf(DAYS.between(fromInclusive, toInclusive.plusDays(1))).setScale(morePreciseScale, HALF_DOWN);
            BigDecimal dailyRate = annualRate.divide(daysInYear(fromInclusive), morePreciseScale, HALF_DOWN);
            return daysBetween.multiply(dailyRate);
        }
        return calculatePeriodRate(annualRate, fromInclusive, endOfMonth(fromInclusive))
                .add(calculatePeriodRate(annualRate, beginningOfNextMonth(fromInclusive), toInclusive));
    }

    private static LocalDate endOfMonth(LocalDate installmentStartDate) {
        return YearMonth.from(installmentStartDate).atEndOfMonth();
    }

    private static LocalDate beginningOfNextMonth(LocalDate installmentStartDate) {
        return YearMonth.from(installmentStartDate).atEndOfMonth().plusDays(1);
    }

    private static BigDecimal daysInYear(LocalDate installmentStartDate) {
        return BigDecimal.valueOf(Year.from(installmentStartDate).length());
    }

    private static boolean theSameMonth(LocalDate installmentStartDate, LocalDate installmentEndDate) {
        return YearMonth.from(installmentStartDate).equals(YearMonth.from(installmentEndDate));
    }
}
