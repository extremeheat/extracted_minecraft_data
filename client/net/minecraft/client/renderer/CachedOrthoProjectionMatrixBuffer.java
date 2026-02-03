package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class CachedOrthoProjectionMatrixBuffer implements AutoCloseable {
   private final GpuBuffer buffer;
   private final GpuBufferSlice bufferSlice;
   private final float zNear;
   private final float zFar;
   private final boolean invertY;
   private float width;
   private float height;

   public CachedOrthoProjectionMatrixBuffer(final String name, final float zNear, final float zFar, final boolean invertY) {
      super();
      this.zNear = zNear;
      this.zFar = zFar;
      this.invertY = invertY;
      GpuDevice device = RenderSystem.getDevice();
      this.buffer = device.createBuffer(() -> "Projection matrix UBO " + name, 136, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.bufferSlice = this.buffer.slice(0L, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice getBuffer(final float width, final float height) {
      if (this.width != width || this.height != height) {
         Matrix4f projectionMatrix = this.createProjectionMatrix(width, height);
         MemoryStack stack = MemoryStack.stackPush();

         try {
            ByteBuffer byteBuffer = Std140Builder.onStack(stack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(projectionMatrix).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }

         this.width = width;
         this.height = height;
      }

      return this.bufferSlice;
   }

   private Matrix4f createProjectionMatrix(final float width, final float height) {
      return (new Matrix4f()).setOrtho(0.0F, width, this.invertY ? height : 0.0F, this.invertY ? 0.0F : height, this.zNear, this.zFar, RenderSystem.getDevice().isZZeroToOne());
   }

   public void close() {
      this.buffer.close();
   }
}
