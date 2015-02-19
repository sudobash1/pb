package pbsc;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.regex.*;
import command.*;

public class PbscCompiler {

    private ErrorMsgr m_errorMsgr;

    //This magicNumber is not found anywhere in the program input.
    //It is used to make labels for compiler use that will not interfere.
    private int m_magicNumber;

    private int m_ifCounter = 0;
    private int m_whileCounter = 0;
    private int m_forCounter = 0;

    public PbscCompiler() {
        m_errorMsgr = new ErrorMsgr();
    }

    public ErrorMsgr errorMsgr() {
        return m_errorMsgr;
    }

    public String ifID() {
        return "IF" + (m_ifCounter++) + "ID" + m_magicNumber;
    }

    public String whileID() {
        return "WHILE" + (m_whileCounter++) + "ID" + m_magicNumber;
    }

    public String forID() {
        return "WHILE" + (m_forCounter++) + "ID" + m_magicNumber;
    }

    public String id(String label) {
        return label + "ID" + m_magicNumber;
    }

    public String id(String[] namespace, String label) {
        String ns = "";
        for (int i = 0; i < namespace.length; ++i) {
            ns += namespace[i];
        }
        return ns + label + "ID" + m_magicNumber;
    }

    public static int countNewlines(String s) {
        Matcher m = Pattern.compile("(\n)").matcher(s);
        int lines = 0;
        while (m.find()) { ++lines; }
        return lines;
    }

    public String readFile(String path) {

        File file = new File(path);
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String input;

        try {
            reader = new BufferedReader(new FileReader(file));

            while ((input = reader.readLine()) != null) {
                sb.append(input + '\n');
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

    public int run(String[] args) {
        String input;

        if (args.length < 1) {
            System.err.println("Requires more arguments.");
            return 1;
        }

        input = readFile(args[0]);

        do {
            m_magicNumber = (int)(Math.random() * 100000);
        } while ( input.contains(String.valueOf(m_magicNumber)) );

        int line = 1; //What line number are we on?

        CommandFactory cmdFactory = new CommandFactory(this);

        ArrayList<String> str_commands = new ArrayList<String>(Arrays.asList(input.split(";")));
        ArrayList<Command> program = new ArrayList<Command>();

        for(String str_command: str_commands) {
            line += countNewlines(str_command);

            Command newCommand = cmdFactory.GenerateCommand(str_command, line);

            if (newCommand != null) {
                program.add(newCommand);
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new PbscCompiler().run(args));
    }

}
