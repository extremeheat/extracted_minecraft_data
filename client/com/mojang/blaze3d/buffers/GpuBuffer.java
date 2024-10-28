package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

public class GpuBuffer implements AutoCloseable {
   private static final MemoryPool MEMORY_POOl = TracyClient.createMemoryPool("GPU Buffers");
   private final BufferType type;
   private final BufferUsage usage;
   private boolean closed;
   private boolean initialized;
   public final int handle;
   public int size;

   public GpuBuffer(BufferType var1, BufferUsage var2, int var3) {
      super();
      this.initialized = false;
      this.type = var1;
      this.size = var3;
      this.usage = var2;
      this.handle = GlStateManager._glGenBuffers();
   }

   public GpuBuffer(BufferType var1, BufferUsage var2, ByteBuffer var3) {
      this(var1, var2, var3.remaining());
      this.write(var3, 0);
   }

   public void resize(int var1) {
      if (this.closed) {
         throw new IllegalStateException("Buffer already closed");
      } else {
         if (this.initialized) {
            MEMORY_POOl.free((long)this.handle);
         }

         this.size = var1;
         if (this.usage.writable) {
            this.initialized = false;
         } else {
            this.bind();
            GlStateManager._glBufferData(this.type.id, (long)var1, this.usage.id);
            MEMORY_POOl.malloc((long)this.handle, var1);
            this.initialized = true;
         }

      }
   }

   public void write(ByteBuffer var1, int var2) {
      if (this.closed) {
         throw new IllegalStateException("Buffer already closed");
      } else if (!this.usage.writable) {
         throw new IllegalStateException("Buffer is not writable");
      } else {
         int var3 = var1.remaining();
         if (var3 + var2 > this.size) {
            throw new IllegalArgumentException("Cannot write more data than this buffer can hold (attempting to write " + var3 + " bytes at offset " + var2 + " to " + this.size + " size buffer)");
         } else {
            this.bind();
            if (this.initialized) {
               GlStateManager._glBufferSubData(this.type.id, var2, var1);
            } else if (var2 == 0 && var3 == this.size) {
               GlStateManager._glBufferData(this.type.id, var1, this.usage.id);
               MEMORY_POOl.malloc((long)this.handle, this.size);
               this.initialized = true;
            } else {
               GlStateManager._glBufferData(this.type.id, (long)this.size, this.usage.id);
               GlStateManager._glBufferSubData(this.type.id, var2, var1);
               MEMORY_POOl.malloc((long)this.handle, this.size);
               this.initialized = true;
            }

         }
      }
   }

   @Nullable
   public ReadView read() {
      return this.read(0, this.size);
   }

   @Nullable
   public ReadView read(int var1, int var2) {
      if (this.closed) {
         throw new IllegalStateException("Buffer already closed");
      } else if (!this.usage.readable) {
         throw new IllegalStateException("Buffer is not readable");
      } else if (var1 + var2 > this.size) {
         throw new IllegalArgumentException("Cannot read more data than this buffer can hold (attempting to read " + var2 + " bytes at offset " + var1 + " from " + this.size + " size buffer)");
      } else {
         this.bind();
         ByteBuffer var3 = GlStateManager._glMapBufferRange(this.type.id, var1, var2, 1);
         return var3 == null ? null : new ReadView(this.type.id, var3);
      }
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         GlStateManager._glDeleteBuffers(this.handle);
         if (this.initialized) {
            MEMORY_POOl.free((long)this.handle);
         }

      }
   }

   public void bind() {
      GlStateManager._glBindBuffer(this.type.id, this.handle);
   }

   public static class ReadView implements AutoCloseable {
      private final int target;
      private final ByteBuffer data;

      protected ReadView(int var1, ByteBuffer var2) {
         super();
         this.target = var1;
         this.data = var2;
      }

      public ByteBuffer data() {
         return this.data;
      }

      public void close() {
         GlStateManager._glUnmapBuffer(this.target);
      }
   }
}
