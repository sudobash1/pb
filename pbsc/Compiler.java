package pbsc;

import java.io.*;
import java.lang.*;

public class Compiler {

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

    }

}
