package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader {
   private VertexBuffer field_178179_a = null;

   public VertexBufferUploader() {
      super();
   }

   public void func_181679_a(WorldRenderer var1) {
      var1.func_178965_a();
      this.field_178179_a.func_181722_a(var1.func_178966_f());
   }

   public void func_178178_a(VertexBuffer var1) {
      this.field_178179_a = var1;
   }
}
