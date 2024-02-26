// This class is used for calculating the possible smallest(lexicographically) lake name. Since it will not be initiated,
// it is static.
public abstract class NameCreator {
    private static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static int[] nameCounts = new int[27 * 26];

    // This method returns the first possible lake name. This class is used when there is no lake around a square.
    public static String setLakeName() {
        String name = "";
        for (int i = 0; i < 27 * 26; i++) {
            if (nameCounts[i] != 0) {
                continue;
            }
            if (i < 26) {
                name = alphabet[i];
            } else {
                name = alphabet[i / 26 - 1] + alphabet[i % 26];
            }
            nameCounts[i] = 1;
            break;
        }
        return name;
    }

    // Since this class tracks the total area(not volume) of a lake, if there is a neighbour lake around a square,
    // we need to increase the number of this lake because we will set this name to the current square.
    public static void increaseCount(String name) {
        if (name.length() == 1) {
            for (int i = 0; i < 26; i++) {
                if (name.equals(alphabet[i])) {
                    nameCounts[i]++;
                    break;
                }
            }
        } else if (name.length() == 2) {
            int firstIndex = name.charAt(0) - 65;
            int secondIndex = name.charAt(1) - 65;
            int realIndex = (firstIndex + 1) * 26 + secondIndex;
            nameCounts[realIndex]++;
        }
    }

    // If a lake is changed to a smaller name than the number of this lake should be decreased to make it suitable for
    // future use.
    public static void decreaseCount(String name) {
        if (name.length() == 1) {
            for (int i = 0; i < 26; i++) {
                if (name.equals(alphabet[i])) {
                    nameCounts[i]--;
                    break;
                }
            }
        } else if (name.length() == 2) {
            int firstIndex = name.charAt(0) - 65;
            int secondIndex = name.charAt(1) - 65;
            int realIndex = (firstIndex + 1) * 26 + secondIndex;
            nameCounts[realIndex]--;
        }
    }

}
