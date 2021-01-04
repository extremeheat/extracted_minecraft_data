package com.mojang.blaze3d.vertex;

public class VertexBufferUploader extends BufferUploader {
   private VertexBuffer buffer;

   public VertexBufferUploader() {
      super();
   }

   public void end(BufferBuilder var1) {
      var1.clear();
      this.buffer.upload(var1.getBuffer());
   }

   public void setBuffer(VertexBuffer var1) {
      this.buffer = var1;
   }
}
