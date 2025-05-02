package com.markendation.server.utils;

import org.springframework.data.util.Pair;

public class UnitQuantityParser {
    public static Pair<Integer, String> parseQuantity(String input) {
        String regex = "(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            Integer quantity = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);
            return Pair.of(quantity, unit);
        }

        return Pair.of(-1, "g");
    }
}
