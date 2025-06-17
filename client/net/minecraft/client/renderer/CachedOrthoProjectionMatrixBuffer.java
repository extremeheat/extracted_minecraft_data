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

   public CachedOrthoProjectionMatrixBuffer(String var1, float var2, float var3, boolean var4) {
      super();
      this.zNear = var2;
      this.zFar = var3;
      this.invertY = var4;
      GpuDevice var5 = RenderSystem.getDevice();
      this.buffer = var5.createBuffer(() -> "Projection matrix UBO " + var1, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.bufferSlice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice getBuffer(float var1, float var2) {
      if (this.width != var1 || this.height != var2) {
         Matrix4f var3 = this.createProjectionMatrix(var1, var2);
         MemoryStack var4 = MemoryStack.stackPush();

         try {
            ByteBuffer var5 = Std140Builder.onStack(var4, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(var3).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var5);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            var4.close();
         }

         this.width = var1;
         this.height = var2;
      }

      return this.bufferSlice;
   }

   private Matrix4f createProjectionMatrix(float var1, float var2) {
      return (new Matrix4f()).setOrtho(0.0F, var1, this.invertY ? var2 : 0.0F, this.invertY ? 0.0F : var2, this.zNear, this.zFar);
   }

   public void close() {
      this.buffer.close();
   }
}
