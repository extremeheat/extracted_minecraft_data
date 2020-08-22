package com.mojang.blaze3d.vertex;

public class VertexMultiConsumer {
   public static VertexConsumer create(VertexConsumer var0, VertexConsumer var1) {
      return new VertexMultiConsumer.Double(var0, var1);
   }

   static class Double implements VertexConsumer {
      private final VertexConsumer first;
      private final VertexConsumer second;

      public Double(VertexConsumer var1, VertexConsumer var2) {
         if (var1 == var2) {
            throw new IllegalArgumentException("Duplicate delegates");
         } else {
            this.first = var1;
            this.second = var2;
         }
      }

      public VertexConsumer vertex(double var1, double var3, double var5) {
         this.first.vertex(var1, var3, var5);
         this.second.vertex(var1, var3, var5);
         return this;
      }

      public VertexConsumer color(int var1, int var2, int var3, int var4) {
         this.first.color(var1, var2, var3, var4);
         this.second.color(var1, var2, var3, var4);
         return this;
      }

      public VertexConsumer uv(float var1, float var2) {
         this.first.uv(var1, var2);
         this.second.uv(var1, var2);
         return this;
      }

      public VertexConsumer overlayCoords(int var1, int var2) {
         this.first.overlayCoords(var1, var2);
         this.second.overlayCoords(var1, var2);
         return this;
      }

      public VertexConsumer uv2(int var1, int var2) {
         this.first.uv2(var1, var2);
         this.second.uv2(var1, var2);
         return this;
      }

      public VertexConsumer normal(float var1, float var2, float var3) {
         this.first.normal(var1, var2, var3);
         this.second.normal(var1, var2, var3);
         return this;
      }

      public void vertex(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11, float var12, float var13, float var14) {
         this.first.vertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
         this.second.vertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }

      public void endVertex() {
         this.first.endVertex();
         this.second.endVertex();
      }
   }
}
