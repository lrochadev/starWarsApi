package br.com.challenge.b2w.starWarsApi.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Leonardo Rocha
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String getMessage(String msg, String... values) {
        return messageSource.getMessage(msg, values, LocaleContextHolder.getLocale());
    }
}
