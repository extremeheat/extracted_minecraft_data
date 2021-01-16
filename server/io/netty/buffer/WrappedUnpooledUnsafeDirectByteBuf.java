package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class WrappedUnpooledUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
   WrappedUnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, long var2, int var4, boolean var5) {
      super(var1, PlatformDependent.directBuffer(var2, var4), var4, var5);
   }

   protected void freeDirect(ByteBuffer var1) {
      PlatformDependent.freeMemory(this.memoryAddress);
   }
}
