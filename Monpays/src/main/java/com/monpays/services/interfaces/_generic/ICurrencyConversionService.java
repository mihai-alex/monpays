package com.monpays.services.interfaces._generic;

import java.math.BigDecimal;

public interface ICurrencyConversionService {
    BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode);
}
