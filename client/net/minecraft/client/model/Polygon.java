package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.phys.Vec3;

public class Polygon {
   public Vertex[] vertices;
   public final int vertexCount;
   private boolean flipNormal;

   public Polygon(Vertex[] var1) {
      super();
      this.vertices = var1;
      this.vertexCount = var1.length;
   }

   public Polygon(Vertex[] var1, int var2, int var3, int var4, int var5, float var6, float var7) {
      this(var1);
      float var8 = 0.0F / var6;
      float var9 = 0.0F / var7;
      var1[0] = var1[0].remap((float)var4 / var6 - var8, (float)var3 / var7 + var9);
      var1[1] = var1[1].remap((float)var2 / var6 + var8, (float)var3 / var7 + var9);
      var1[2] = var1[2].remap((float)var2 / var6 + var8, (float)var5 / var7 - var9);
      var1[3] = var1[3].remap((float)var4 / var6 - var8, (float)var5 / var7 - var9);
   }

   public void mirror() {
      Vertex[] var1 = new Vertex[this.vertices.length];

      for(int var2 = 0; var2 < this.vertices.length; ++var2) {
         var1[var2] = this.vertices[this.vertices.length - var2 - 1];
      }

      this.vertices = var1;
   }

   public void render(BufferBuilder var1, float var2) {
      Vec3 var3 = this.vertices[1].pos.vectorTo(this.vertices[0].pos);
      Vec3 var4 = this.vertices[1].pos.vectorTo(this.vertices[2].pos);
      Vec3 var5 = var4.cross(var3).normalize();
      float var6 = (float)var5.x;
      float var7 = (float)var5.y;
      float var8 = (float)var5.z;
      if (this.flipNormal) {
         var6 = -var6;
         var7 = -var7;
         var8 = -var8;
      }

      var1.begin(7, DefaultVertexFormat.ENTITY);

      for(int var9 = 0; var9 < 4; ++var9) {
         Vertex var10 = this.vertices[var9];
         var1.vertex(var10.pos.x * (double)var2, var10.pos.y * (double)var2, var10.pos.z * (double)var2).uv((double)var10.u, (double)var10.v).normal(var6, var7, var8).endVertex();
      }

      Tesselator.getInstance().end();
   }
}
