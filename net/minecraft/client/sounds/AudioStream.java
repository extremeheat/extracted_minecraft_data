package net.minecraft.client.sounds;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public interface AudioStream extends Closeable {
   AudioFormat getFormat();

   ByteBuffer readAll() throws IOException;

   @Nullable
   ByteBuffer read(int var1) throws IOException;
}
