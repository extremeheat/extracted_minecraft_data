package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import java.nio.ByteBuffer;

@DontObfuscate
public abstract class GpuBuffer implements AutoCloseable {
   public static final int USAGE_MAP_READ = 1;
   public static final int USAGE_MAP_WRITE = 2;
   public static final int USAGE_HINT_CLIENT_STORAGE = 4;
   public static final int USAGE_COPY_DST = 8;
   public static final int USAGE_COPY_SRC = 16;
   public static final int USAGE_VERTEX = 32;
   public static final int USAGE_INDEX = 64;
   public static final int USAGE_UNIFORM = 128;
   public static final int USAGE_UNIFORM_TEXEL_BUFFER = 256;
   private final int usage;
   private final int size;

   public GpuBuffer(int var1, int var2) {
      super();
      this.size = var2;
      this.usage = var1;
   }

   public int size() {
      return this.size;
   }

   public int usage() {
      return this.usage;
   }

   public abstract boolean isClosed();

   public abstract void close();

   public GpuBufferSlice slice(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0 && var1 + var2 <= this.size) {
         return new GpuBufferSlice(this, var1, var2);
      } else {
         throw new IllegalArgumentException("Offset of " + var1 + " and length " + var2 + " would put new slice outside buffer's range (of 0," + var2 + ")");
      }
   }

   public GpuBufferSlice slice() {
      return new GpuBufferSlice(this, 0, this.size);
   }

   @DontObfuscate
   public interface MappedView extends AutoCloseable {
      ByteBuffer data();

      void close();
   }
}
