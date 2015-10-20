package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;

public class FileWriterFiber implements SuspendableRunnable {

    private final Channel<String> channel;
    private final int             id;
    private PrintWriter           pWriter;

    public FileWriterFiber(final int id, final Channel<String> c) {
        this.channel = c;
        this.id = id;
        final File f = new File("Results/File" + this.id + ".txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (final IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            final FileWriter fileWriter = new FileWriter(f, true);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            this.pWriter = new PrintWriter(bufferedWriter);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() throws SuspendExecution, InterruptedException {
        while (!this.channel.isClosed()) {
            final String receiveStr = this.channel.receive();
            System.out.println(this.id + " msg <- " + receiveStr);
            this.pWriter.println(receiveStr);
        }
        this.pWriter.close();

    }

}
