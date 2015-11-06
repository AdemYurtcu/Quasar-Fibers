package com.foreks.feed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.collections4.list.GrowthList;

import com.lmax.disruptor.DataProvider;

public class FileReaderFiber implements DataProvider<String> {
    private final GrowthList<String> dataSource;

    public FileReaderFiber() throws IOException {
        this.dataSource = new GrowthList<String>(10);
        Files.lines(Paths.get("File.txt")).forEach(s -> {
            this.dataSource.add(s);
        });
    }

    @Override
    public String get(final long sequence) {
        return this.dataSource.get((int) sequence);
    }

    public GrowthList<String> getDataSource() {
        return this.dataSource;
    }

}
