package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public class LoopingAudioStream implements AudioStream {
   private final LoopingAudioStream.AudioStreamProvider provider;
   private AudioStream stream;
   private final BufferedInputStream bufferedInputStream;

   public LoopingAudioStream(LoopingAudioStream.AudioStreamProvider var1, InputStream var2) throws IOException {
      super();
      this.provider = var1;
      this.bufferedInputStream = new BufferedInputStream(var2);
      this.bufferedInputStream.mark(2147483647);
      this.stream = var1.create(new LoopingAudioStream.NoCloseBuffer(this.bufferedInputStream));
   }

   public AudioFormat getFormat() {
      return this.stream.getFormat();
   }

   public ByteBuffer read(int var1) throws IOException {
      ByteBuffer var2 = this.stream.read(var1);
      if (!var2.hasRemaining()) {
         this.stream.close();
         this.bufferedInputStream.reset();
         this.stream = this.provider.create(new LoopingAudioStream.NoCloseBuffer(this.bufferedInputStream));
         var2 = this.stream.read(var1);
      }

      return var2;
   }

   public void close() throws IOException {
      this.stream.close();
      this.bufferedInputStream.close();
   }

   @FunctionalInterface
   public interface AudioStreamProvider {
      AudioStream create(InputStream var1) throws IOException;
   }

   private static class NoCloseBuffer extends FilterInputStream {
      NoCloseBuffer(InputStream var1) {
         super(var1);
      }

      public void close() {
      }
   }
}
