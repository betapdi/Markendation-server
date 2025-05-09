package com.markendation.server.utils;

public class KMPProductMatcher {
    private String pattern;
    int[] lps;

    public KMPProductMatcher(String pattern) {
        this.pattern = pattern.toLowerCase();
        lps = computeLPSArray(this.pattern);
    }

    private static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    public boolean isMatch(String s3) {
        String s2 = s3.toLowerCase();
        if (pattern == null || s2 == null || pattern.length() > s2.length()) {
            return false;
        }

        int i = 0; // index for s2
        int j = 0; // index for s1

        while (i < s2.length()) {
            if (pattern.charAt(j) == s2.charAt(i)) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                return true;
            } else if (i < s2.length() && pattern.charAt(j) != s2.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return false;
    }
}
