package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UCCProfiler {

    /**
     * Discovers all minimal, non-trivial unique column combinations in the provided relation.
     *
     * @param relation The relation that should be profiled for unique column combinations.
     * @return The list of all minimal, non-trivial unique column combinations in ths provided relation.
     */
    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

        // Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all unique column combinations of size n>1 by traversing the lattice level-wise. Make sure to     //
        // generate only minimal candidates while moving upwards and to prune non-minimal ones. Hint: The class       //
        // AttributeList offers some helpful functions to test for sub- and superset relationships. Use PLI           //
        // intersection to validate the candidates in every lattice level. Advances techniques, such as random walks, //
        // hybrid search strategies, or hitting set reasoning can be used, but are mandatory to pass the assignment.  //


        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int level = 1;

        while (!currentNonUniques.isEmpty()) {
            List<PositionListIndex> nextNonUniques = new ArrayList<>();
            Set<AttributeList> existingCombinations = new HashSet<>();

            // Check combinations of current non-unique PLIs
            for (int i = 0; i < currentNonUniques.size(); i++) {
                for (int j = i + 1; j < currentNonUniques.size(); j++) {
                    PositionListIndex pli1 = currentNonUniques.get(i);
                    PositionListIndex pli2 = currentNonUniques.get(j);

                    // Combine attributes of pli1 and pli2
                    AttributeList combinedAttributes = pli1.getAttributes().union(pli2.getAttributes());

                    // Skip if this combination already exists
                    if (existingCombinations.contains(combinedAttributes)) continue;
                    existingCombinations.add(combinedAttributes);

                    // Check if combinedAttributes has the correct size
                    if (combinedAttributes.size() != level + 1) continue;

                    // Check if all subsets of combinedAttributes are unique
                    boolean uniqueSubsets = true;
                    for (AttributeList subset : getSubsets(combinedAttributes)) {
                        if (isUnique(subset, uniques)) {
                            uniqueSubsets = false;
                            break;
                        }
                    }
                    if (!uniqueSubsets) continue;

                    // Create a new PLI for the combined attributes
                    PositionListIndex combinedPLI = new PositionListIndex(combinedAttributes, combinedAttributeColumns(relation, combinedAttributes));

                    // Intersect with existing PLIs to refine uniqueness
                    for (PositionListIndex pli : currentNonUniques) {
                        if (pli.getAttributes().subsetOf(combinedAttributes)) {
                            combinedPLI = combinedPLI.intersect(pli);
                        }
                    }

                    // Check if the combined PLI is unique
                    if (combinedPLI.isUnique()) {
                        uniques.add(new UCC(relation, combinedAttributes));
                    } else {
                        nextNonUniques.add(combinedPLI);
                    }
                }
            }

            // Move to the next level of combinations
            currentNonUniques = nextNonUniques;
            level++;
        }

        return uniques;
    }

    private boolean isUnique(AttributeList attributes, List<UCC> uniques) {
        for (UCC ucc : uniques) {
            if (ucc.getAttributeList().equals(attributes)) {
                return true;
            }
        }
        return false;
    }

    private List<AttributeList> getSubsets(AttributeList attributeList) {
        List<AttributeList> subsets = new ArrayList<>();
        int[] attributes = attributeList.getAttributes();
        generateSubsets(attributes, 0, new IntArrayList(), subsets);
        return subsets;
    }

    private void generateSubsets(int[] attributes, int index, IntArrayList current, List<AttributeList> subsets) {
        if (index == attributes.length) {
            if (!current.isEmpty()) {
                subsets.add(new AttributeList(current.toArray(new int[0])));
            }
            return;
        }

        // Include the current attribute
        current.add(attributes[index]);
        generateSubsets(attributes, index + 1, current, subsets);

        // Exclude the current attribute
        current.removeInt(current.size() - 1);
        generateSubsets(attributes, index + 1, current, subsets);
    }

    private String[] combinedAttributeColumns(Relation relation, AttributeList combinedAttributes) {
        int[] attributes = combinedAttributes.getAttributes();
        int numRows = relation.getColumns()[0].length;
        String[] combinedColumns = new String[numRows];

        // Combine columns based on combinedAttributes
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            StringBuilder combinedValue = new StringBuilder();
            for (int attribute : attributes) {
                combinedValue.append(relation.getColumns()[attribute][rowIndex]).append(",");
            }
            combinedColumns[rowIndex] = combinedValue.substring(0, combinedValue.length() - 1);
        }

        return combinedColumns;
    }
}
