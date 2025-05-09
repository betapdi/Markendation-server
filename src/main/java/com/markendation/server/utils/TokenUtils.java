package com.markendation.server.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TokenUtils {
    public static List<String> tokenizeByWhitespace(String text) {
        if (text == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.toLowerCase().split("\\s+"))
            .filter(token -> token.length() >= 2)
            .collect(Collectors.toList());
    }

    public static List<String> generateNGrams(String token, int n) {
        if (token == null || token.length() < n) {
            return Collections.emptyList();
        }
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= token.length() - n; i++) {
            nGrams.add(token.substring(i, i + n));
        }
        return nGrams;
    }

    public static List<String> generateTokenNGrams(String text, int n) {
        return tokenizeByWhitespace(text).stream()
            .flatMap(token -> generateNGrams(token, n).stream())
            .collect(Collectors.toList());
    }
}
