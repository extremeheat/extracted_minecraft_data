package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public class LoopingAudioStream implements AudioStream {
   private final AudioStreamProvider provider;
   private AudioStream stream;
   private final BufferedInputStream bufferedInputStream;

   public LoopingAudioStream(final AudioStreamProvider provider, final InputStream originalInputStream) throws IOException {
      super();
      this.provider = provider;
      this.bufferedInputStream = new BufferedInputStream(originalInputStream);
      this.bufferedInputStream.mark(2147483647);
      this.stream = provider.create(new NoCloseBuffer(this.bufferedInputStream));
   }

   public AudioFormat getFormat() {
      return this.stream.getFormat();
   }

   public ByteBuffer read(final int expectedSize) throws IOException {
      ByteBuffer result = this.stream.read(expectedSize);
      if (!result.hasRemaining()) {
         this.stream.close();
         this.bufferedInputStream.reset();
         this.stream = this.provider.create(new NoCloseBuffer(this.bufferedInputStream));
         result = this.stream.read(expectedSize);
      }

      return result;
   }

   public void close() throws IOException {
      this.stream.close();
      this.bufferedInputStream.close();
   }

   private static class NoCloseBuffer extends FilterInputStream {
      private NoCloseBuffer(final InputStream in) {
         super(in);
      }

      public void close() {
      }
   }

   @FunctionalInterface
   public interface AudioStreamProvider {
      AudioStream create(final InputStream inputStream) throws IOException;
   }
}
