package com.foreks.feed;

import static com.foreks.feed.UtilExceptions.rethroConsumer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class QFTask1 {
    private static FileReaderFiber fileReader = null;

    public static void loadingFile() throws IOException {
        fileReader = new FileReaderFiber();
    }

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
        loadingFile();
        writer(fileReader);
        System.out.println("Task is complated");
    }
    static int i;

    public static void writer(final FileReaderFiber file) throws ExecutionException, InterruptedException, SuspendExecution, IOException {
        IntStream.range(0, 10)
                 .mapToObj((final int index) -> new Fiber<Void>("writer " + index, new FileWriterFibers(index, createChannel(file))))
                 .forEach(rethroConsumer(f -> f.start().join()));

    }

    public static Channel<String> createChannel(final FileReaderFiber file) {
        final Channel<String> channel = Channels.newChannel(10000, Channels.OverflowPolicy.BLOCK, true, false);
        for (int i = 0; i < file.getDataSource().size(); i++) {
            try {
                channel.send(file.get(i));
            } catch (SuspendExecution | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        channel.close();
        return channel;
    }

}