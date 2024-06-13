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

      @Override
      public VertexConsumer addVertex(float var1, float var2, float var3) {
         this.first.addVertex(var1, var2, var3);
         this.second.addVertex(var1, var2, var3);
         return this;
      }

      @Override
      public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
         this.first.setColor(var1, var2, var3, var4);
         this.second.setColor(var1, var2, var3, var4);
         return this;
      }

      @Override
      public VertexConsumer setUv(float var1, float var2) {
         this.first.setUv(var1, var2);
         this.second.setUv(var1, var2);
         return this;
      }

      @Override
      public VertexConsumer setUv1(int var1, int var2) {
         this.first.setUv1(var1, var2);
         this.second.setUv1(var1, var2);
         return this;
      }

      @Override
      public VertexConsumer setUv2(int var1, int var2) {
         this.first.setUv2(var1, var2);
         this.second.setUv2(var1, var2);
         return this;
      }

      @Override
      public VertexConsumer setNormal(float var1, float var2, float var3) {
         this.first.setNormal(var1, var2, var3);
         this.second.setNormal(var1, var2, var3);
         return this;
      }

      @Override
      public void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
         this.first.addVertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         this.second.addVertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   static record Multiple(VertexConsumer[] delegates) implements VertexConsumer {
      Multiple(VertexConsumer[] delegates) {
         super();

         for (int var2 = 0; var2 < delegates.length; var2++) {
            for (int var3 = var2 + 1; var3 < delegates.length; var3++) {
               if (delegates[var2] == delegates[var3]) {
                  throw new IllegalArgumentException("Duplicate delegates");
               }
            }
         }

         this.delegates = delegates;
      }

      private void forEach(Consumer<VertexConsumer> var1) {
         for (VertexConsumer var5 : this.delegates) {
            var1.accept(var5);
         }
      }

      @Override
      public VertexConsumer addVertex(float var1, float var2, float var3) {
         this.forEach(var3x -> var3x.addVertex(var1, var2, var3));
         return this;
      }

      @Override
      public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
         this.forEach(var4x -> var4x.setColor(var1, var2, var3, var4));
         return this;
      }

      @Override
      public VertexConsumer setUv(float var1, float var2) {
         this.forEach(var2x -> var2x.setUv(var1, var2));
         return this;
      }

      @Override
      public VertexConsumer setUv1(int var1, int var2) {
         this.forEach(var2x -> var2x.setUv1(var1, var2));
         return this;
      }

      @Override
      public VertexConsumer setUv2(int var1, int var2) {
         this.forEach(var2x -> var2x.setUv2(var1, var2));
         return this;
      }

      @Override
      public VertexConsumer setNormal(float var1, float var2, float var3) {
         this.forEach(var3x -> var3x.setNormal(var1, var2, var3));
         return this;
      }

      @Override
      public void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
         this.forEach(var11x -> var11x.addVertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11));
      }
   }
}
