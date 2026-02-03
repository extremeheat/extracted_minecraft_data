package com.mojang.blaze3d.vertex;

import java.util.function.Consumer;

public class VertexMultiConsumer {
   public VertexMultiConsumer() {
      super();
   }

   public static VertexConsumer create() {
      throw new IllegalArgumentException();
   }

   public static VertexConsumer create(final VertexConsumer consumer) {
      return consumer;
   }

   public static VertexConsumer create(final VertexConsumer first, final VertexConsumer second) {
      return new Double(first, second);
   }

   public static VertexConsumer create(final VertexConsumer... consumers) {
      return new Multiple(consumers);
   }

   private static class Double implements VertexConsumer {
      private final VertexConsumer first;
      private final VertexConsumer second;

      public Double(final VertexConsumer first, final VertexConsumer second) {
         super();
         if (first == second) {
            throw new IllegalArgumentException("Duplicate delegates");
         } else {
            this.first = first;
            this.second = second;
         }
      }

      public VertexConsumer addVertex(final float x, final float y, final float z) {
         this.first.addVertex(x, y, z);
         this.second.addVertex(x, y, z);
         return this;
      }

      public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
         this.first.setColor(r, g, b, a);
         this.second.setColor(r, g, b, a);
         return this;
      }

      public VertexConsumer setColor(final int color) {
         this.first.setColor(color);
         this.second.setColor(color);
         return this;
      }

      public VertexConsumer setUv(final float u, final float v) {
         this.first.setUv(u, v);
         this.second.setUv(u, v);
         return this;
      }

      public VertexConsumer setUv1(final int u, final int v) {
         this.first.setUv1(u, v);
         this.second.setUv1(u, v);
         return this;
      }

      public VertexConsumer setUv2(final int u, final int v) {
         this.first.setUv2(u, v);
         this.second.setUv2(u, v);
         return this;
      }

      public VertexConsumer setNormal(final float x, final float y, final float z) {
         this.first.setNormal(x, y, z);
         this.second.setNormal(x, y, z);
         return this;
      }

      public VertexConsumer setLineWidth(final float width) {
         this.first.setLineWidth(width);
         this.second.setLineWidth(width);
         return this;
      }

      public void addVertex(final float x, final float y, final float z, final int color, final float u, final float v, final int overlayCoords, final int lightCoords, final float nx, final float ny, final float nz) {
         this.first.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz);
         this.second.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz);
      }
   }

   private static record Multiple(VertexConsumer[] delegates) implements VertexConsumer {
      private Multiple(VertexConsumer[] delegates) {
         super();

         for(int i = 0; i < delegates.length; ++i) {
            for(int j = i + 1; j < delegates.length; ++j) {
               if (delegates[i] == delegates[j]) {
                  throw new IllegalArgumentException("Duplicate delegates");
               }
            }
         }

         this.delegates = delegates;
      }

      private void forEach(final Consumer<VertexConsumer> out) {
         for(VertexConsumer delegate : this.delegates) {
            out.accept(delegate);
         }

      }

      public VertexConsumer addVertex(final float x, final float y, final float z) {
         this.forEach((d) -> d.addVertex(x, y, z));
         return this;
      }

      public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
         this.forEach((d) -> d.setColor(r, g, b, a));
         return this;
      }

      public VertexConsumer setColor(final int color) {
         this.forEach((d) -> d.setColor(color));
         return this;
      }

      public VertexConsumer setUv(final float u, final float v) {
         this.forEach((d) -> d.setUv(u, v));
         return this;
      }

      public VertexConsumer setUv1(final int u, final int v) {
         this.forEach((d) -> d.setUv1(u, v));
         return this;
      }

      public VertexConsumer setUv2(final int u, final int v) {
         this.forEach((d) -> d.setUv2(u, v));
         return this;
      }

      public VertexConsumer setNormal(final float x, final float y, final float z) {
         this.forEach((d) -> d.setNormal(x, y, z));
         return this;
      }

      public VertexConsumer setLineWidth(final float width) {
         this.forEach((d) -> d.setLineWidth(width));
         return this;
      }

      public void addVertex(final float x, final float y, final float z, final int color, final float u, final float v, final int overlayCoords, final int lightCoords, final float nx, final float ny, final float nz) {
         this.forEach((d) -> d.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz));
      }
   }
}
