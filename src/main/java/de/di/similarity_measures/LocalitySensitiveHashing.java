package de.di.similarity_measures;

import de.di.similarity_measures.helper.MinHash;
import de.di.similarity_measures.helper.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class LocalitySensitiveHashing implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    // The MinHash functions that are used to calculate the LSH signatures.
    private final List<MinHash> minHashFunctions;

    public LocalitySensitiveHashing(final Tokenizer tokenizer, final boolean bagSemantics, final int numHashFunctions) {
        assert(tokenizer.getTokenSize() >= numHashFunctions);

        this.tokenizer = tokenizer;
        this.bagSemantics = bagSemantics;
        this.minHashFunctions = new ArrayList<>(numHashFunctions);
        for (int i = 0; i < numHashFunctions; i++)
            this.minHashFunctions.add(new MinHash(i));
    }

    /**
     * Calculates the LSH similarity of the two input strings.
     * The LHS algorithm calculates the LHS signatures by first tokenizing the input strings and then applying its
     * internal MinHash functions to the tokenized strings. Then, it uses the two signatures to approximate the Jaccard
     * similarity of the two strings with their signatures by simply applying the Jaccard algorithm on the two signatures.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The LSH similarity (= Jaccard approximation) of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the LSH similarity of the two input string arrays.
     * The LHS algorithm calculates the LHS signatures by applying its internal MinHash functions to the two input string
     * lists. Then, it uses the two signatures to approximate the Jaccard similarity of the two strings with their
     * signatures by simply applying the Jaccard algorithm on the two signatures.
     * @param strings1 The first string argument for the similarity calculation.
     * @param strings2 The second string argument for the similarity calculation.
     * @return The LSH similarity (= Jaccard approximation) of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        int numHashFunctions = this.minHashFunctions.size();
        int[] signature1 = new int[numHashFunctions];
        int[] signature2 = new int[numHashFunctions];

        // Calculate MinHash signatures for both sets of strings
        for (int i = 0; i < numHashFunctions; i++) {
            MinHash minHash = this.minHashFunctions.get(i);
            signature1[i] = minHash.compute(strings1);
            signature2[i] = minHash.compute(strings2);
        }

        // Calculate Jaccard similarity between the two signatures
        int intersection = 0;
        for (int i = 0; i < numHashFunctions; i++) {
            if (signature1[i] == signature2[i]) {
                intersection++;
            }
        }

        // Calculate LSH Jaccard similarity
        double lshJaccard = (double) intersection / numHashFunctions;

        return lshJaccard;
    }
}