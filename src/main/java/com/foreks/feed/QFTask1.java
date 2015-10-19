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

    private static final Channel<String> channel = Channels.newChannel(5, Channels.OverflowPolicy.BLOCK, true, true);

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
        writer();
        reader();
        System.out.println("Task is complated");
    }
    static int i;

    private static void writer() throws ExecutionException, InterruptedException {
        for (i = 0; i < 500; i++) {
            final Fiber<Void> writer = new Fiber<Void>("writer " + i, new SuspendableRunnable() {
                private static final long serialVersionUID = 1L;

                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    final Channel<String> templateChannel = channel;
                    final File f = new File("Results/writer" + i + ".txt");
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (final IOException e) {
                            System.out.println(e.getMessage());
                        }

                    } else {
                        PrintWriter pWriter = null;
                        try {
                            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
                            while (templateChannel.isClosed() == true) {
                                final String receive = templateChannel.receive();
                                pWriter.println(receive);
                            }

                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        pWriter.close();
                    }

                }
            }).start();
            writer.join();
        }
    }

    private static void reader() throws IOException {

        Files.lines(Paths.get("File.txt")).forEach(s -> {
            try {
                channel.send(s);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

}