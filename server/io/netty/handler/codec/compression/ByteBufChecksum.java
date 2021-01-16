package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

abstract class ByteBufChecksum implements Checksum {
   private static final Method ADLER32_UPDATE_METHOD = updateByteBuffer(new Adler32());
   private static final Method CRC32_UPDATE_METHOD = updateByteBuffer(new CRC32());
   private final ByteProcessor updateProcessor = new ByteProcessor() {
      public boolean process(byte var1) throws Exception {
         ByteBufChecksum.this.update(var1);
         return true;
      }
   };

   ByteBufChecksum() {
      super();
   }

   private static Method updateByteBuffer(Checksum var0) {
      if (PlatformDependent.javaVersion() >= 8) {
         try {
            Method var1 = var0.getClass().getDeclaredMethod("update", ByteBuffer.class);
            var1.invoke(var1, ByteBuffer.allocate(1));
            return var1;
         } catch (Throwable var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   static ByteBufChecksum wrapChecksum(Checksum var0) {
      ObjectUtil.checkNotNull(var0, "checksum");
      if (var0 instanceof Adler32 && ADLER32_UPDATE_METHOD != null) {
         return new ByteBufChecksum.ReflectiveByteBufChecksum(var0, ADLER32_UPDATE_METHOD);
      } else {
         return (ByteBufChecksum)(var0 instanceof CRC32 && CRC32_UPDATE_METHOD != null ? new ByteBufChecksum.ReflectiveByteBufChecksum(var0, CRC32_UPDATE_METHOD) : new ByteBufChecksum.SlowByteBufChecksum(var0));
      }
   }

   public void update(ByteBuf var1, int var2, int var3) {
      if (var1.hasArray()) {
         this.update(var1.array(), var1.arrayOffset() + var2, var3);
      } else {
         var1.forEachByte(var2, var3, this.updateProcessor);
      }

   }

   private static class SlowByteBufChecksum extends ByteBufChecksum {
      protected final Checksum checksum;

      SlowByteBufChecksum(Checksum var1) {
         super();
         this.checksum = var1;
      }

      public void update(int var1) {
         this.checksum.update(var1);
      }

      public void update(byte[] var1, int var2, int var3) {
         this.checksum.update(var1, var2, var3);
      }

      public long getValue() {
         return this.checksum.getValue();
      }

      public void reset() {
         this.checksum.reset();
      }
   }

   private static final class ReflectiveByteBufChecksum extends ByteBufChecksum.SlowByteBufChecksum {
      private final Method method;

      ReflectiveByteBufChecksum(Checksum var1, Method var2) {
         super(var1);
         this.method = var2;
      }

      public void update(ByteBuf var1, int var2, int var3) {
         if (var1.hasArray()) {
            this.update(var1.array(), var1.arrayOffset() + var2, var3);
         } else {
            try {
               this.method.invoke(this.checksum, CompressionUtil.safeNioBuffer(var1));
            } catch (Throwable var5) {
               throw new Error();
            }
         }

      }
   }
}
