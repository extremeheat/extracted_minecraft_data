package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.model.Polygon;
import net.minecraft.client.model.Vertex;

public class Cube {
   private final Vertex[] vertices;
   private final Polygon[] polygons;
   public final float minX;
   public final float minY;
   public final float minZ;
   public final float maxX;
   public final float maxY;
   public final float maxZ;
   public String id;

   public Cube(ModelPart var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var1.mirror);
   }

   public Cube(ModelPart var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10, boolean var11) {
      super();
      this.minX = var4;
      this.minY = var5;
      this.minZ = var6;
      this.maxX = var4 + (float)var7;
      this.maxY = var5 + (float)var8;
      this.maxZ = var6 + (float)var9;
      this.vertices = new Vertex[8];
      this.polygons = new Polygon[6];
      float var12 = var4 + (float)var7;
      float var13 = var5 + (float)var8;
      float var14 = var6 + (float)var9;
      var4 -= var10;
      var5 -= var10;
      var6 -= var10;
      var12 += var10;
      var13 += var10;
      var14 += var10;
      if (var11) {
         float var15 = var12;
         var12 = var4;
         var4 = var15;
      }

      Vertex var27 = new Vertex(var4, var5, var6, 0.0F, 0.0F);
      Vertex var16 = new Vertex(var12, var5, var6, 0.0F, 8.0F);
      Vertex var17 = new Vertex(var12, var13, var6, 8.0F, 8.0F);
      Vertex var18 = new Vertex(var4, var13, var6, 8.0F, 0.0F);
      Vertex var19 = new Vertex(var4, var5, var14, 0.0F, 0.0F);
      Vertex var20 = new Vertex(var12, var5, var14, 0.0F, 8.0F);
      Vertex var21 = new Vertex(var12, var13, var14, 8.0F, 8.0F);
      Vertex var22 = new Vertex(var4, var13, var14, 8.0F, 0.0F);
      this.vertices[0] = var27;
      this.vertices[1] = var16;
      this.vertices[2] = var17;
      this.vertices[3] = var18;
      this.vertices[4] = var19;
      this.vertices[5] = var20;
      this.vertices[6] = var21;
      this.vertices[7] = var22;
      this.polygons[0] = new Polygon(new Vertex[]{var20, var16, var17, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var9, var3 + var9 + var8, var1.xTexSize, var1.yTexSize);
      this.polygons[1] = new Polygon(new Vertex[]{var27, var19, var22, var18}, var2, var3 + var9, var2 + var9, var3 + var9 + var8, var1.xTexSize, var1.yTexSize);
      this.polygons[2] = new Polygon(new Vertex[]{var20, var19, var27, var16}, var2 + var9, var3, var2 + var9 + var7, var3 + var9, var1.xTexSize, var1.yTexSize);
      this.polygons[3] = new Polygon(new Vertex[]{var17, var18, var22, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var7, var3, var1.xTexSize, var1.yTexSize);
      this.polygons[4] = new Polygon(new Vertex[]{var16, var27, var18, var17}, var2 + var9, var3 + var9, var2 + var9 + var7, var3 + var9 + var8, var1.xTexSize, var1.yTexSize);
      this.polygons[5] = new Polygon(new Vertex[]{var19, var20, var21, var22}, var2 + var9 + var7 + var9, var3 + var9, var2 + var9 + var7 + var9 + var7, var3 + var9 + var8, var1.xTexSize, var1.yTexSize);
      if (var11) {
         Polygon[] var23 = this.polygons;
         int var24 = var23.length;

         for(int var25 = 0; var25 < var24; ++var25) {
            Polygon var26 = var23[var25];
            var26.mirror();
         }
      }

   }

   public void compile(BufferBuilder var1, float var2) {
      Polygon[] var3 = this.polygons;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Polygon var6 = var3[var5];
         var6.render(var1, var2);
      }

   }

   public Cube setId(String var1) {
      this.id = var1;
      return this;
   }
}
