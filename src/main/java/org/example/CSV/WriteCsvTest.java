package org.example.CSV;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteCsvTest {

    public static void main(String[] args) {
        writeCSV("/Users/m1/Downloads/ml-latest-small/movies_test.csv"
                , "999996,Comedy|Drama");
    }

    public static void writeCSV(String csvFilePath, String addData) {
        File file = new File(csvFilePath);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.newLine();
            bw.write(addData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
