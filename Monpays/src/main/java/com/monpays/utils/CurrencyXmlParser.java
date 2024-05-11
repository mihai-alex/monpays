package com.monpays.utils;

import com.monpays.entities._generic.Currency;
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
    public final String xmlPath;
    @Getter
    private final List<Currency> currencies;

    public CurrencyXmlParser(@Value("${currencies.xml.path}") String xmlPath) {
        this.xmlPath = xmlPath;
        currencies = this.parseXml();
    }

    public Optional<Currency> getCurrencyByCode(String code) {
        return currencies
                .stream()
                .filter(currency -> currency.getCode().equals(code))
                .findAny();
    }

    private List<Currency> parseXml() {
        try {
            File xmlFile = new ClassPathResource(this.xmlPath).getFile();

            jakarta.xml.bind.JAXBContext jaxbCtx = jakarta.xml.bind.JAXBContext.newInstance(Currencies.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            Currencies currenciesWrapper = (Currencies) unmarshaller.unmarshal(xmlFile);
            return currenciesWrapper.getCurrencies();
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
}
