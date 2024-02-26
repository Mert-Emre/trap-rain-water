// This class takes a map, and it calculates the water stored between its walls.
public class WaterCalculator {
    private GameMap map;

    public WaterCalculator(GameMap map) {
        this.map = map;
    }

    // This method calculates minimum water level(for a square if its neighbours have water over themselves, their wall
    // level is calculated like wall level + water height. For a square if its neighbour has water, it is also
    // like a wall for current square because some wall around neighbour, covers neighbour square, these walls will also
    // cover current square. If they don't, levels are recalculated).
    private int findMinimum(int... numbers) {
        int lowestWall = Integer.MAX_VALUE;
        for (int num : numbers) {
            if (num < lowestWall && num != -1) {
                lowestWall = num;
            }
        }
        return lowestWall;
    }

    // This method calculates water levels and returns a map that stores these water levels.
    // Water level and water height is not the same in this context.
    // Water levels are calculated like that: Height that water reaches - wall height.
    // This method traverses a rectangle that is 1 square in thickness and has a whole in it. We start from the outer rim,
    // and proceed into smaller rims. For each traverse we decrease the size of edges by 2(1 from left and 1 from right,
    // 1 from top and 1 from bottom).
    // Calculation is like that:
    // 1: If map contains 2 or fewer rows or columns, this map can't store water. So return a map with zeros.
    // 2: Put the most outer walls into tempHeights as they are. Because they will not store water.
    // 3: Move into inner frame, for upper edge of the frame start from left  and continue to the right.
    // For each square look to the water heights of neighbours. If the minimum value of water height is higher than the
    // current wall then current square will store water, and store minWater height in the tempHeights, in the end when we
    // subtract wall heights from every entry we will find water levels. If minWater height and current wall are the same
    // or current wall is higher than the minimum water height of neighbours, store current wall in the tempHeights.
    // For left and right edges traverse from top to bottom and for the bottom edge traverse from left to right.
    // 4: We will traverse a frame until we don't change any entry in a traverse of the frame.
    // 5: We move to an inner frame. This goes on and on until we can't move into a smaller frame.
    // 6: In any frames if there is water in the current square and the water heights on the diagonal neighbours are
    // different, and they also store water this means that from the diagonals a leak will occur. To fix it we set the
    // current water height to minWater height of the neighbours and leave the current frame and
    // move into the outer frame and recalculate its water heights. By doing this we also take diagonal leaks into account.
    // 7: After calculating water heights for every square, subtract wall heights from calculated water heights and this
    // will give water level for current square.
    public GameMap calculateWaterLevels() {
        int[][] heights = map.getHeights();
        int[][] tempHeights = new int[heights.length][heights[0].length];
        String[][] tempNames = new String[heights.length][heights[0].length];
        // Stage 2
        for (int row = 0; row < heights.length; row++) {
            for (int column = 0; column < heights[0].length; column++) {
                tempNames[row][column] = "";
                if (row == 0 || row == heights.length - 1 || column == 0 || column == heights[0].length - 1) {
                    tempHeights[row][column] = heights[row][column];
                } else {
                    tempHeights[row][column] = -1;
                }
            }
        }
        // Stage 1
        if (heights.length <= 2 || heights[0].length <= 2) {
            return new GameMap(tempHeights);
        }
        int leftBoundary = 1;
        int rightBoundary = heights[0].length - 2;
        int upperBoundary = 1;
        int lowerBoundary = heights.length - 2;
        // Stage 3 and 4
        while (leftBoundary <= rightBoundary && upperBoundary <= lowerBoundary) {
            boolean hasChanged = false;
            boolean goOuter = false;
            // For upper edge of the frame(all edges are calculated similarly, just some indexes differ.)
            for (int i = leftBoundary; i <= rightBoundary; i++) {
                // Initialize neighbour water levels. As I mentioned earlier, for a square if there is water in the
                // neighbour square it is also like a wall to the current square.
                int leftWall = -1;
                int rightWall = -1;
                int upperWall = -1;
                int lowerWall = -1;
                int rightUpperWall = -1;
                int leftUpperWall = -1;
                int rightLowerWall = -1;
                int leftLowerWall = -1;

                leftWall = tempHeights[upperBoundary][i - 1];
                upperWall = tempHeights[upperBoundary - 1][i];
                rightUpperWall = tempHeights[upperBoundary - 1][i + 1];
                leftUpperWall = tempHeights[upperBoundary - 1][i - 1];

                for (int k = upperBoundary + 1; k < heights.length; k++) {
                    if (tempHeights[k][i] == -1) {
                        continue;
                    }
                    lowerWall = tempHeights[k][i];
                    break;
                }
                for (int k = i + 1; k < heights[0].length; k++) {
                    if (tempHeights[upperBoundary][k] == -1) {
                        continue;
                    }
                    rightWall = tempHeights[upperBoundary][k];
                    break;
                }

                rightLowerWall = tempHeights[upperBoundary + 1][i + 1];
                leftLowerWall = tempHeights[upperBoundary + 1][i - 1];

                // Find the minimum water levels in the neighbours
                int minWall = findMinimum(rightWall, leftWall, upperWall, lowerWall, rightUpperWall, leftUpperWall, rightLowerWall, leftLowerWall);
                //Stage 6
                if (leftUpperWall != rightUpperWall && tempHeights[upperBoundary - 1][i - 1] - heights[upperBoundary - 1][i - 1] > 0 &&
                        tempHeights[upperBoundary - 1][i + 1] - heights[upperBoundary - 1][i + 1] > 0 &&
                        lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                    goOuter = true;
                }
                if (leftLowerWall != rightLowerWall && tempHeights[upperBoundary + 1][i - 1] - heights[upperBoundary + 1][i - 1] > 0 &&
                        tempHeights[upperBoundary + 1][i + 1] - heights[upperBoundary + 1][i + 1] > 0 &&
                        lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                    goOuter = true;
                }
                // Stage 3
                int currentLevel = tempHeights[upperBoundary][i];
                int currentWall = heights[upperBoundary][i];
                if (currentLevel == -1) {
                    currentLevel = currentWall < minWall ? minWall : currentWall;
                    tempHeights[upperBoundary][i] = currentLevel;
                    hasChanged = true;
                } else if (currentLevel != minWall) {
                    if (currentWall <= minWall) {
                        currentLevel = minWall;
                        tempHeights[upperBoundary][i] = currentLevel;
                        hasChanged = true;
                    } else {
                        if (currentWall != currentLevel) {
                            currentLevel = currentWall;
                            tempHeights[upperBoundary][i] = currentLevel;
                            hasChanged = true;
                        }
                    }
                } // Stage 5
                if (goOuter) {
                    break;
                }
            }
            if (goOuter) {
                rightBoundary++;
                upperBoundary--;
                lowerBoundary++;
                leftBoundary--;
                continue;
            }
            // For left and right edges of the frame
            for (int i = upperBoundary + 1; i < lowerBoundary; i++) {
                for (int j = 0; j <= 1; j++) {
                    int boundary = j == 0 ? leftBoundary : rightBoundary;
                    int leftWall = -1;
                    int rightWall = -1;
                    int upperWall = -1;
                    int lowerWall = -1;
                    int rightUpperWall = -1;
                    int leftUpperWall = -1;
                    int rightLowerWall = -1;
                    int leftLowerWall = -1;

                    leftWall = tempHeights[i][boundary - 1];
                    upperWall = tempHeights[i - 1][boundary];
                    rightUpperWall = tempHeights[i][boundary + 1];
                    leftUpperWall = tempHeights[i][boundary - 1];

                    for (int k = i + 1; k < heights.length; k++) {
                        if (tempHeights[k][boundary] == -1) {
                            continue;
                        }
                        lowerWall = tempHeights[k][boundary];
                        break;
                    }
                    for (int k = boundary + 1; k < heights[0].length; k++) {
                        if (tempHeights[i][k] == -1) {
                            continue;
                        }
                        rightWall = tempHeights[i][k];
                        break;
                    }
                    rightLowerWall = tempHeights[i + 1][boundary + 1];
                    leftLowerWall = tempHeights[i + 1][boundary - 1];
                    int minWall = findMinimum(rightWall, leftWall, upperWall, lowerWall, rightUpperWall, leftUpperWall, rightLowerWall, leftLowerWall);
                    if (leftUpperWall != rightUpperWall && tempHeights[i - 1][boundary - 1] - heights[i - 1][boundary - 1] > 0 && tempHeights[i - 1][boundary + 1] - heights[i - 1][boundary + 1] > 0 && lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                        goOuter = true;
                    }
                    if (leftLowerWall != rightLowerWall && tempHeights[i + 1][boundary - 1] - heights[i + 1][boundary - 1] > 0 && tempHeights[i + 1][boundary + 1] - heights[i + 1][boundary + 1] > 0 && lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                        goOuter = true;
                    }

                    int currentLevel = tempHeights[i][boundary];
                    int currentWall = heights[i][boundary];
                    if (currentLevel == -1) {
                        currentLevel = currentWall < minWall ? minWall : currentWall;
                        tempHeights[i][boundary] = currentLevel;
                        hasChanged = true;
                    } else if (currentLevel != minWall) {
                        if (currentWall <= minWall) {
                            currentLevel = minWall;
                            tempHeights[i][boundary] = currentLevel;
                            hasChanged = true;
                        } else {
                            if (currentWall != currentLevel) {
                                currentLevel = currentWall;
                                tempHeights[i][boundary] = currentLevel;
                                hasChanged = true;
                            }
                        }
                    }
                    if (goOuter) {
                        break;
                    }
                }
                if (goOuter) {
                    break;
                }
            }

            if (goOuter) {
                rightBoundary++;
                upperBoundary--;
                lowerBoundary++;
                leftBoundary--;
                continue;
            }
            // For bottom edge of the frame
            for (int i = leftBoundary; i <= rightBoundary; i++) {
                int leftWall = -1;
                int rightWall = -1;
                int upperWall = -1;
                int lowerWall = -1;
                int rightUpperWall = -1;
                int leftUpperWall = -1;
                int rightLowerWall = -1;
                int leftLowerWall = -1;

                leftWall = tempHeights[lowerBoundary][i - 1];
                upperWall = tempHeights[lowerBoundary - 1][i];
                rightUpperWall = tempHeights[lowerBoundary - 1][i + 1];
                leftUpperWall = tempHeights[lowerBoundary - 1][i - 1];

                for (int k = lowerBoundary - 1; k >= 0; k--) {
                    if (tempHeights[k][i] == -1) {
                        continue;
                    }
                    upperWall = tempHeights[k][i];
                    break;
                }
                for (int k = i + 1; k < heights[0].length; k++) {
                    if (tempHeights[lowerBoundary][k] == -1) {
                        continue;
                    }
                    rightWall = tempHeights[lowerBoundary][k];
                    break;
                }
                rightLowerWall = tempHeights[lowerBoundary + 1][i + 1];
                leftLowerWall = tempHeights[lowerBoundary + 1][i - 1];
                int minWall = findMinimum(rightWall, leftWall, upperWall, lowerWall, rightUpperWall, leftUpperWall, rightLowerWall, leftLowerWall);
                if (leftUpperWall != rightUpperWall && tempHeights[lowerBoundary - 1][i - 1] - heights[lowerBoundary - 1][i - 1] > 0 && tempHeights[lowerBoundary - 1][i + 1] - heights[lowerBoundary - 1][i + 1] > 0 && lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                    goOuter = true;
                }
                if (leftLowerWall != rightLowerWall && tempHeights[lowerBoundary + 1][i - 1] - heights[lowerBoundary + 1][i - 1] > 0 && tempHeights[lowerBoundary + 1][i + 1] - heights[lowerBoundary + 1][i + 1] > 0 && lowerBoundary - upperBoundary > 1 && rightBoundary - leftBoundary > 1) {
                    goOuter = true;
                }

                int currentLevel = tempHeights[lowerBoundary][i];
                int currentWall = heights[lowerBoundary][i];
                if (currentLevel == -1) {
                    currentLevel = currentWall < minWall ? minWall : currentWall;
                    tempHeights[lowerBoundary][i] = currentLevel;
                    hasChanged = true;
                } else if (currentLevel != minWall) {
                    if (currentWall <= minWall) {
                        currentLevel = minWall;
                        tempHeights[lowerBoundary][i] = currentLevel;
                        hasChanged = true;
                    } else {
                        if (currentWall != currentLevel) {
                            currentLevel = currentWall;
                            tempHeights[lowerBoundary][i] = currentLevel;
                            hasChanged = true;
                        }
                    }
                }
                if (goOuter) {
                    break;
                }
            }
            // Stage 6
            if (goOuter) {
                rightBoundary++;
                upperBoundary--;
                lowerBoundary++;
                leftBoundary--;
                continue;
            }
            // Stage 5
            if (!hasChanged) {
                upperBoundary++;
                lowerBoundary--;
                leftBoundary++;
                rightBoundary--;
            }
        }
        // Stage 7
        for (int i = 0; i < tempHeights.length; i++) {
            for (int j = 0; j < tempHeights[0].length; j++) {
                tempHeights[i][j] -= heights[i][j];
            }
        }

        return new GameMap(tempHeights);
    }
}
