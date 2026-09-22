package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.commands.GpuQueryPool;
import java.util.OptionalLong;
import org.lwjgl.opengl.GL33C;

public class GlQueryPool implements GpuQueryPool {
   private final GlDevice device;
   private final int[] handles;
   private boolean closed;

   public GlQueryPool(final GlDevice device, final int size) {
      super();
      this.device = device;
      this.handles = new int[size];
      GL33C.glGenQueries(this.handles);
   }

   public int size() {
      return this.handles.length;
   }

   public OptionalLong getValue(final int index) {
      this.device.ensureCurrent();
      int handle = this.handles[index];
      return GL33C.glGetQueryObjecti(handle, 34919) == 0 ? OptionalLong.empty() : OptionalLong.of(GL33C.glGetQueryObjectui64(handle, 34918));
   }

   public OptionalLong[] getValues(final int index, final int count) {
      if (index + count > this.handles.length) {
         throw new IndexOutOfBoundsException("getValues would read out-of-bounds for an array of " + count + " starting at " + index + ", when total size is " + this.handles.length);
      } else {
         OptionalLong[] result = new OptionalLong[count];

         for(int i = 0; i < count; ++i) {
            result[i] = this.getValue(index + i);
         }

         return result;
      }
   }

   protected void writeTimestamp(final int index) {
      this.device.ensureCurrent();
      GL33C.glQueryCounter(this.handles[index], 36392);
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.ensureCurrent();
         GL33C.glDeleteQueries(this.handles);
      }

   }
}
