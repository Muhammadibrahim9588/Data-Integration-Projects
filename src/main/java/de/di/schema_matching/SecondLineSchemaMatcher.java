package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;

import java.util.Arrays;

public class SecondLineSchemaMatcher {

    /**
     * Translates the provided similarity matrix into a binary correspondence matrix by selecting possibly optimal
     * attribute correspondences from the similarities.
     * @param similarityMatrix A matrix of pair-wise attribute similarities.
     * @return A CorrespondenceMatrix of pair-wise attribute correspondences.
     */
    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Translate the similarity matrix into a binary correlation matrix by implementing either the StableMarriage //
        // algorithm or the Hungarian method.                                                                         //

        if (simMatrix == null || simMatrix.length == 0) {
            throw new IllegalArgumentException("Similarity matrix cannot be null or empty");
        }

        int n = simMatrix.length;
        int m = simMatrix[0].length;


        if (Arrays.stream(simMatrix).anyMatch(row -> row.length != m)) {
            throw new IllegalArgumentException("All rows in the similarity matrix must have the same length");
        }


        int[] sourceMatches = StableMarriageAlgo.stableMarriage(simMatrix);

        int[][] corrMatrix = convertToCorrelationMatrix(sourceMatches, n, m);

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());
    }

    /**
     * Translate an array of source assignments into a correlation matrix. For example, [0,3,2] maps 0->1, 1->3, 2->2
     * and, therefore, translates into [[1,0,0,0][0,0,0,1][0,0,1,0]].
     * @param sourceAssignments The list of source assignments.
     * @param simMatrix The original similarity matrix; just used to determine the number of source and target attributes.
     * @return The correlation matrix extracted form the source assignments.
     */
    private int[][] assignmentArray2correlationMatrix(int[] sourceAssignments, double[][] simMatrix) {
        int[][] corrMatrix = new int[simMatrix.length][];
        for (int i = 0; i < simMatrix.length; i++) {
            corrMatrix[i] = new int[simMatrix[i].length];
            for (int j = 0; j < simMatrix[i].length; j++)
                corrMatrix[i][j] = 0;
        }
        for (int i = 0; i < sourceAssignments.length; i++)
            if (sourceAssignments[i] >= 0)
                corrMatrix[i][sourceAssignments[i]] = 1;
        return corrMatrix;
    }
}
