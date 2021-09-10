package com.su.util;

import ch.qos.logback.core.util.SystemInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HackInputStream extends InputStream {
    private final static ThreadLocal<InputStream> holdInputStream=new ThreadLocal<>();

    @Override
    public int read() throws IOException {
        return 0;
    }

    public InputStream get(){
        return holdInputStream.get();
    }

    public void set(String systemIn)
    {
        holdInputStream.set(new ByteArrayInputStream(systemIn.getBytes()));
    }

    public void close()
    {
        holdInputStream.remove();
    }
}
