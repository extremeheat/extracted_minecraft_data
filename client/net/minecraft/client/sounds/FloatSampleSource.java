package net.minecraft.client.sounds;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface FloatSampleSource extends FiniteAudioStream {
   int EXPECTED_MAX_FRAME_SIZE = 8192;

   boolean readChunk(FloatConsumer var1) throws IOException;

   default ByteBuffer read(int var1) throws IOException {
      ChunkedSampleByteBuf var2 = new ChunkedSampleByteBuf(var1 + 8192);

      while(this.readChunk(var2) && var2.size() < var1) {
      }

      return var2.get();
   }

   default ByteBuffer readAll() throws IOException {
      ChunkedSampleByteBuf var1 = new ChunkedSampleByteBuf(16384);

      while(this.readChunk(var1)) {
      }

      return var1.get();
   }
}
