package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import java.nio.ByteBuffer;

@DontObfuscate
public abstract class GpuBuffer implements AutoCloseable {
   private final BufferType type;
   private final BufferUsage usage;
   public int size;

   public GpuBuffer(BufferType var1, BufferUsage var2, int var3) {
      super();
      this.type = var1;
      this.size = var3;
      this.usage = var2;
   }

   public int size() {
      return this.size;
   }

   public BufferType type() {
      return this.type;
   }

   public BufferUsage usage() {
      return this.usage;
   }

   public abstract boolean isClosed();

   public abstract void close();

   @DontObfuscate
   public interface ReadView extends AutoCloseable {
      ByteBuffer data();

      void close();
   }
}
