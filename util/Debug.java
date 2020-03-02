package com.smoothstack.lms.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Debug {

    private static boolean debug = false;

    static {
        debug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Debug.debug = debug;
    }

    public static void printMethodInfo() {
        if (!isDebug()) return;
        printMethodInfo(2);
    }

    public static void printMethodInfo(int i) {
        if (!isDebug()) return;
        StackTraceElement[] ste =  new Throwable().getStackTrace();

        if (i < ste.length)
            printf("Method %s ... (%s:%s) in %s.class \n",
                    ste[i].getMethodName(),
                    ste[i].getFileName(), ste[i].getLineNumber(),
                ste[i].getClassName().replace("com.smoothstack.lms.borrowermicroservice.","$.")
                    );
    }

    public static void printStackTrace() {

        if (!isDebug()) return;
        printMethodInfo(2);
        StackTraceElement[] ste =  new Throwable().getStackTrace();

        for (int i = 1; i < ste.length; i++)
            printf(" ... (%s:%d) -> '%s::%s' line %d\n",
                    ste[i].getFileName(), ste[i].getLineNumber(),
                    ste[i].getClassName()
                        .replace("com.smoothstack.lms.borrowermicroservice.","$."),
                    ste[i].getMethodName(), ste[i].getLineNumber()  );
    }

    public static void printException(Exception e) {
        if (!isDebug()) return;
        printMethodInfo(2);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        System.err.println(sw.toString());
    }

    public static void printExceptionAndThrow(Exception e) throws Exception {
        if (!isDebug()) return;
        printMethodInfo(2);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        System.err.println(sw.toString());

        throw(e);
    }

    public static void println(String s) {

        printf("%s\n", s);

    }

    public static void print(String s) {
        printf(s);
    }

    public static void printf(String format, Object... objects) {
        if (!isDebug()) return;

        String message = wordWrap(String.format(format, objects), 120);
        System.err.print(message.replaceAll("^"," * "));
    }

    public static <R> R tee(R obj) {
        println(obj.toString());
        return obj;
    }
    private static String wordWrap(String text, int width, String delim) {
        String out = "";
        String[] words;
        int currentWidth = 0;

        //Parse out tabs and new lines
        text = text.replaceAll("[\t]", " ");
        words = text.split(delim);

        //Rewrap to new width
        for (String word : words) {
            if (word.length() >= width) {
                //If it's not the first word, put it on a new line
                if (!out.isEmpty()) {
                    out += "\n + ";
                }
                out += word + " ";
                currentWidth = word.length();
            }
            else if ((currentWidth + word.length()) <= width) {
                out += word + " ";
                currentWidth += word.length() + 1;
            } else {
                out = out.substring(0, out.length() - 1);
                out += "\n + " + word + " ";
                currentWidth = word.length() + 1;
            }
        }

        return out.substring(0, out.length() - 1);
    }

    public static String wordWrap(String text, int width) {
        return wordWrap(text, width, " ");
    }

    public static String drawBox(String text, int width) {
        String out;
        String border;
        String[] lines;

        border = " ";
        for (int i = 0; i < (width - 2); i++) {
            border += "-";
        }
        border += " \n";

        out = border;

        if (width < 5) {
            width = 5;
        }
        width -= 4;
        lines = wordWrap(text, width).split("\n");
        for (String line : lines) {
            out += String.format("| %-" + width + "s |\n", line);
        }

        out += border;

        return out;
    }

}
