package com.foreks.feed;

import static com.foreks.feed.UtilException.rethrowConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class QFTask1 {

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {

        writer();

        System.out.println("Task is complated");
    }
    static int i;

    private static void writer() throws ExecutionException, InterruptedException {
        final Channel<String> channel = Channels.newChannel(10000, Channels.OverflowPolicy.BLOCK, true, false);
        final List<Fiber<Void>> fibers = IntStream.range(0, 10)
                                                  .mapToObj((final int index) -> new Fiber<Void>("writer "
                                                                                                 + index,
                                                                                                 new com.foreks.feed.FileWriterFiber(index, channel)))
                                                  .peek(rethrowConsumer(f -> f.start()))
                                                  .collect(Collectors.toList());
        try {
            Files.lines(Paths.get("File.txt")).forEach(s -> {
                try {
                    System.out.println("msg ->" + s);
                    channel.send(s);
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        channel.close();
        fibers.stream().forEach(rethrowConsumer(f -> f.join()));

    }

}
