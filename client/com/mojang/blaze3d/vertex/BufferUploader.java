package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;

public class BufferUploader {
   private static int vertexBufferObject;
   private static int indexBufferObject;

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
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      var0.clear();
      if (var3 > 0) {
         if (vertexBufferObject == 0) {
            vertexBufferObject = GlStateManager._glGenBuffers();
         }

         int var7 = var3 * var2.getVertexSize();
         GlStateManager._glBindBuffer(34962, vertexBufferObject);
         var0.position(0);
         var0.limit(var7);
         GlStateManager._glBufferData(34962, var0, 35044);
         int var8;
         if (var6) {
            RenderSystem.AutoStorageIndexBuffer var9 = RenderSystem.getSequentialBuffer(var1, var5);
            GlStateManager._glBindBuffer(34963, var9.name());
            var8 = var9.type().asGLType;
         } else {
            if (indexBufferObject == 0) {
               indexBufferObject = GlStateManager._glGenBuffers();
            }

            GlStateManager._glBindBuffer(34963, indexBufferObject);
            var0.position(var7);
            var0.limit(var7 + var5 * var4.bytes);
            GlStateManager._glBufferData(34963, var0, 35044);
            var8 = var4.asGLType;
         }

         var2.setupBufferState(0L);
         GlStateManager._drawElements(var1.asGLMode, var5, var8, 0L);
         var2.clearBufferState();
         var0.position(0);
         GlStateManager._glBindBuffer(34963, 0);
         GlStateManager._glBindBuffer(34962, 0);
      }
   }
}
