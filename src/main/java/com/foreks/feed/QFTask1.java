package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class QFTask1 {

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {

        writer();

        System.out.println("Task is complated");
    }
    static int i;

    private static void writer() throws ExecutionException, InterruptedException {
        for (i = 0; i < 1000; i++) {
            new Fiber<Void>("writer " + i, new SuspendableRunnable() {
                private static final long serialVersionUID = 1L;

                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    Channel<String> channel;
                    try {
                        channel = createChannel();
                        final File f = new File("Results/writer" + i + ".txt");
                        if (!f.exists()) {
                            try {
                                f.createNewFile();
                            } catch (final IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        PrintWriter pWriter = null;

                        try {
                            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        while (!channel.isClosed()) {
                            pWriter.println(channel.receive());
                        }

                        pWriter.close();
                    } catch (final IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }

            }).start().join();
        }

    }

    public static Channel<String> createChannel() throws IOException {
        final Channel<String> channel = Channels.newChannel(1200, Channels.OverflowPolicy.BLOCK, true, true);
        Files.lines(Paths.get("File.txt")).forEach(s -> {
            try {
                channel.send(s);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        channel.close();
        return channel;
    }

}
