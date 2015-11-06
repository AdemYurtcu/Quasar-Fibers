package com.foreks.feed;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.disruptor.StrandBlockingWaitStrategy;

public class QFTask3 {
    public static final RingBuffer<StringEvent> buffer = RingBuffer.createSingleProducer(StringEvent.FACTORY, 1024, new StrandBlockingWaitStrategy());

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        writer();
    }

    public static void writer() throws ExecutionException, InterruptedException {

        final List<Fiber<Void>> list = IntStream.range(0, 100)
                                                .mapToObj(i -> new Fiber<Void>("Consumer" + i, new FileWriterFibers(i, createChannell(), "3")))
                                                .collect(Collectors.toList());

        final Fiber<Void> producer = new Fiber<Void>("-1", () -> {
            for (int i = 0; i < 100; i++) {
                final long sequence = buffer.next();
                try {
                    final StringEvent e = buffer.get(sequence);
                    e.setValue("" + i);
                } finally {
                    buffer.publish(sequence);
                }
            }

        });
        producer.start().join();
        list.forEach(f -> {
            try {
                f.join();
            } catch (final Exception e)

            {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        });
    }

    public static Channel<StringEvent> createChannell() {
        final Channel<StringEvent> channel = new DisruptorChannel(buffer, new Sequence[] {});
        return channel;
    }
}
