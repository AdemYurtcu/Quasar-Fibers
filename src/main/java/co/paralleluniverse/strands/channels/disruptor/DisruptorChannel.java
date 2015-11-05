package co.paralleluniverse.strands.channels.disruptor;

import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Timeout;
import co.paralleluniverse.strands.channels.Channel;

public class DisruptorChannel extends DisruptorReceiveChannel<StringEvent> implements Channel<StringEvent> {

    public DisruptorChannel(final RingBuffer<StringEvent> buffer, final Sequence[] dependentSequences) {
        super(buffer, dependentSequences);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void send(final StringEvent message) throws SuspendExecution, InterruptedException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean send(final StringEvent message, final long timeout, final TimeUnit unit) throws SuspendExecution, InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean send(final StringEvent message, final Timeout timeout) throws SuspendExecution, InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean trySend(final StringEvent message) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close(final Throwable t) {
        // TODO Auto-generated method stub

    }

}
