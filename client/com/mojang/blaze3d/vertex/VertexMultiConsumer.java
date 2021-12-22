package com.mojang.blaze3d.vertex;

import java.util.function.Consumer;

public class VertexMultiConsumer {
   public VertexMultiConsumer() {
      super();
   }

   public static VertexConsumer create() {
      throw new IllegalArgumentException();
   }

   public static VertexConsumer create(VertexConsumer var0) {
      return var0;
   }

   public static VertexConsumer create(VertexConsumer var0, VertexConsumer var1) {
      return new VertexMultiConsumer.Double(var0, var1);
   }

   public static VertexConsumer create(VertexConsumer... var0) {
      return new VertexMultiConsumer.Multiple(var0);
   }

   static class Double implements VertexConsumer {
      private final VertexConsumer first;
      private final VertexConsumer second;

      public Double(VertexConsumer var1, VertexConsumer var2) {
         super();
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

      // $FF: renamed from: uv (float, float) com.mojang.blaze3d.vertex.VertexConsumer
      public VertexConsumer method_7(float var1, float var2) {
         this.first.method_7(var1, var2);
         this.second.method_7(var1, var2);
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

      public void defaultColor(int var1, int var2, int var3, int var4) {
         this.first.defaultColor(var1, var2, var3, var4);
         this.second.defaultColor(var1, var2, var3, var4);
      }

      public void unsetDefaultColor() {
         this.first.unsetDefaultColor();
         this.second.unsetDefaultColor();
      }
   }

   private static class Multiple implements VertexConsumer {
      private final VertexConsumer[] delegates;

      public Multiple(VertexConsumer[] var1) {
         super();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            for(int var3 = var2 + 1; var3 < var1.length; ++var3) {
               if (var1[var2] == var1[var3]) {
                  throw new IllegalArgumentException("Duplicate delegates");
               }
            }
         }

         this.delegates = var1;
      }

      private void forEach(Consumer<VertexConsumer> var1) {
         VertexConsumer[] var2 = this.delegates;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            VertexConsumer var5 = var2[var4];
            var1.accept(var5);
         }

      }

      public VertexConsumer vertex(double var1, double var3, double var5) {
         this.forEach((var6) -> {
            var6.vertex(var1, var3, var5);
         });
         return this;
      }

      public VertexConsumer color(int var1, int var2, int var3, int var4) {
         this.forEach((var4x) -> {
            var4x.color(var1, var2, var3, var4);
         });
         return this;
      }

      // $FF: renamed from: uv (float, float) com.mojang.blaze3d.vertex.VertexConsumer
      public VertexConsumer method_7(float var1, float var2) {
         this.forEach((var2x) -> {
            var2x.method_7(var1, var2);
         });
         return this;
      }

      public VertexConsumer overlayCoords(int var1, int var2) {
         this.forEach((var2x) -> {
            var2x.overlayCoords(var1, var2);
         });
         return this;
      }

      public VertexConsumer uv2(int var1, int var2) {
         this.forEach((var2x) -> {
            var2x.uv2(var1, var2);
         });
         return this;
      }

      public VertexConsumer normal(float var1, float var2, float var3) {
         this.forEach((var3x) -> {
            var3x.normal(var1, var2, var3);
         });
         return this;
      }

      public void vertex(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11, float var12, float var13, float var14) {
         this.forEach((var14x) -> {
            var14x.vertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
         });
      }

      public void endVertex() {
         this.forEach(VertexConsumer::endVertex);
      }

      public void defaultColor(int var1, int var2, int var3, int var4) {
         this.forEach((var4x) -> {
            var4x.defaultColor(var1, var2, var3, var4);
         });
      }

      public void unsetDefaultColor() {
         this.forEach(VertexConsumer::unsetDefaultColor);
      }
   }
}
