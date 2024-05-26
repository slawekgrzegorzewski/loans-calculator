package pl.sg.loans.utils;

import java.time.LocalDate;

public interface RepaymentDayStrategy {
    LocalDate getNextPaymentDay(LocalDate lastPayment);
}
