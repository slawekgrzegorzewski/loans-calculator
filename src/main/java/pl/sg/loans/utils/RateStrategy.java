package pl.sg.loans.utils;

import java.math.BigDecimal;

public interface RateStrategy {
    BigDecimal getNextInstallmentRate();
    BigDecimal getNextInstallmentPercent();
}
