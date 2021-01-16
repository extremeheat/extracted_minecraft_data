package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

class UnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
   UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected ByteBuffer allocateDirect(int var1) {
      return PlatformDependent.allocateDirectNoCleaner(var1);
   }

   ByteBuffer reallocateDirect(ByteBuffer var1, int var2) {
      return PlatformDependent.reallocateDirectNoCleaner(var1, var2);
   }

   protected void freeDirect(ByteBuffer var1) {
      PlatformDependent.freeDirectNoCleaner(var1);
   }

   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.capacity();
      if (var1 == var2) {
         return this;
      } else {
         ByteBuffer var3 = this.reallocateDirect(this.buffer, var1);
         if (var1 < var2) {
            if (this.readerIndex() < var1) {
               if (this.writerIndex() > var1) {
                  this.writerIndex(var1);
               }
            } else {
               this.setIndex(var1, var1);
            }
         }

         this.setByteBuffer(var3, false);
         return this;
      }
   }
}
