package com.foreks.feed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class QFTask1 {
    private static final Channel<String> channel = Channels.newChannel(1000, Channels.OverflowPolicy.BLOCK, true, true);

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
        reader();
        System.out.println("Task is complated.");
    }
    private static int i = 0;

    private static void reader() throws IOException {

        Files.lines(Paths.get("File.txt")).forEach(s -> {
            i++;

            final Fiber<Void> writer = new Fiber<Void>("reader " + i, new SuspendableRunnable() {

                private static final long serialVersionUID = 1L;

                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    final String receive = channel.receive();
                    System.out.println(receive);
                }
            }).start();
            final String message = s + " " + writer.getName();
            try {
                channel.send(message);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                writer.join();
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
    }

}
