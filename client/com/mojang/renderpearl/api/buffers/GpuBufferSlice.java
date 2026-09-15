package com.mojang.renderpearl.api.buffers;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.nio.ByteBuffer;

public record GpuBufferSlice(GpuBuffer buffer, long offset, long length) {
   public GpuBufferSlice {
      super();
   }

   public GpuBufferSlice slice(final long offset, final long length) {
      if (offset >= 0L && length >= 0L && offset + length <= this.length) {
         return new GpuBufferSlice(this.buffer, this.offset + offset, length);
      } else {
         throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside existing slice's range (of " + this.offset + "," + this.length + ")");
      }
   }

   public MappedView map(final boolean read, final boolean write) {
      return this.buffer.map(this.offset, this.length, read, write);
   }

   public static record MappedView(GpuBufferSlice slice, ByteBuffer data, Runnable onClose) implements UncheckedAutoCloseable {
      public MappedView {
         super();
      }

      public void close() {
         this.onClose.run();
      }
   }
}
