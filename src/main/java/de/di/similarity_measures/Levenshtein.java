package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    // Utility method to find the minimum value among the given numbers
    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // The choice of whether Levenshtein or Damerau-Levenshtein should be calculated
    private final boolean withDamerau;

    /**
     * Calculates the Levenshtein similarity of the two input strings.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The (Damerau) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        // Handle null inputs by converting them to empty strings
        final int len1 = string1.length();
        final int len2 = string2.length();

        // Initialize the distance matrix
        int[][] distance = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            distance[0][j] = j;
        }

        // Fill the distance matrix
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (string1.charAt(i - 1) == string2.charAt(j - 1)) ? 0 : 1;

                distance[i][j] = min(
                        distance[i - 1][j] + 1,     // Deletion
                        distance[i][j - 1] + 1,     // Insertion
                        distance[i - 1][j - 1] + cost // Substitution
                );

                // Damerau-Levenshtein transposition
                if (withDamerau && i > 1 && j > 1 &&
                        string1.charAt(i - 1) == string2.charAt(j - 2) &&
                        string1.charAt(i - 2) == string2.charAt(j - 1)) {
                    distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + cost);
                }
            }
        }

        // Calculate the Levenshtein similarity
        int levenshteinDistance = distance[len1][len2];
        int maxLength = Math.max(len1, len2);
        double normalizedDistance = (double) levenshteinDistance / maxLength;
        return 1.0 - normalizedDistance;
    }

    /**
     * Calculates the Levenshtein similarity of the two input string lists.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * For string lists, we consider each list as an ordered list of tokens and calculate the distance as the number of
     * token insertions, deletions, replacements (and swaps) that transform one list into the other.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The (multiset) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        final int len1 = strings1.length;
        final int len2 = strings2.length;

        // Initialize the distance matrix
        int[][] distance = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            distance[0][j] = j;
        }

        // Fill the distance matrix
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (strings1[i - 1].equals(strings2[j - 1])) ? 0 : 1;

                distance[i][j] = min(
                        distance[i - 1][j] + 1,     // Deletion
                        distance[i][j - 1] + 1,     // Insertion
                        distance[i - 1][j - 1] + cost // Substitution
                );

                // Damerau-Levenshtein transposition
                if (withDamerau && i > 1 && j > 1 &&
                        strings1[i - 1].equals(strings2[j - 2]) &&
                        strings1[i - 2].equals(strings2[j - 1])) {
                    distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + cost);
                }
            }
        }

        // Calculate the Levenshtein similarity
        int levenshteinDistance = distance[len1][len2];
        int maxLength = Math.max(len1, len2);
        double normalizedDistance = (double) levenshteinDistance / maxLength;
        return 1.0 - normalizedDistance;
    }
}