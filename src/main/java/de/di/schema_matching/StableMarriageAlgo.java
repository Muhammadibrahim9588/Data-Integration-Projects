package de.di.schema_matching;

import java.util.Arrays;

public class StableMarriageAlgo {
    public static int[] stableMarriage(double[][] simMatrix) {
        int n = simMatrix.length;
        int m = simMatrix[0].length;

        int[] sourceMatches = new int[n];
        int[] targetMatches = new int[m];
        Arrays.fill(sourceMatches, -1);
        Arrays.fill(targetMatches, -1);

        boolean proposalMade;

        do {
            proposalMade = false;

            for (int i = 0; i < n; i++) {
                if (sourceMatches[i] == -1) {
                    int bestTarget = findBestTarget(i, simMatrix, sourceMatches, targetMatches);

                    if (bestTarget != -1) {
                        int currentMatch = targetMatches[bestTarget];
                        if (currentMatch != -1) {
                            sourceMatches[currentMatch] = -1;
                        }

                        sourceMatches[i] = bestTarget;
                        targetMatches[bestTarget] = i;
                        proposalMade = true;
                    }
                }
            }
        } while (proposalMade);

        return sourceMatches;
    }

    private static int findBestTarget(int sourceIndex, double[][] simMatrix, int[] sourceMatches, int[] targetMatches) {
        int bestTarget = -1;
        double bestScore = -1.0;

        for (int j = 0; j < simMatrix[0].length; j++) {
            if (targetMatches[j] == -1 || simMatrix[sourceIndex][j] > simMatrix[targetMatches[j]][j]) {
                if (bestTarget == -1 || simMatrix[sourceIndex][j] > bestScore) {
                    bestTarget = j;
                    bestScore = simMatrix[sourceIndex][j];
                }
            }
        }

        return bestTarget;
    }
}

