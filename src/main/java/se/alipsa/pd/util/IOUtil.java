package se.alipsa.pd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class IOUtil {

  public static void download(URL url, File toFile) throws IOException {
    try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(toFile)) {
      FileChannel fileChannel = fileOutputStream.getChannel();
      fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
    }
  }
}
