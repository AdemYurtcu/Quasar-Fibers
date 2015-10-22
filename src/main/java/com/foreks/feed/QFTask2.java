package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
public class QFTask2 {

    private static FileReaderFiber file = null;

    @Setup
    public static void loadingFile() throws IOException {
        file = new FileReaderFiber();
    }

    public static void main(final String[] args) throws IOException {
        loadingFile();
        writer();
        System.out.println("task is complated ");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Fork(1)
    public static void writer() {

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