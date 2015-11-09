package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.IntStream;

public class QFTask2 {

    private static FileReaderFiber file = null;

    public static void loadingFile() throws IOException {
        file = new FileReaderFiber();
    }

    public static void main(final String[] args) throws IOException {
        loadingFile();
        writer();
        System.out.println("task is complated ");
    }

    public static void writer() throws IOException {
        loadingFile();
        IntStream.range(0, 100).forEach(i -> {
            final File f = new File("Results2/File" + i + ".txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (final IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            try {
                final FileWriter fileWriter = new FileWriter(f, true);
                final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                final PrintWriter pWriter = new PrintWriter(bufferedWriter);
                for (int j = 0; j < file.getDataSource().size(); j++) {
                    pWriter.println(file.get(j));
                }
                pWriter.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

    }

}