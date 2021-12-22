package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public class BufferUploader {
   private static int lastVertexArrayObject;
   private static int lastVertexBufferObject;
   private static int lastIndexBufferObject;
   @Nullable
   private static VertexFormat lastFormat;

   public BufferUploader() {
      super();
   }

   public static void reset() {
      if (lastFormat != null) {
         lastFormat.clearBufferState();
         lastFormat = null;
      }

      GlStateManager._glBindBuffer(34963, 0);
      lastIndexBufferObject = 0;
      GlStateManager._glBindBuffer(34962, 0);
      lastVertexBufferObject = 0;
      GlStateManager._glBindVertexArray(0);
      lastVertexArrayObject = 0;
   }

   public static void invalidateElementArrayBufferBinding() {
      GlStateManager._glBindBuffer(34963, 0);
      lastIndexBufferObject = 0;
   }

   public static void end(BufferBuilder var0) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            Pair var1 = var0.popNextBuffer();
            BufferBuilder.DrawState var2 = (BufferBuilder.DrawState)var1.getFirst();
            _end((ByteBuffer)var1.getSecond(), var2.mode(), var2.format(), var2.vertexCount(), var2.indexType(), var2.indexCount(), var2.sequentialIndex());
         });
      } else {
         Pair var1 = var0.popNextBuffer();
         BufferBuilder.DrawState var2 = (BufferBuilder.DrawState)var1.getFirst();
         _end((ByteBuffer)var1.getSecond(), var2.mode(), var2.format(), var2.vertexCount(), var2.indexType(), var2.indexCount(), var2.sequentialIndex());
      }

   }

   private static void _end(ByteBuffer var0, VertexFormat.Mode var1, VertexFormat var2, int var3, VertexFormat.IndexType var4, int var5, boolean var6) {
      RenderSystem.assertOnRenderThread();
      var0.clear();
      if (var3 > 0) {
         int var7 = var3 * var2.getVertexSize();
         updateVertexSetup(var2);
         var0.position(0);
         var0.limit(var7);
         GlStateManager._glBufferData(34962, var0, 35048);
         int var8;
         int var10;
         if (var6) {
            RenderSystem.AutoStorageIndexBuffer var9 = RenderSystem.getSequentialBuffer(var1, var5);
            var10 = var9.name();
            if (var10 != lastIndexBufferObject) {
               GlStateManager._glBindBuffer(34963, var10);
               lastIndexBufferObject = var10;
            }

            var8 = var9.type().asGLType;
         } else {
            int var12 = var2.getOrCreateIndexBufferObject();
            if (var12 != lastIndexBufferObject) {
               GlStateManager._glBindBuffer(34963, var12);
               lastIndexBufferObject = var12;
            }

            var0.position(var7);
            var0.limit(var7 + var5 * var4.bytes);
            GlStateManager._glBufferData(34963, var0, 35048);
            var8 = var4.asGLType;
         }

         ShaderInstance var13 = RenderSystem.getShader();

         for(var10 = 0; var10 < 8; ++var10) {
            int var11 = RenderSystem.getShaderTexture(var10);
            var13.setSampler("Sampler" + var10, var11);
         }

         if (var13.MODEL_VIEW_MATRIX != null) {
            var13.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
         }

         if (var13.PROJECTION_MATRIX != null) {
            var13.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
         }

         if (var13.INVERSE_VIEW_ROTATION_MATRIX != null) {
            var13.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
         }

         if (var13.COLOR_MODULATOR != null) {
            var13.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
         }

         if (var13.FOG_START != null) {
            var13.FOG_START.set(RenderSystem.getShaderFogStart());
         }

         if (var13.FOG_END != null) {
            var13.FOG_END.set(RenderSystem.getShaderFogEnd());
         }

         if (var13.FOG_COLOR != null) {
            var13.FOG_COLOR.set(RenderSystem.getShaderFogColor());
         }

         if (var13.TEXTURE_MATRIX != null) {
            var13.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
         }

         if (var13.GAME_TIME != null) {
            var13.GAME_TIME.set(RenderSystem.getShaderGameTime());
         }

         if (var13.SCREEN_SIZE != null) {
            Window var14 = Minecraft.getInstance().getWindow();
            var13.SCREEN_SIZE.set((float)var14.getWidth(), (float)var14.getHeight());
         }

         if (var13.LINE_WIDTH != null && (var1 == VertexFormat.Mode.LINES || var1 == VertexFormat.Mode.LINE_STRIP)) {
            var13.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
         }

         RenderSystem.setupShaderLights(var13);
         var13.apply();
         GlStateManager._drawElements(var1.asGLMode, var5, var8, 0L);
         var13.clear();
         var0.position(0);
      }
   }

   public static void _endInternal(BufferBuilder var0) {
      RenderSystem.assertOnRenderThread();
      Pair var1 = var0.popNextBuffer();
      BufferBuilder.DrawState var2 = (BufferBuilder.DrawState)var1.getFirst();
      ByteBuffer var3 = (ByteBuffer)var1.getSecond();
      VertexFormat var4 = var2.format();
      int var5 = var2.vertexCount();
      var3.clear();
      if (var5 > 0) {
         int var6 = var5 * var4.getVertexSize();
         updateVertexSetup(var4);
         var3.position(0);
         var3.limit(var6);
         GlStateManager._glBufferData(34962, var3, 35048);
         RenderSystem.AutoStorageIndexBuffer var7 = RenderSystem.getSequentialBuffer(var2.mode(), var2.indexCount());
         int var8 = var7.name();
         if (var8 != lastIndexBufferObject) {
            GlStateManager._glBindBuffer(34963, var8);
            lastIndexBufferObject = var8;
         }

         int var9 = var7.type().asGLType;
         GlStateManager._drawElements(var2.mode().asGLMode, var2.indexCount(), var9, 0L);
         var3.position(0);
      }
   }

   private static void updateVertexSetup(VertexFormat var0) {
      int var1 = var0.getOrCreateVertexArrayObject();
      int var2 = var0.getOrCreateVertexBufferObject();
      boolean var3 = var0 != lastFormat;
      if (var3) {
         reset();
      }

      if (var1 != lastVertexArrayObject) {
         GlStateManager._glBindVertexArray(var1);
         lastVertexArrayObject = var1;
      }

      if (var2 != lastVertexBufferObject) {
         GlStateManager._glBindBuffer(34962, var2);
         lastVertexBufferObject = var2;
      }

      if (var3) {
         var0.setupBufferState();
         lastFormat = var0;
      }

   }
}
