import java.io.*;
import java.util.*;

public class Parser {

    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;

    private List<String> commands;
    private int index = -1;
    private String current;

    public Parser(File file) throws IOException {
        commands = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("//.*", "").trim();
            if (!line.isEmpty()) commands.add(line);
        }
        br.close();
    }

    public boolean hasMoreCommands() {
        return index + 1 < commands.size();
    }

    public void advance() {
        current = commands.get(++index);
    }

    public int commandType() {
        if (current.startsWith("push")) return C_PUSH;
        if (current.startsWith("pop")) return C_POP;
        if (current.startsWith("label")) return C_LABEL;
        if (current.startsWith("goto")) return C_GOTO;
        if (current.startsWith("if-goto")) return C_IF;
        if (current.startsWith("function")) return C_FUNCTION;
        if (current.startsWith("call")) return C_CALL;
        if (current.startsWith("return")) return C_RETURN;
        return C_ARITHMETIC;
    }

    public String arg1() {
        if (commandType() == C_ARITHMETIC) return current;
        return current.split(" ")[1];
    }

    public int arg2() {
        return Integer.parseInt(current.split(" ")[2]);
    }
}
