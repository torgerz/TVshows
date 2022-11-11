package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads file with show titles.
 */

 class ReadFile {
    public static ArrayList<String> ReadF() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            File myObj = new File("src/main/resources/data/userdata.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String st = myReader.nextLine();
                String showForURL = st.replaceAll("\\s","+");
                list.add(showForURL);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return list;
    }
}
