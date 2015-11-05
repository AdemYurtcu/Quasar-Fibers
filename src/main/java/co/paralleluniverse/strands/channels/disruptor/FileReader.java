package co.paralleluniverse.strands.channels.disruptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.collections4.list.GrowthList;

import com.lmax.disruptor.DataProvider;

public class FileReader implements DataProvider<String> {
    private final GrowthList<String> dataSource;

    public FileReader() throws IOException {
        this.dataSource = new GrowthList<String>(10);
        Files.lines(Paths.get("File.txt")).forEach(s -> {
            this.dataSource.add(s);
        });
    }

    @Override
    public String get(final long sequence) {
        return this.dataSource.get((int) sequence);
    }

}
