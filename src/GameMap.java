// This class reads the matrix from input file and creates the map array accordingly. It has three constructors.
// One for reading from text, one for int[][] arrays and one for String[][]. The last one is used for keeping names of
// the lakes and creating final array. This class also handles calculating the score, printing the map, and changing
// the terrain with user input.

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class GameMap {
    private int rowNumber;
    private int columnNumber;
    private boolean heightMap;
    private int[][] heights;
    private String[][] names;
    private double score;

    // Reads the input file and creates an array which holds the heights of the walls.
    public GameMap(String input) throws FileNotFoundException {
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("File doesn't exist");
            System.exit(1);
        }
        Scanner scanner = new Scanner(file);
        if (scanner.hasNextLine()) {
            try {
                columnNumber = scanner.nextInt();
                rowNumber = scanner.nextInt();
            } catch (Exception ex) {
                System.out.println("Wrong format for column or row number");
                System.exit(1);
            }

            scanner.nextLine();
        }
        heights = new int[rowNumber][columnNumber];
        int rowCounter = 0;
        while (rowCounter < rowNumber && scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(" ");
            for (int i = 0; i < line.length; i++) {
                try {
                    heights[rowCounter][i] = Integer.parseInt(line[i]);
                    // If there is a number less than zero than it is an error.
                    if (Integer.parseInt(line[i]) < 0) {
                        System.out.println("Wrong format for wall height");
                        System.exit(1);
                    }
                } catch (Exception ex) {
                    // If an entry can't be turned into an integer than it is an error.
                    System.out.println("Wrong format for wall height");
                    System.exit(1);
                }
            }
            rowCounter++;
        }
        scanner.close();
        heightMap = true;
        score = 0;
    }

    // This constructor is used for storing water levels in the matrix. It is used by WaterCalculator class.
    public GameMap(int[][] map) {
        this.heights = map;
        rowNumber = heights.length;
        columnNumber = heights[0].length;
        heightMap = true;
        score = 0;
    }

    // This class is used for storing the lake names. It is used by NameCalculator class.
    public GameMap(String[][] map) {
        names = map;
        rowNumber = names.length;
        columnNumber = names[0].length;
        heightMap = false;
        score = 0;
    }

    // This class is used for creating the final version of matrix with numbers and lake names. It is used by finalMap
    // method of this class.
    public GameMap(String[][] map, double score) {
        this(map);
        this.score = score;
    }

    public int[][] getHeights() {
        return heights;
    }

    public String[][] getNames() {
        return names;
    }

    // This method prints the map, and it takes if the instance holds names or heights into account. Boolean parameter
    // is used for missing space in the first row of output.
    public void printMap(boolean space) {
        // If the instance holds heights than traverse the heights array and print the numbers. If a number is greater
        // than 10, leave less space to prevent shifting to the right.
        if (heightMap) {
            for (int row = 0; row < rowNumber; row++) {
                if (row < 10) {
                    System.out.print("  " + row + " ");
                } else {
                    System.out.print(" " + row + " ");
                }
                for (int column = 0; column < columnNumber; column++) {
                    if (heights[row][column] < 10) {
                        System.out.print(" ");
                    }
                    if (row == 0 && column == columnNumber - 1 && !space) {
                        System.out.print(heights[row][column] + "");
                    } else {
                        System.out.print(heights[row][column] + " ");
                    }
                }
                System.out.println();
            }

            String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            System.out.print("    ");
            // Prints the last row of the map like a chessboard (a,b,c,d...).
            for (int column = 0; column < columnNumber; column++) {
                if (column < 26) {
                    System.out.print(" " + letters[column] + " ");
                } else {
                    System.out.print(letters[column / 26 - 1] + letters[column % 26] + " ");
                }
            }
            System.out.println();
        } else {
            // If the instance holds names than traverse the names array and print the names. This part will also print
            // the final map which holds both names of the lakes and heights of the wall.
            for (int row = 0; row < rowNumber; row++) {
                if (row < 10) {
                    System.out.print("  " + row + " ");
                } else {
                    System.out.print(" " + row + " ");
                }
                for (int column = 0; column < columnNumber; column++) {
                    if (names[row][column].length() < 2) {
                        System.out.print(" ");
                    }
                    System.out.print(names[row][column] + " ");
                }
                System.out.println();
            }

            String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            System.out.print("    ");
            for (int column = 0; column < columnNumber; column++) {
                if (column < 26) {
                    System.out.print(" " + letters[column] + " ");
                } else {
                    System.out.print(letters[column / 26 - 1] + letters[column % 26] + " ");
                }
            }
            System.out.println();
        }

    }

    // Prints the score with 2 decimal places.
    public void printScore() {
        System.out.printf("Final score: %.2f", score);
    }

    // This method is used for increasing the height of a wall with user input. User should make 10 successfull
    // modifications. It checks the input for wrong formats.
    public void makeModifications() {
        int successfullModifications = 0;
        Scanner scanner = new Scanner(System.in);
        while (successfullModifications < 10) {

            System.out.print("Add stone " + (successfullModifications + 1) + " / 10 to coordinate:");
            String input = scanner.nextLine();
            if (input.length() < 2 || input.length() > 5) {
                System.out.println("Not a valid step!");
                continue;
            }
            boolean problematic = false;
            int numberOfDigits = 0;
            // If an input contains a symbol or space, it is problematic.
            // If an input doesn't contain a number, it is problematic.
            // If an input contains an input at zeroth index, it is problematic.
            // If an input contains a letter after first index, it is problematic.
            // If an input contains more than three digits, it is problematic.
            // If number parts is bigger than row number, it is problematic.
            // If string part is not in the map, it is problematic.
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isLetterOrDigit(input.charAt(i))) {
                    problematic = true;
                    break;
                }
                if (i > 1 && !Character.isDigit(input.charAt(i))) {
                    problematic = true;
                    break;
                }
                if (i == 0 && !Character.isLetter(input.charAt(i))) {
                    problematic = true;
                    break;
                }
                if (Character.isDigit(input.charAt(i))) {
                    numberOfDigits++;
                }
            }

            if (numberOfDigits > 3 || numberOfDigits == 0) {
                problematic = true;
            }
            if (problematic) {
                System.out.println("Not a valid step!");
                continue;
            }
            String column = "";
            int row = 0;
            column += input.charAt(0);
            if (Character.isLetter(input.charAt(1))) {
                column += input.charAt(1);
                row = Integer.parseInt(input.substring(2));
            } else {
                row = Integer.parseInt(input.substring(1));
            }

            if (row >= heights.length) {
                problematic = true;
            }
            int columnToNumber = 0;
            if (column.length() == 1) {
                int num = column.charAt(0);
                columnToNumber = num - 97;
            }
            if (column.length() == 2) {
                int firstNum = column.charAt(0) - 97;
                int secondNum = column.charAt(1) - 97;
                columnToNumber = (firstNum + 1) * 26 + secondNum;
            }

            if (columnToNumber >= heights[0].length) {
                problematic = true;
            }
            if (problematic) {
                System.out.println("Not a valid step!");
                continue;
            }
            successfullModifications++;
            heights[row][columnToNumber]++;
            printMap(true);
            System.out.println("---------------");
        }
        scanner.close();
    }

    // This method gets three maps. First one stores calculated water levels, the second one stores the names of
    // the lakes, and the last one stores wall heights. By using these maps if there is no water over a wall, then its
    // wall height is put into the final map but if there is water, its lake name is put into the final map.
    public static GameMap finalMap(GameMap mapOfHeights, GameMap mapOfNames, GameMap firstMap) {
        if(mapOfHeights.getHeights().length<=2 && mapOfHeights.getHeights()[0].length <=2){
            return new GameMap(mapOfHeights.getHeights());
        }
        String[][] tempMap = new String[firstMap.rowNumber][firstMap.columnNumber];
        int[] volumes = new int[27 * 26];
        for (int i = 0; i < mapOfHeights.getHeights().length; i++) {
            for (int j = 0; j < mapOfHeights.getHeights()[0].length; j++) {
                if (mapOfHeights.getHeights()[i][j] == 0) {
                    tempMap[i][j] = Integer.toString(firstMap.getHeights()[i][j]);
                } else {
                    String currentName = mapOfNames.getNames()[i][j];
                    tempMap[i][j] = currentName;
                    if (currentName.length() == 1) {
                        int position = currentName.charAt(0) - 65;
                        volumes[position] += mapOfHeights.getHeights()[i][j];
                    } else if (currentName.length() == 2) {
                        int position = (currentName.charAt(0) - 64) * 26 + currentName.charAt(1) - 65;
                        volumes[position] += mapOfHeights.getHeights()[i][j];
                    }
                }
            }
        }
        double score = 0;
        for (int volume : volumes) {
            if (volume > 0) {
                score += Math.sqrt(volume);
            }
        }
        return new GameMap(tempMap, score);
    }
}