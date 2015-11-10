package com.foreks.feed;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

import co.paralleluniverse.fibers.SuspendExecution;

@State(Scope.Benchmark)
public class QFTask1Jmh {
    public static FileReaderFiber file;

    @Setup
    public static void loadingFile() throws IOException {
        file = new FileReaderFiber();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Fork(1)
    public static void paralellTest() throws ExecutionException, InterruptedException, SuspendExecution, IOException {
        QFTask1.writer(file);
    }

}
