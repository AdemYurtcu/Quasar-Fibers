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
        writer();
        System.out.println("Task is complated");
    }
    static int i;

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