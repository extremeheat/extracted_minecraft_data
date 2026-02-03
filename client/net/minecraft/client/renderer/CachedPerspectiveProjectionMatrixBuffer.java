package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class CachedPerspectiveProjectionMatrixBuffer implements AutoCloseable {
   private final GpuBuffer buffer;
   private final GpuBufferSlice bufferSlice;
   private final float zNear;
   private final float zFar;
   private int width;
   private int height;
   private float fov;

   public CachedPerspectiveProjectionMatrixBuffer(final String name, final float zNear, final float zFar) {
      super();
      this.zNear = zNear;
      this.zFar = zFar;
      GpuDevice device = RenderSystem.getDevice();
      this.buffer = device.createBuffer(() -> "Projection matrix UBO " + name, 136, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.bufferSlice = this.buffer.slice(0L, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice getBuffer(final int width, final int height, final float fov) {
      if (this.width != width || this.height != height || this.fov != fov) {
         Matrix4f projectionMatrix = this.createProjectionMatrix(width, height, fov);
         MemoryStack stack = MemoryStack.stackPush();

         try {
            ByteBuffer byteBuffer = Std140Builder.onStack(stack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(projectionMatrix).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
         } catch (Throwable var9) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (stack != null) {
            stack.close();
         }

         this.width = width;
         this.height = height;
         this.fov = fov;
      }

      return this.bufferSlice;
   }

   private Matrix4f createProjectionMatrix(final int width, final int height, final float fov) {
      return (new Matrix4f()).perspective(fov * 0.017453292F, (float)width / (float)height, this.zNear, this.zFar, RenderSystem.getDevice().isZZeroToOne());
   }

   public void close() {
      this.buffer.close();
   }
}
