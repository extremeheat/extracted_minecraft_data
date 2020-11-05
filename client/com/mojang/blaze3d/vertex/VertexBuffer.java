package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class VertexBuffer implements AutoCloseable {
   private int id;
   private int indexBufferId;
   private VertexFormat.IndexType indexType;
   private int indexCount;
   private VertexFormat.Mode mode;
   private boolean sequentialIndices;

   public VertexBuffer() {
      super();
      RenderSystem.glGenBuffers((var1) -> {
         this.id = var1;
      });
      RenderSystem.glGenBuffers((var1) -> {
         this.indexBufferId = var1;
      });
   }

   public void bind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.id;
      });
      if (this.sequentialIndices) {
         RenderSystem.glBindBuffer(34963, () -> {
            RenderSystem.AutoStorageIndexBuffer var1 = RenderSystem.getSequentialBuffer(this.mode, this.indexCount);
            this.indexType = var1.type();
            return var1.name();
         });
      } else {
         RenderSystem.glBindBuffer(34963, () -> {
            return this.indexBufferId;
         });
      }

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
         BufferBuilder.DrawState var3 = (BufferBuilder.DrawState)var2.getFirst();
         ByteBuffer var4 = (ByteBuffer)var2.getSecond();
         int var5 = var3.vertexBufferSize();
         this.indexCount = var3.indexCount();
         this.indexType = var3.indexType();
         this.mode = var3.mode();
         this.sequentialIndices = var3.sequentialIndex();
         this.bind();
         if (!var3.indexOnly()) {
            var4.limit(var5);
            RenderSystem.glBufferData(34962, var4, 35044);
            var4.position(var5);
         }

         if (!this.sequentialIndices) {
            var4.limit(var3.bufferSize());
            RenderSystem.glBufferData(34963, var4, 35044);
            var4.position(0);
         } else {
            var4.limit(var3.bufferSize());
            var4.position(0);
         }

         unbind();
      }
   }

   public void draw(Matrix4f var1) {
      if (this.indexCount != 0) {
         RenderSystem.pushMatrix();
         RenderSystem.loadIdentity();
         RenderSystem.multMatrix(var1);
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
         RenderSystem.popMatrix();
      }
   }

   public static void unbind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
      RenderSystem.glBindBuffer(34963, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.id >= 0) {
         RenderSystem.glDeleteBuffers(this.id);
         this.id = -1;
      }

      if (this.indexBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.indexBufferId);
         this.indexBufferId = -1;
      }

   }
}
