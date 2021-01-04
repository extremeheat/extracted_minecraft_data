package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;

public class VertexBuffer {
   private int id;
   private final VertexFormat format;
   private int vertexCount;

   public VertexBuffer(VertexFormat var1) {
      super();
      this.format = var1;
      this.id = GLX.glGenBuffers();
   }

   public void bind() {
      GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.id);
   }

   public void upload(ByteBuffer var1) {
      this.bind();
      GLX.glBufferData(GLX.GL_ARRAY_BUFFER, var1, 35044);
      unbind();
      this.vertexCount = var1.limit() / this.format.getVertexSize();
   }

   public void draw(int var1) {
      GlStateManager.drawArrays(var1, 0, this.vertexCount);
   }

   public static void unbind() {
      GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, 0);
   }

   public void delete() {
      if (this.id >= 0) {
         GLX.glDeleteBuffers(this.id);
         this.id = -1;
      }

   }
}
