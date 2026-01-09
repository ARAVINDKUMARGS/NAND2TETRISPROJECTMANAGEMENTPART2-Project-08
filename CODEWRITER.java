import java.io.*;

public class CodeWriter {

    private BufferedWriter bw;
    private String fileName;
    private int labelCount = 0;
    private String currentFunction = "";

    public CodeWriter(String output) throws IOException {
        bw = new BufferedWriter(new FileWriter(output));
    }

    public void setFileName(String name) {
        fileName = name.replace(".vm", "");
    }

    private void write(String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

    // ---------- BOOTSTRAP ----------
    public void writeInit() throws IOException {
        write("@256");
        write("D=A");
        write("@SP");
        write("M=D");
        writeCall("Sys.init", 0);
    }

    // ---------- ARITHMETIC ----------
    public void writeArithmetic(String cmd) throws IOException {
        if (cmd.equals("neg") || cmd.equals("not")) {
            write("@SP"); write("A=M-1");
            write(cmd.equals("neg") ? "M=-M" : "M=!M");
            return;
        }

        write("@SP"); write("AM=M-1"); write("D=M");
        write("A=A-1");

        switch (cmd) {
            case "add": write("M=M+D"); break;
            case "sub": write("M=M-D"); break;
            case "and": write("M=M&D"); break;
            case "or":  write("M=M|D"); break;
            case "eq": case "gt": case "lt":
                String labelTrue = "TRUE" + labelCount;
                String labelEnd = "END" + labelCount++;
                write("D=M-D");
                write("@" + labelTrue);
                write(cmd.equals("eq") ? "D;JEQ" : cmd.equals("gt") ? "D;JGT" : "D;JLT");
                write("@SP"); write("A=M-1"); write("M=0");
                write("@" + labelEnd); write("0;JMP");
                write("(" + labelTrue + ")");
                write("@SP"); write("A=M-1"); write("M=-1");
                write("(" + labelEnd + ")");
        }
    }

    // ---------- PUSH / POP ----------
    public void writePushPop(int type, String segment, int index) throws IOException {
        if (type == Parser.C_PUSH) {
            if (segment.equals("constant")) {
                write("@" + index); write("D=A");
            } else {
                String base = segmentBase(segment);
                write("@" + base);
                write("D=M");
                write("@" + index);
                write("A=D+A");
                write("D=M");
            }
            write("@SP"); write("A=M"); write("M=D");
            write("@SP"); write("M=M+1");
        } else {
            String base = segmentBase(segment);
            write("@" + base); write("D=M");
            write("@" + index); write("D=D+A");
            write("@R13"); write("M=D");
            write("@SP"); write("AM=M-1"); write("D=M");
            write("@R13"); write("A=M"); write("M=D");
        }
    }

    private String segmentBase(String segment) {
        switch (segment) {
            case "local": return "LCL";
            case "argument": return "ARG";
            case "this": return "THIS";
            case "that": return "THAT";
            case "temp": return "5";
            case "pointer": return "3";
            case "static": return fileName;
        }
        return "";
    }

    // ---------- PROGRAM CONTROL ----------
    public void writeLabel(String label) throws IOException {
        write("(" + currentFunction + "$" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        write("@" + currentFunction + "$" + label);
        write("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        write("@SP"); write("AM=M-1"); write("D=M");
        write("@" + currentFunction + "$" + label);
        write("D;JNE");
    }

    // ---------- FUNCTIONS ----------
    public void writeFunction(String name, int nVars) throws IOException {
        currentFunction = name;
        write("(" + name + ")");
        for (int i = 0; i < nVars; i++) {
            write("@0"); write("D=A");
            write("@SP"); write("A=M"); write("M=D");
            write("@SP"); write("M=M+1");
        }
    }

    public void writeCall(String name, int nArgs) throws IOException {
        String ret = "RET" + labelCount++;

        write("@" + ret); write("D=A");
        pushD();

        pushSegment("LCL");
        pushSegment("ARG");
        pushSegment("THIS");
        pushSegment("THAT");

        write("@SP"); write("D=M");
        write("@" + (nArgs + 5)); write("D=D-A");
        write("@ARG"); write("M=D");

        write("@SP"); write("D=M");
        write("@LCL"); write("M=D");

        write("@" + name); write("0;JMP");
        write("(" + ret + ")");
    }

    public void writeReturn() throws IOException {
        write("@LCL"); write("D=M"); write("@R13"); write("M=D");
        write("@5"); write("A=D-A"); write("D=M"); write("@R14"); write("M=D");

        write("@SP"); write("AM=M-1"); write("D=M");
        write("@ARG"); write("A=M"); write("M=D");

        write("@ARG"); write("D=M+1"); write("@SP"); write("M=D");

        restore("THAT", 1);
        restore("THIS", 2);
        restore("ARG", 3);
        restore("LCL", 4);

        write("@R14"); write("A=M"); write("0;JMP");
    }

    private void restore(String seg, int i) throws IOException {
        write("@R13"); write("D=M");
        write("@" + i); write("A=D-A");
        write("D=M");
        write("@" + seg); write("M=D");
    }

    private void pushSegment(String seg) throws IOException {
        write("@" + seg); write("D=M");
        pushD();
    }

    private void pushD() throws IOException {
        write("@SP"); write("A=M"); write("M=D");
        write("@SP"); write("M=M+1");
    }

    public void close() throws IOException {
        bw.close();
    }
}
