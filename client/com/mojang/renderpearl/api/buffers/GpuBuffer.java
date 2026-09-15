package com.mojang.renderpearl.api.buffers;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface GpuBuffer extends UncheckedAutoCloseable {
   int USAGE_MAP_READ = 1;
   int USAGE_MAP_WRITE = 2;
   int USAGE_HINT_CLIENT_STORAGE = 4;
   int USAGE_COPY_DST = 8;
   int USAGE_COPY_SRC = 16;
   int USAGE_VERTEX = 32;
   int USAGE_INDEX = 64;
   int USAGE_UNIFORM = 128;
   int USAGE_UNIFORM_TEXEL_BUFFER = 256;
   int USAGE_INDIRECT_PARAMETERS = 512;

   long size();

   @GpuBuffer.Usage int usage();

   boolean isClosed();

   default GpuBufferSlice slice(final long offset, final long length) {
      if (offset >= 0L && length >= 0L && offset + length <= this.size()) {
         return new GpuBufferSlice(this, offset, length);
      } else {
         throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside buffer's range (of 0," + this.size() + ")");
      }
   }

   default GpuBufferSlice slice() {
      return new GpuBufferSlice(this, 0L, this.size());
   }

   default GpuBufferSlice.MappedView map(final boolean read, final boolean write) {
      return this.map(0L, this.size(), read, write);
   }

   GpuBufferSlice.MappedView map(long offset, long length, boolean read, boolean write);

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Usage {
   }
}
