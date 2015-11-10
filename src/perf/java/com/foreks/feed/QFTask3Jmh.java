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

import com.lmax.disruptor.RingBuffer;

import co.paralleluniverse.strands.channels.disruptor.StrandBlockingWaitStrategy;

@State(Scope.Benchmark)
public class QFTask3Jmh {

    public static FileReaderFiber         file;
    public static RingBuffer<StringEvent> buffer = null;

    @Setup
    public static void loadingFile() throws IOException {
        file = new FileReaderFiber();
        buffer = RingBuffer.createSingleProducer(StringEvent.FACTORY, 1024, new StrandBlockingWaitStrategy());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Fork(1)
    public void ringBufferTest() throws ExecutionException, InterruptedException, IOException {
        QFTask3.writer(file, buffer);
    }
}
