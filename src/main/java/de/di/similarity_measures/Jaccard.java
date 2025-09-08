package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        if (bagSemantics) {
            return calculateBagJaccard(strings1, strings2);
        } else {
            return calculateSetSemantics(strings1, strings2);
        }
    }

    private double calculateSetSemantics(String[] strings1, String[] strings2) {
        Set<String> set1 = new HashSet<>(Arrays.asList(strings1));
        Set<String> set2 = new HashSet<>(Arrays.asList(strings2));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private double calculateBagJaccard(String[] strings1, String[] strings2) {
        Map<String, Integer> freqMap1 = new HashMap<>();
        Map<String, Integer> freqMap2 = new HashMap<>();

        for (String str : strings1) {
            freqMap1.put(str, freqMap1.getOrDefault(str, 0) + 1);
        }
        for (String str : strings2) {
            freqMap2.put(str, freqMap2.getOrDefault(str, 0) + 1);
        }

        Set<String> allTokens = new HashSet<>(freqMap1.keySet());
        allTokens.addAll(freqMap2.keySet());

        int intersectionCount = 0;
        int unionCount = 0;


        for (String token : allTokens) {
            int count1 = freqMap1.getOrDefault(token, 0);
            int count2 = freqMap2.getOrDefault(token, 0);
            intersectionCount += Math.min(count1, count2);
            unionCount += count1 + count2;
        }

        if (unionCount == 0) {
            return 0.0;
        }
        return (double) intersectionCount / unionCount;
    }
}