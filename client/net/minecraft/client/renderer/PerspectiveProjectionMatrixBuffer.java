package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class PerspectiveProjectionMatrixBuffer implements AutoCloseable {
   private final GpuBuffer buffer;
   private final GpuBufferSlice bufferSlice;

   public PerspectiveProjectionMatrixBuffer(final String name) {
      super();
      GpuDevice device = RenderSystem.getDevice();
      this.buffer = device.createBuffer(() -> "Projection matrix UBO " + name, 136, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.bufferSlice = this.buffer.slice(0L, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice getBuffer(final Matrix4f projectionMatrix) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         ByteBuffer byteBuffer = Std140Builder.onStack(stack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(projectionMatrix).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
      } catch (Throwable var6) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (stack != null) {
         stack.close();
      }

      return this.bufferSlice;
   }

   public void close() {
      this.buffer.close();
   }
}
