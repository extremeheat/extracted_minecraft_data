package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public interface CommandEncoder {
   RenderPass createRenderPass(GpuTexture var1, OptionalInt var2);

   RenderPass createRenderPass(GpuTexture var1, OptionalInt var2, @Nullable GpuTexture var3, OptionalDouble var4);

   void clearColorTexture(GpuTexture var1, int var2);

   void clearColorAndDepthTextures(GpuTexture var1, int var2, GpuTexture var3, double var4);

   void clearDepthTexture(GpuTexture var1, double var2);

   void writeToBuffer(GpuBuffer var1, ByteBuffer var2, int var3);

   void resizeBuffer(GpuBuffer var1, int var2);

   GpuBuffer.ReadView readBuffer(GpuBuffer var1);

   GpuBuffer.ReadView readBuffer(GpuBuffer var1, int var2, int var3);

   void writeToTexture(GpuTexture var1, NativeImage var2);

   void writeToTexture(GpuTexture var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void writeToTexture(GpuTexture var1, IntBuffer var2, NativeImage.Format var3, int var4, int var5, int var6, int var7, int var8);

   void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, int var3, Runnable var4, int var5);

   void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, int var3, Runnable var4, int var5, int var6, int var7, int var8, int var9);

   void copyTextureToTexture(GpuTexture var1, GpuTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void presentTexture(GpuTexture var1);
}
