package com.monpays.services.implementations._generic;

import com.monpays.entities._generic.ExchangeRate;
import com.monpays.services.interfaces._generic.ICurrencyConversionService;
import com.monpays.utils.CurrencyXmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CurrencyConversionService implements ICurrencyConversionService {

    private final CurrencyXmlParser currencyXmlParser;

    @Autowired
    public CurrencyConversionService(CurrencyXmlParser currencyXmlParser) {
        this.currencyXmlParser = currencyXmlParser;
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode) {
        if (fromCurrencyCode.equals(toCurrencyCode)) {
            return amount;
        }

        String currencyPair = fromCurrencyCode + "/" + toCurrencyCode;
        Optional<ExchangeRate> exchangeRateOptional = currencyXmlParser.getExchangeRateByCurrencyPair(currencyPair);

        if (exchangeRateOptional.isEmpty()) {
            throw new IllegalArgumentException("Exchange rate not found for currency pair: " + currencyPair);
        }

        BigDecimal rate = exchangeRateOptional.get().getRate();
        return amount.multiply(rate);
    }
}
