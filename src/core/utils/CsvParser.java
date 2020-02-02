package core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CsvParser {

    public static int[] readCSVships(int size) {
        int[] res = new int[4];

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(CsvParser.class.getResourceAsStream("/ship_table.csv")));
            String zeile;

            while (null != (zeile = reader.readLine())) {
                String[] split = zeile.split(";");

                if (split[4].equals(size + "")) {
                    for (int i = 0; i < 4; i++) {
                        res[i] = Integer.parseInt(split[i]);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return res;
    }
}