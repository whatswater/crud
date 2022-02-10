package com.whatswater.gen;


import java.util.Arrays;

public class CodePrinter {
    private StringBuilder code;
    private int indent;

    public CodePrinter() {
        this.code = new StringBuilder();
        this.indent = 0;
    }

    public CodePrinter newBlock() {
        code.append("{");
        newLine();
        addIndent();
        return this;
    }

    public CodePrinter exitBlock() {
        newLine();
        subIndent();
        code.append("}");
        return this;
    }

    // back时，不能跨行back
    public CodePrinter back() {
        return back(1);
    }

    public CodePrinter back(int n) {
        int idx = code.lastIndexOf("\n");
        int maxBackLen = code.length() - idx - 1;
        if (n > maxBackLen) {
            throw new IllegalArgumentException("back to long");
        }
        code.setLength(Math.max(code.length() - n, 0));
        return this;
    }

    public CodePrinter addIndent() {
        indent = indent + 1;
        return this;
    }

    public CodePrinter subIndent() {
        indent = indent - 1;
        return this;
    }

    public CodePrinter newLine() {
        return newLine(1);
    }

    public CodePrinter newLine(int n) {
        for (int i = 0; i < n; i++) {
            int idx = code.lastIndexOf("\n") + 1;
            code.insert(idx, generateBlank(indent));
            code.append("\n");
        }
        return this;
    }

    public StringBuilder getCode() {
        return code;
    }

    public CodePrinter print(String str) {
        String[] lines = str.split("\\r?\\n");
        if (lines.length == 1) {
            code.append(str);
            return this;
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            code.append(line);
            if (i != lines.length - 1) {
                newLine();
            }
        }
        return this;
    }

    public static String generateBlank(int indent) {
        char[] blank = new char[indent * 4];
        Arrays.fill(blank, ' ');
        return String.valueOf(blank);
    }
}
