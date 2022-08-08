package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;

public class BufferUploader {
   @Nullable
   private static VertexBuffer lastImmediateBuffer;

   public BufferUploader() {
      super();
   }

   public static void reset() {
      if (lastImmediateBuffer != null) {
         invalidate();
         VertexBuffer.unbind();
      }

   }

   public static void invalidate() {
      lastImmediateBuffer = null;
   }

   public static void drawWithShader(BufferBuilder.RenderedBuffer var0) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            _drawWithShader(var0);
         });
      } else {
         _drawWithShader(var0);
      }

   }

   private static void _drawWithShader(BufferBuilder.RenderedBuffer var0) {
      VertexBuffer var1 = upload(var0);
      if (var1 != null) {
         var1.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
      }

   }

   public static void draw(BufferBuilder.RenderedBuffer var0) {
      VertexBuffer var1 = upload(var0);
      if (var1 != null) {
         var1.draw();
      }

   }

   @Nullable
   private static VertexBuffer upload(BufferBuilder.RenderedBuffer var0) {
      RenderSystem.assertOnRenderThread();
      if (var0.isEmpty()) {
         var0.release();
         return null;
      } else {
         VertexBuffer var1 = bindImmediateBuffer(var0.drawState().format());
         var1.upload(var0);
         return var1;
      }
   }

   private static VertexBuffer bindImmediateBuffer(VertexFormat var0) {
      VertexBuffer var1 = var0.getImmediateDrawVertexBuffer();
      bindImmediateBuffer(var1);
      return var1;
   }

   private static void bindImmediateBuffer(VertexBuffer var0) {
      if (var0 != lastImmediateBuffer) {
         var0.bind();
         lastImmediateBuffer = var0;
      }

   }
}
