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

   public CachedPerspectiveProjectionMatrixBuffer(String var1, float var2, float var3) {
      super();
      this.zNear = var2;
      this.zFar = var3;
      GpuDevice var4 = RenderSystem.getDevice();
      this.buffer = var4.createBuffer(() -> "Projection matrix UBO " + var1, 136, 64);
      this.bufferSlice = this.buffer.slice(0, 64);
   }

   public GpuBufferSlice getBuffer(int var1, int var2, float var3) {
      if (this.width != var1 || this.height != var2 || this.fov != var3) {
         Matrix4f var4 = this.createProjectionMatrix(var1, var2, var3);
         MemoryStack var5 = MemoryStack.stackPush();

         try {
            ByteBuffer var6 = Std140Builder.onStack(var5, 64).putMat4f(var4).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var6);
         } catch (Throwable var9) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var5 != null) {
            var5.close();
         }

         this.width = var1;
         this.height = var2;
         this.fov = var3;
      }

      return this.bufferSlice;
   }

   private Matrix4f createProjectionMatrix(int var1, int var2, float var3) {
      return (new Matrix4f()).perspective(var3 * 0.017453292F, (float)var1 / (float)var2, this.zNear, this.zFar);
   }

   public void close() {
      this.buffer.close();
   }
}
