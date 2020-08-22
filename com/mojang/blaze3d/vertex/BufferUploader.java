package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class BufferUploader {
   public static void end(BufferBuilder var0) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            Pair var1 = var0.popNextBuffer();
            BufferBuilder.DrawState var2 = (BufferBuilder.DrawState)var1.getFirst();
            _end((ByteBuffer)var1.getSecond(), var2.mode(), var2.format(), var2.vertexCount());
         });
      } else {
         Pair var1 = var0.popNextBuffer();
         BufferBuilder.DrawState var2 = (BufferBuilder.DrawState)var1.getFirst();
         _end((ByteBuffer)var1.getSecond(), var2.mode(), var2.format(), var2.vertexCount());
      }

   }

   private static void _end(ByteBuffer var0, int var1, VertexFormat var2, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      var0.clear();
      if (var3 > 0) {
         var2.setupBufferState(MemoryUtil.memAddress(var0));
         GlStateManager._drawArrays(var1, 0, var3);
         var2.clearBufferState();
      }
   }
}
