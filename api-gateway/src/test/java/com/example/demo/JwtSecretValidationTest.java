package com.example.demo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.config.JwtService;

/**
 * Une clé absente ou trop faible doit empêcher le démarrage. Sans ce garde-fou,
 * un déploiement mal configuré tournerait avec une signature devinable, et
 * n'importe qui pourrait fabriquer un jeton d'administrateur.
 */
class JwtSecretValidationTest {

    @Test
    @DisplayName("Une clé absente, trop courte ou mal encodée fait échouer le démarrage")
    void aMissingOrWeakSecretIsRefused() {
        assertThatThrownBy(() -> new JwtService(""))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET is not set");

        assertThatThrownBy(() -> new JwtService("c2hvcnQ="))   // "short" : 5 octets
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("256 bits");

        assertThatThrownBy(() -> new JwtService("pas-du-base64!!"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Base64");
    }

    @Test
    @DisplayName("Une clé de 256 bits en Base64 est acceptée")
    void aProperSecretIsAccepted() {
        assertThatCode(() -> new JwtService("dGVzdC1zZWNyZXQtcG91ci1sZXMtdGVzdHMtMjU2LWJpdHMhIQ=="))
                .doesNotThrowAnyException();
    }
}
