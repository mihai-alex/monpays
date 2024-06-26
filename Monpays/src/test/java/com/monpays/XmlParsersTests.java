//package com.monpays;
//
//import com.monpays.utils.CurrencyXmlParser;
//import com.monpays.utils.ProfileTemplateXmlParser;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.logging.Logger;
//
//@SpringBootTest
//public class XmlParsersTests {
//    @Autowired
//    private CurrencyXmlParser currencyXmlParser;
//    @Autowired
//    private ProfileTemplateXmlParser profileTemplateXmlParser;
//    private final Logger log = Logger.getLogger(XmlParsersTests.class.getName());
//
//    @Test
//    void testCurrencyXmlParser() {
//        log.info("Testing CurrencyXmlParser");
//
//        Assertions.assertFalse(currencyXmlParser.getCurrencies().isEmpty());
////        log.info("CurrencyXmlParser.currencies: " + currencyXmlParser.getCurrencies());
//        Assertions.assertTrue(currencyXmlParser.getCurrencyByCode("EUR").isPresent());
////        log.info("CurrencyXmlParser.getCurrencyByCode(\"EUR\"): " + currencyXmlParser.getCurrencyByCode("EUR"));
//
//        log.info("Hurray! Tests passed for CurrencyXmlParser");
//    }
//
//    @Test
//    void testProfileTemplateXmlParser() {
//        log.info("Testing ProfileTemplateXmlParser");
//
//        Assertions.assertFalse(profileTemplateXmlParser.getProfileTemplates().isEmpty());
////        log.info("ProfileTemplateXmlParser.profileTemplates: " + profileTemplateXmlParser.getProfileTemplates());
//        Assertions.assertTrue(profileTemplateXmlParser.getProfileTemplateByName("ADMINISTRATOR").isPresent());
//        Assertions.assertFalse(profileTemplateXmlParser.getProfileTemplateByName("ADMINISTRATOR").get().getRights().isEmpty());
//        Assertions.assertTrue(profileTemplateXmlParser.getProfileTemplateByName("EMPLOYEE").isPresent());
//        Assertions.assertTrue(profileTemplateXmlParser.getProfileTemplateByName("CUSTOMER").isPresent());
////        log.info("ProfileTemplateXmlParser.getProfileTemplateByName(\"CUSTOMER\"): " + profileTemplateXmlParser.getProfileTemplateByName("default"));
//
//        log.info("Hurray! Tests passed for ProfileTemplateXmlParser");
//    }
//}
