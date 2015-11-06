package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;

public class FileWriterFibers implements SuspendableRunnable {
    private static final long    serialVersionUID = 1L;
    private Channel<StringEvent> channel          = null;
    private Channel<String>      channel2         = null;
    private final int            id;
    private PrintWriter          pWriter;

    public FileWriterFibers(final int id, final Channel<StringEvent> c, final String folder) {
        this.channel = c;
        this.id = id;
        final File f = new File("Results" + folder + "/File" + this.id + ".txt");
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

    public FileWriterFibers(final int id, final Channel<String> c) {
        this.channel2 = c;
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
        if (null != this.channel) {
            final StringEvent receiveStr = this.channel.receive();
            while (!this.channel.isClosed()) {
                this.pWriter.println(receiveStr.getValue());
            }
        } else if (null != this.channel2) {
            final String receiveStr = this.channel2.receive();
            while (!this.channel2.isClosed()) {
                this.pWriter.println(receiveStr);
            }
        }

        this.pWriter.close();

    }

}
