package com.foreks.feed;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.channels.disruptor.DisruptorReceiveChannel;
import co.paralleluniverse.strands.channels.disruptor.StrandBlockingWaitStrategy;

public class QFTask3 {
    public static RingBuffer<StringEvent> buffer = null;
    private static FileReaderFiber        file   = null;

    public static void loadingFile() throws IOException {
        file = new FileReaderFiber();
        buffer = RingBuffer.createSingleProducer(StringEvent.FACTORY, 1024, new StrandBlockingWaitStrategy());
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException, IOException {
        loadingFile();
        writer(file, buffer);
    }

    public static void writer(final FileReaderFiber file, final RingBuffer<StringEvent> buffer)
            throws ExecutionException, InterruptedException, IOException {
        final List<Fiber<Void>> list = IntStream.range(0, 10)
                                                .mapToObj(i -> new Fiber<Void>("Consumer"
                                                                               + i,
                                                                               new FileWriterFibers(createChannel(buffer),
                                                                                                    i,
                                                                                                    file.getDataSource().size())))
                                                .collect(Collectors.toList());
        final Fiber<Void> producer = new Fiber<Void>("producer", () -> {

            for (int i = 0; i < file.getDataSource().size(); i++) {
                final long sequence = buffer.next();
                try {
                    final StringEvent e = buffer.get(sequence);
                    e.setValue(file.get(i));
                } finally {
                    buffer.publish(sequence);
                }
            }

        });
        producer.start();

        list.forEach(f -> {
            try {
                f.start().join();
            } catch (final Exception e)

            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public static DisruptorReceiveChannel<StringEvent> createChannel(final RingBuffer<StringEvent> buffer) {
        final DisruptorReceiveChannel<StringEvent> c = new DisruptorReceiveChannel<StringEvent>(buffer, new Sequence[] {});
        return c;
    }
}
