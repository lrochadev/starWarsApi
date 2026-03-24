package br.com.challenge.b2w.starWarsApi.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author Leonardo Rocha
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    private final MessageSource messageSource;

    public String getMessage(String msg, String... values) {
        return messageSource.getMessage(msg, values, PT_BR);
    }
}
