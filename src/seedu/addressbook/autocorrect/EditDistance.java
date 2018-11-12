//@@author ShreyasKp
package seedu.addressbook.autocorrect;

/**
 * Returns the edit distance needed to convert one string to the other.
 */
class EditDistance {

    /**
     * If returns 0, the strings are same.
     * If returns 1, that means either a character is added, removed or replaced and so on.
     *
     * @param inputString The string input by the user.
     * @param storedString The string contained in the Dictionary.
     * @return The minimum number of operations required to transform inputString into storedString.
     */
    static int computeDistance(String inputString, String storedString) {

        int originalLength = inputString.length();
        int finalLength = storedString.length();

        //Solution below adapted from https://www.programcreek.com/2013/12/edit-distance-in-java/
        // len1+1, len2+1, because finally return distance[len1][len2]
        int[][] distance = new int[originalLength + 1][finalLength + 1];

        for (int i = 0; i <= originalLength; i++) {
            distance[i][0] = i;
        }

        for (int j = 0; j <= finalLength; j++) {
            distance[0][j] = j;
        }

        //iterate through, and check last char
        for (int i = 0; i < originalLength; i++) {
            char c1 = inputString.charAt(i);
            for (int j = 0; j < finalLength; j++) {
                char c2 = storedString.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update distance value for +1 length
                    distance[i + 1][j + 1] = distance[i][j];
                } else {
                    int replace = distance[i][j] + 1;
                    int insert = distance[i][j + 1] + 1;
                    int delete = distance[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    distance[i + 1][j + 1] = min;
                }
            }
        }

        return distance[originalLength][finalLength];
    }
}
