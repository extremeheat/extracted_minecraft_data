package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.Mth;
import org.lwjgl.BufferUtils;

public class ChunkedSampleByteBuf implements FloatConsumer {
   private final List<ByteBuffer> buffers = Lists.newArrayList();
   private final int bufferSize;
   private int byteCount;
   private ByteBuffer currentBuffer;

   public ChunkedSampleByteBuf(int var1) {
      super();
      this.bufferSize = var1 + 1 & -2;
      this.currentBuffer = BufferUtils.createByteBuffer(var1);
   }

   public void accept(float var1) {
      if (this.currentBuffer.remaining() == 0) {
         this.currentBuffer.flip();
         this.buffers.add(this.currentBuffer);
         this.currentBuffer = BufferUtils.createByteBuffer(this.bufferSize);
      }

      int var2 = Mth.clamp((int)(var1 * 32767.5F - 0.5F), -32768, 32767);
      this.currentBuffer.putShort((short)var2);
      this.byteCount += 2;
   }

   public ByteBuffer get() {
      this.currentBuffer.flip();
      if (this.buffers.isEmpty()) {
         return this.currentBuffer;
      } else {
         ByteBuffer var1 = BufferUtils.createByteBuffer(this.byteCount);
         List var10000 = this.buffers;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::put);
         var1.put(this.currentBuffer);
         var1.flip();
         return var1;
      }
   }

   public int size() {
      return this.byteCount;
   }
}
