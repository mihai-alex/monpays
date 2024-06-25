package com.monpays.entities._generic;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRate {
    private String currencyPair;
    private BigDecimal rate;
}
