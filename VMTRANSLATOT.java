import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: VMTranslator <inputfile.vm | directory>");
            return;
        }

        File input = new File(args[0]);
        ArrayList<File> vmFiles = new ArrayList<>();
        boolean isDirectory = input.isDirectory();

        if (isDirectory) {
            for (File f : input.listFiles()) {
                if (f.getName().endsWith(".vm")) {
                    vmFiles.add(f);
                }
            }
        } else {
            vmFiles.add(input);
        }

        String outputName;
        if (isDirectory) {
            outputName = input.getAbsolutePath() + "/" + input.getName() + ".asm";
        } else {
            outputName = input.getAbsolutePath().replace(".vm", ".asm");
        }

        CodeWriter codeWriter = new CodeWriter(outputName);

        if (isDirectory) {
            codeWriter.writeInit(); // Bootstrap only for directory
        }

        for (File vmFile : vmFiles) {
            Parser parser = new Parser(vmFile);
            codeWriter.setFileName(vmFile.getName());

            while (parser.hasMoreCommands()) {
                parser.advance();
                switch (parser.commandType()) {
                    case C_ARITHMETIC:
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    case C_PUSH:
                    case C_POP:
                        codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                        break;
                    case C_LABEL:
                        codeWriter.writeLabel(parser.arg1());
                        break;
                    case C_GOTO:
                        codeWriter.writeGoto(parser.arg1());
                        break;
                    case C_IF:
                        codeWriter.writeIf(parser.arg1());
                        break;
                    case C_FUNCTION:
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case C_CALL:
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                        break;
                    case C_RETURN:
                        codeWriter.writeReturn();
                        break;
                }
            }
        }

        codeWriter.close();
    }
}
