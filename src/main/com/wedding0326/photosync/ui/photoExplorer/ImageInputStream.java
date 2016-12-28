package com.wedding0326.photosync.ui.photoExplorer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.wedding0326.photosync.util.TestUtils;

public class ImageInputStream extends InputStream {

    private MappedByteBuffer mbb;

    private long remaining;

    private long loc;

    private long length;

    public ImageInputStream(String name) throws FileNotFoundException {
        if (null != name) {

            FileInputStream fis = new FileInputStream(name);
            FileChannel fc = fis.getChannel();
            try {
                mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                TestUtils.printTime("a");
                length = fc.size();
                System.out.println("---length--->" + length);
                TestUtils.printTime("b");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (remaining <= 0) {
            return -1;
        }
        mbb.get(b);

        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        mbb.get(b, off, len);
        System.out.println("---len--->" + len);
        return len < length ? (int) length - len : -1;
    }

    @Override
    public int read() throws IOException {
        if (remaining < mbb.array().length)
            return 0;
        else
            return -1;
    }

}
