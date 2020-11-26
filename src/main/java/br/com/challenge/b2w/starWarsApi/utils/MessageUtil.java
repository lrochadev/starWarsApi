package br.com.challenge.b2w.starWarsApi.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * @author Leonardo Rocha
 */
@Configuration
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String getMessage(String msg, String... values) {
        Locale.setDefault(new Locale("pt", "BR"));
        return messageSource.getMessage(msg, values, Locale.getDefault());
    }
}
