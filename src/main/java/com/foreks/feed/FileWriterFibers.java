package com.foreks.feed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.disruptor.DisruptorReceiveChannel;

public class FileWriterFibers implements SuspendableRunnable {
    private static final long                          serialVersionUID = 1L;
    private final Channel<String>                      channel;
    private final int                                  id;
    private PrintWriter                                pWriter;
    private final DisruptorReceiveChannel<StringEvent> channel2;
    private final int                                  bufferSize;

    public FileWriterFibers(final int id, final Channel<String> c) {
        this.channel = c;
        this.id = id;
        final File folder = new File("Results");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File f = new File("Results/File" + this.id + ".txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (final IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            f.delete();
            try {
                f.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final FileWriter fileWriter = new FileWriter(f, true);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            this.pWriter = new PrintWriter(bufferedWriter);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.channel2 = null;
        this.bufferSize = 0;
    }

    public FileWriterFibers(final DisruptorReceiveChannel<StringEvent> c, final int i, final int size) {
        this.channel2 = c;
        this.id = i;
        final File folder = new File("Results3");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File f = new File("Results3/File" + this.id + ".txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (final IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            f.delete();
            try {
                f.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
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
        this.channel = null;
        this.bufferSize = size;
    }

    @Override
    public void run() throws SuspendExecution, InterruptedException {
        if (null != this.channel) {
            String receiveStr;
            while (!this.channel.isClosed()) {
                receiveStr = this.channel.receive();
                this.pWriter.println(receiveStr);
            }
        }
        if (null != this.channel2) {
            StringEvent receiveString = this.channel2.receive();
            int k = 0;
            while (true) {
                this.pWriter.println(receiveString.getValue());
                k++;
                if (k == this.bufferSize) {
                    break;
                }
                receiveString = this.channel2.receive();
            }
        }
        this.pWriter.close();
    }

}