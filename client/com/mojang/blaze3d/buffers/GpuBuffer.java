package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
   private final @GpuBuffer.Usage int usage;
   private final long size;

   public GpuBuffer(@GpuBuffer.Usage int var1, long var2) {
      super();
      this.size = var2;
      this.usage = var1;
   }

   public long size() {
      return this.size;
   }

   public @GpuBuffer.Usage int usage() {
      return this.usage;
   }

   public abstract boolean isClosed();

   public abstract void close();

   public GpuBufferSlice slice(long var1, long var3) {
      if (var1 >= 0L && var3 >= 0L && var1 + var3 <= this.size) {
         return new GpuBufferSlice(this, var1, var3);
      } else {
         throw new IllegalArgumentException("Offset of " + var1 + " and length " + var3 + " would put new slice outside buffer's range (of 0," + var3 + ")");
      }
   }

   public GpuBufferSlice slice() {
      return new GpuBufferSlice(this, 0L, this.size);
   }

   @DontObfuscate
   public interface MappedView extends AutoCloseable {
      ByteBuffer data();

      void close();
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Usage {
   }
}
