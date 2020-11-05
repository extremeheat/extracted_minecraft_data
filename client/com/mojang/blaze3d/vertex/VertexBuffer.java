package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class VertexBuffer implements AutoCloseable {
   private int id;
   private final VertexFormat format;
   private int vertexCount;

   public VertexBuffer(VertexFormat var1) {
      super();
      this.format = var1;
      RenderSystem.glGenBuffers((var1x) -> {
         this.id = var1x;
      });
   }

   public void bind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.id;
      });
   }

   public void upload(BufferBuilder var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload_(var1);
         });
      } else {
         this.upload_(var1);
      }

   }

   public CompletableFuture<Void> uploadLater(BufferBuilder var1) {
      if (!RenderSystem.isOnRenderThread()) {
         return CompletableFuture.runAsync(() -> {
            this.upload_(var1);
         }, (var0) -> {
            RenderSystem.recordRenderCall(var0::run);
         });
      } else {
         this.upload_(var1);
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   private void upload_(BufferBuilder var1) {
      Pair var2 = var1.popNextBuffer();
      if (this.id != -1) {
         ByteBuffer var3 = (ByteBuffer)var2.getSecond();
         this.vertexCount = var3.remaining() / this.format.getVertexSize();
         this.bind();
         RenderSystem.glBufferData(34962, var3, 35044);
         unbind();
      }
   }

   public void draw(Matrix4f var1, int var2) {
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(var1);
      RenderSystem.drawArrays(var2, 0, this.vertexCount);
      RenderSystem.popMatrix();
   }

   public static void unbind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.id >= 0) {
         RenderSystem.glDeleteBuffers(this.id);
         this.id = -1;
      }

   }
}
