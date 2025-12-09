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

   public PerspectiveProjectionMatrixBuffer(String var1) {
      super();
      GpuDevice var2 = RenderSystem.getDevice();
      this.buffer = var2.createBuffer(() -> "Projection matrix UBO " + var1, 136, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.bufferSlice = this.buffer.slice(0L, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice getBuffer(Matrix4f var1) {
      MemoryStack var2 = MemoryStack.stackPush();

      try {
         ByteBuffer var3 = Std140Builder.onStack(var2, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(var1).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var3);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

      return this.bufferSlice;
   }

   public void close() {
      this.buffer.close();
   }
}
