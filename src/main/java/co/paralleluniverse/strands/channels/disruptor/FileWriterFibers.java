package co.paralleluniverse.strands.channels.disruptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;

public class FileWriterFibers implements SuspendableRunnable {
    private static final long          serialVersionUID = 1L;
    private final Channel<StringEvent> channel;
    private final int                  id;
    private PrintWriter                pWriter;

    public FileWriterFibers(final int id, final Channel<StringEvent> channel2, final String folder) {
        this.channel = channel2;
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

    @Override
    public void run() throws SuspendExecution, InterruptedException {
        final StringEvent receiveStr = this.channel.receive();
        while (!"end".equals(receiveStr.getValue())) {
            this.pWriter.println(receiveStr.getValue());
        }
        this.pWriter.close();

    }

}
