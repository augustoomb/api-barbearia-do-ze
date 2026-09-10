package com.augustoomb.api_barbearia_do_ze.domain.service;

import java.text.Normalizer;

public final class NameNormalizer {

    private NameNormalizer() {
    }

    public static String normalize(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        String withoutAccents = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return withoutAccents.toLowerCase();
    }
}
