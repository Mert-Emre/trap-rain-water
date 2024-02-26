// This class is used to name the lakes from left to right. If there is a named neighbour lake around a square, then
// this square should be named with the smallest name of the neighbours. If this is not the case, then we need to get
// first suitable name from the NameCreator class.
public class NameCalculator {
    private int[][] water;
    private String[][] names;

    // This constructor takes a map which contains water levels above the walls.
    public NameCalculator(GameMap map) {
        water = map.getHeights();
        names = new String[water.length][water[0].length];
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < names[0].length; j++) {
                names[i][j] = "";
            }
        }
    }

    // This method is used for finding the smallest(lexicographically) name from given parameters. Neighbour lake names
    // are given as parameters.
    private String findMinimumName(String... names) {
        String minimumName = "ZZZ";
        for (String name : names) {
            if (name.compareTo(minimumName) < 0 && name.compareTo("") != 0) {
                minimumName = name;
            }
        }
        if (minimumName.length() == 3) {
            minimumName = "";
        }
        return minimumName;
    }

    // This method looks to the names of the neighbours. If neighbours have a name. It chooses the smallest one among
    // them. But if there is no water around a square, it gets a suitable name from NameCreator class.
    // This method starts traversing from first row(not zeroth since there can't be a lake at zeroth row) first column,
    // and traverses up to last - 1 row and last -1 column. If there is a leak from diagonals, there may be wrongly named
    // lakes in the upper rows. If it detects such situations it turns to the upper rows and starts naming them again.
    public GameMap calculateNames() {
        int row = 1;
        if (water.length <= 2 || water[0].length <= 2) {
            return new GameMap(names);
        }
        while (row < water.length - 1) {
            boolean goUpper = false;
            boolean hasChanged = false;
            for (int i = 1; i < water[0].length - 1; i++) {
                // Find the names of the neighbours.
                String leftName = names[row][i - 1];
                String rightName = names[row][i + 1];
                String upperName = names[row - 1][i];
                String lowerName = names[row + 1][i];
                String rightUpperName = names[row - 1][i + 1];
                String leftUpperName = names[row - 1][i - 1];
                String leftLowerName = names[row + 1][i - 1];
                String rightLowerName = names[row + 1][i + 1];
                String currentName = names[row][i];
                int currentWater = water[row][i];
                int leftUpperWater = water[row - 1][i - 1];
                int rightUpperWater = water[row - 1][i + 1];
                String minName = findMinimumName(leftName, rightName, upperName, lowerName, rightUpperName, rightLowerName, leftLowerName, leftUpperName);
                // If there is water above the current wall, then square should be a lake.
                if (currentWater > 0) {
                    if (minName.equals("") && currentName.equals("")) {
                        currentName = NameCreator.setLakeName();
                        names[row][i] = currentName;
                        hasChanged = true;
                    } else if (!minName.equals("") && currentName.equals("")) {
                        currentName = minName;
                        NameCreator.increaseCount(minName);
                        names[row][i] = currentName;
                        hasChanged = true;
                    } else if (!minName.equals("") && !currentName.equals("") && minName.compareTo(currentName) < 0) {
                        NameCreator.decreaseCount(currentName);
                        currentName = minName;
                        NameCreator.increaseCount(minName);
                        names[row][i] = currentName;
                        hasChanged = true;
                    }
                }
                if (currentWater > 0 && leftUpperWater > 0 && rightUpperWater > 0 && !leftUpperName.equals(rightUpperName)) {
                    goUpper = true;
                }
            }
            if (goUpper) {
                row--;
                continue;
            }
            if (!hasChanged) {
                row++;
            }
        }
        return new GameMap(names);
    }
}
