// This class is just used for crating the instances and calling print functions. Detailed explanation of each class is
// given in appropriate places.
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        GameMap map = new GameMap("input9.txt");
        map.printMap(false);
        map.makeModifications();
        WaterCalculator waterCalculator = new WaterCalculator(map);
        GameMap heightsMap = waterCalculator.calculateWaterLevels();
        NameCalculator nameCalculator = new NameCalculator(heightsMap);
        GameMap namesMap = nameCalculator.calculateNames();
        GameMap finalMap = GameMap.finalMap(heightsMap, namesMap, map);
        finalMap.printMap(true);
        finalMap.printScore();

    }
}