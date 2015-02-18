package pbsc;

import java.io.*;
import java.lang.*;

public class Compiler {

    //This magicNumber is not found anywhere in the program input.
    //It is used to make labels for compiler use that will not interfere.
    private static int magicNumber;


    public static String readFile(String path) {

        File file = new File(path);
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String input;

        try {
            reader = new BufferedReader(new FileReader(file));

            while ((input = reader.readLine()) != null) {
                //newlines are not needed.
                sb.append(input);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file `" + path + "'.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Cannot open file `" + path + "' for reading.");
            System.exit(1);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {}
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String input;

        if (args.length < 1) {
            System.err.println("Requires more arguments.");
        }

        input = readFile(args[0]);

        do {
            magicNumber = (int)(Math.random() * 100000);
        } while ( input.contains((String) magicNumber) );

    }

}
