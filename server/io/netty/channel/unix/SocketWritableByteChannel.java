package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class SocketWritableByteChannel implements WritableByteChannel {
   private final FileDescriptor fd;

   protected SocketWritableByteChannel(FileDescriptor var1) {
      super();
      this.fd = (FileDescriptor)ObjectUtil.checkNotNull(var1, "fd");
   }

   public final int write(ByteBuffer var1) throws IOException {
      int var3 = var1.position();
      int var4 = var1.limit();
      int var2;
      if (var1.isDirect()) {
         var2 = this.fd.write(var1, var3, var1.limit());
      } else {
         int var5 = var4 - var3;
         ByteBuf var6 = null;

         try {
            if (var5 == 0) {
               var6 = Unpooled.EMPTY_BUFFER;
            } else {
               ByteBufAllocator var7 = this.alloc();
               if (var7.isDirectBufferPooled()) {
                  var6 = var7.directBuffer(var5);
               } else {
                  var6 = ByteBufUtil.threadLocalDirectBuffer();
                  if (var6 == null) {
                     var6 = Unpooled.directBuffer(var5);
                  }
               }
            }

            var6.writeBytes(var1.duplicate());
            ByteBuffer var11 = var6.internalNioBuffer(var6.readerIndex(), var5);
            var2 = this.fd.write(var11, var11.position(), var11.limit());
         } finally {
            if (var6 != null) {
               var6.release();
            }

         }
      }

      if (var2 > 0) {
         var1.position(var3 + var2);
      }

      return var2;
   }

   public final boolean isOpen() {
      return this.fd.isOpen();
   }

   public final void close() throws IOException {
      this.fd.close();
   }

   protected abstract ByteBufAllocator alloc();
}
