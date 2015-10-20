package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class QFTask1 {

    private static Channel<String>   channel = Channels.newChannel(1000, Channels.OverflowPolicy.BLOCK, true, true);
    private static ArrayList<String> ls      = new ArrayList<String>();

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
        reader();
        writer();
        System.out.println("Task is complated");
    }
    static int i;

    private static void writer() throws ExecutionException, InterruptedException {
        for (i = 0; i < 100; i++) {
            new Fiber<Void>("writer " + i, new SuspendableRunnable() {
                private static final long serialVersionUID = 1L;

                @Override
                public void run() throws SuspendExecution, InterruptedException {

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
                        for (int j = 0; j < ls.size(); j++) {
                            pWriter.println(ls.get(j));
                        }

                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pWriter.close();

                }

            }).start().join();
        }
    }

    private static void reader() throws IOException {

        Files.lines(Paths.get("File.txt")).forEach(s -> {
            ls.add(s);
        });
    }

}