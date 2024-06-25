package com.monpays.utils;

import com.monpays.entities._generic.Currency;
import com.monpays.entities._generic.ExchangeRate;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CurrencyXmlParser {
    public final String currenciesXmlPath;
    public final String exchangeRatesXmlPath;
    @Getter
    private final List<Currency> currencies;
    @Getter
    private final List<ExchangeRate> exchangeRates;

    public CurrencyXmlParser(@Value("${currencies.xml.path}") String currenciesXmlPath,
                             @Value("${exchange_rates.xml.path}") String exchangeRatesXmlPath) {
        this.currenciesXmlPath = currenciesXmlPath;
        this.exchangeRatesXmlPath = exchangeRatesXmlPath;
        currencies = this.parseCurrenciesXml();
        exchangeRates = this.parseExchangeRatesXml();
    }

    public Optional<Currency> getCurrencyByCode(String code) {
        return currencies
                .stream()
                .filter(currency -> currency.getCode().equals(code))
                .findAny();
    }

    public Optional<ExchangeRate> getExchangeRateByCurrencyPair(String currencyPair) {
        return exchangeRates
                .stream()
                .filter(exchangeRate -> exchangeRate.getCurrencyPair().equals(currencyPair))
                .findAny();
    }

    private List<Currency> parseCurrenciesXml() {
        try {
            File xmlFile = new ClassPathResource(this.currenciesXmlPath).getFile();

            jakarta.xml.bind.JAXBContext jaxbCtx = jakarta.xml.bind.JAXBContext.newInstance(Currencies.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            Currencies currenciesWrapper = (Currencies) unmarshaller.unmarshal(xmlFile);
            return currenciesWrapper.getCurrencies();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<ExchangeRate> parseExchangeRatesXml() {
        try {
            File xmlFile = new ClassPathResource(this.exchangeRatesXmlPath).getFile();

            jakarta.xml.bind.JAXBContext jaxbCtx = jakarta.xml.bind.JAXBContext.newInstance(ExchangeRates.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            ExchangeRates exchangeRatesWrapper = (ExchangeRates) unmarshaller.unmarshal(xmlFile);
            return exchangeRatesWrapper.getExchangeRates();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @XmlRootElement(name = "currencies")
    static class Currencies {
        private List<Currency> currencies = new ArrayList<>();

        @XmlElement(name = "currency")
        public List<Currency> getCurrencies() {
            return currencies;
        }
    }

    @XmlRootElement(name = "exchangeRates")
    static class ExchangeRates {
        private List<ExchangeRate> exchangeRates = new ArrayList<>();

        @XmlElement(name = "exchangeRate")
        public List<ExchangeRate> getExchangeRates() {
            return exchangeRates;
        }
    }
}
