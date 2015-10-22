package com.foreks.feed;

import static com.foreks.feed.UtilExceptions.rethroConsumer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
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

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

@State(Scope.Benchmark)
public class QFTask1 {
    private static FileReaderFiber fileReader = null;

    @Setup
    public static void loadingFile() throws IOException {
        fileReader = new FileReaderFiber();

    }

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
        loadingFile();
        writer();
        System.out.println("Task is complated");
    }
    static int i;

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Fork(1)
    public static void writer() throws ExecutionException, InterruptedException, SuspendExecution {

        IntStream.range(0, 100)
                 .mapToObj((final int index) -> new Fiber<Void>("writer " + index, new FileWriterFibers(index, createChannel())))
                 .forEach(rethroConsumer(f -> f.start().join()));

    }

    public static Channel<String> createChannel() {
        final Channel<String> channel = Channels.newChannel(10000, Channels.OverflowPolicy.BLOCK, true, false);
        for (int i = 0; i < fileReader.getDataSource().size(); i++) {
            try {
                channel.send(fileReader.get(i));
            } catch (SuspendExecution | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        channel.close();
        return channel;
    }

}
