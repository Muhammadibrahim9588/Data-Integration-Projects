package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.IND;

import java.util.*;
import java.util.stream.Collectors;

public class INDProfiler {

    /**
     * Discovers all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     * @param relations The relations that should be profiled for inclusion dependencies.
     * @return The list of all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     */
    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all inclusion dependencies and return them in inclusion dependencies list. The boolean flag       //
        // discoverNary indicates, whether only unary or both unary and n-ary INDs should be discovered. To solve     //
        // this assignment, only unary INDs need to be discovered. Discovering also n-ary INDs is optional.           //

        for (int i = 0; i < relations.size(); i++) {
            Relation currentRelation = relations.get(i);
            String[][] currentColumns = currentRelation.getColumns();

            // Check within the same relation for INDs
            for (int col1 = 0; col1 < currentColumns.length; col1++) {
                for (int col2 = 0; col2 < currentColumns.length; col2++) {
                    if (col1 != col2) { // Avoid self-references
                        List<String> col1Values = Arrays.asList(currentColumns[col1]);
                        List<String> col2Values = Arrays.asList(currentColumns[col2]);

                        // Check if col2Values contain all elements of col1Values
                        if (col2Values.containsAll(col1Values)) {
                            IND ind = new IND(currentRelation, col1, currentRelation, col2);
                            if (!inclusionDependencies.contains(ind)) {
                                inclusionDependencies.add(ind);
                            }
                        }
                    }
                }
            }

            // Check across different relations for INDs
            for (int j = 0; j < relations.size(); j++) {
                if (i != j) { // Avoid self-references
                    Relation otherRelation = relations.get(j);
                    String[][] otherColumns = otherRelation.getColumns();

                    // Compare columns between currentRelation and otherRelation
                    for (int col1 = 0; col1 < currentColumns.length; col1++) {
                        for (int col2 = 0; col2 < otherColumns.length; col2++) {
                            List<String> col1Values = Arrays.asList(currentColumns[col1]);
                            List<String> col2Values = Arrays.asList(otherColumns[col2]);

                            // Check if col2Values contain all elements of col1Values
                            if (col2Values.containsAll(col1Values)) {
                                IND ind = new IND(currentRelation, col1, otherRelation, col2);
                                if (!inclusionDependencies.contains(ind)) {
                                    inclusionDependencies.add(ind);
                                }
                            }
                        }
                    }
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (discoverNary)
            // Here, the lattice search would start if n-ary IND discovery would be supported.
            throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");

        return inclusionDependencies;
    }

    private List<Set<String>> toColumnSets(String[][] columns) {
        return Arrays.stream(columns)
                .map(column -> new HashSet<>(new ArrayList<>(List.of(column))))
                .collect(Collectors.toList());
    }
}
