package com.reks.config;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class CryptoConfig {

    @Bean
    public Pairing pairing() {
        // Using Type A pairing for efficiency
        return PairingFactory.getPairing("a.properties");
    }

    @Bean
    public Field<?> gtField(Pairing pairing) {
        return pairing.getGT();
    }

    @Bean
    public Field<?> g1Field(Pairing pairing) {
        return pairing.getG1();
    }

    @Bean
    public Field<?> zrField(Pairing pairing) {
        return pairing.getZr();
    }

    @Bean
    public Element generator(Pairing pairing) {
        return pairing.getG1().newRandomElement();
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}