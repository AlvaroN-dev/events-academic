package com.codeup.riwi.tiqueteracatalogo.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuration for validation with custom message resolution.
 * Integrates Spring's MessageSource with Jakarta Bean Validation
 * to provide externalized validation messages.
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Custom messages from messages.properties</li>
 *   <li>UTF-8 encoding for international character support</li>
 *   <li>Automatic message resolution for validation annotations</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Configuration
public class ValidationConfig {

    /**
     * Creates a MessageSource that loads messages from properties files.
     * The messages.properties file contains all validation messages.
     * 
     * @return configured MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for 1 hour in production
        return messageSource;
    }

    /**
     * Creates a validator factory that uses our MessageSource for message interpolation.
     * This allows @NotBlank, @Size, and other validation annotations to use
     * messages defined in messages.properties via the {message.key} syntax.
     * 
     * @param messageSource the MessageSource to use for message resolution
     * @return configured LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
