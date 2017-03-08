package com.dropbox.core;

import com.dropbox.core.http.HttpRequestor;

import java.util.Locale;

/*>>> import checkers.nullness.quals.Nullable; */

/**
 * A grouping of a few configuration parameters for how we should make requests to the
 * Dropbox servers.
 */
public class DbxRequestConfig {
    private final String clientIdentifier;
    private final /*@Nullable*/String userLocale;
    private final HttpRequestor httpRequestor;
    private final int maxRetries;

    private DbxRequestConfig(String clientIdentifier, /*@Nullable*/ String userLocale, HttpRequestor httpRequestor, int maxRetries) {
        if (clientIdentifier == null) throw new NullPointerException("clientIdentifier");
        if (httpRequestor == null) throw new NullPointerException("httpRequestor");
        if (maxRetries < 0) throw new IllegalArgumentException("maxRetries");

        this.clientIdentifier = clientIdentifier;
        this.userLocale = toLanguageTag(userLocale);
        this.httpRequestor = httpRequestor;
        this.maxRetries = maxRetries;
    }

    // Available in Java 7, but not in Java 6. Do a hacky version of it here.
    private static String toLanguageTag(Locale locale) {
        if (locale == null) {
            return null;
        }
        StringBuilder tag = new StringBuilder();

        tag.append(locale.getLanguage().toLowerCase());

        if (!locale.getCountry().isEmpty()) {
            tag.append("-");
            tag.append(locale.getCountry().toUpperCase());
        }

        return tag.toString();
    }

    // APIv1 accepts Locale.toString() formatted locales (e.g. 'en_US'), but APIv2 will return an
    // error if the locale is not in proper Language Tag format. Attempt to convert old locale
    // formats to the new one.
    private static String toLanguageTag(String locale) {
        if (locale == null) {
            return null;
        }
        // assume we are already a language tag
        if (!locale.contains("_")) {
            return locale;
        }

        // language can be missing, in which case we don't even bother
        if (locale.startsWith("_")) {
            return locale;
        }

        // Java 6 does "lang_country_variant". If country is missing, then "lang__variant". If no
        // variant, then just "lang_country".
        String [] parts = locale.split("_", 3);

        String lang = parts[0];
        String country = parts[1];
        String variant = parts.length == 3 ? parts[2] : "";

        return toLanguageTag(new Locale(lang, country, variant));
    }

}
