package core.utils;
public class CsvParser {

    public static int[] readCSVships(int size) {
        int [] res = new int [4];

        try {
            java.io.BufferedReader FileReader=
                    new java.io.BufferedReader(
                            new java.io.FileReader(
                                    new java.io.File("assets/ship_table.csv")
                            )
                    );

            String zeile;

            while(null!=(zeile=FileReader.readLine())){
                String[] split=zeile.split(";");

                if (split[4].equals(size+"")){
                    for(int i=0; i<4; i++){
                        res[i]= Integer.parseInt(split[i]);
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