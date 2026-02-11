package com.mojang.blaze3d.vertex;

import net.minecraft.util.LightCoordsUtil;

public class QuadLightmapCoords {
   protected int value0;
   protected int value1;
   protected int value2;
   protected int value3;

   private QuadLightmapCoords(final int value0, final int value1, final int value2, final int value3) {
      super();
      this.value0 = value0;
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
   }

   public static QuadLightmapCoords create(final int value) {
      return new QuadLightmapCoords(value, value, value, value);
   }

   public int get(final int vertex) {
      int var10000;
      switch (vertex) {
         case 0 -> var10000 = this.value0;
         case 1 -> var10000 = this.value1;
         case 2 -> var10000 = this.value2;
         case 3 -> var10000 = this.value3;
         default -> throw new IndexOutOfBoundsException();
      }

      return var10000;
   }

   public int composeWithEmission(final int vertex, final int lightEmission) {
      return LightCoordsUtil.lightCoordsWithEmission(this.get(vertex), lightEmission);
   }

   public static class Mutable extends QuadLightmapCoords {
      public Mutable() {
         super(0, 0, 0, 0);
      }

      public void set(final int vertex, final int value) {
         switch (vertex) {
            case 0 -> this.value0 = value;
            case 1 -> this.value1 = value;
            case 2 -> this.value2 = value;
            case 3 -> this.value3 = value;
            default -> throw new IndexOutOfBoundsException();
         }

      }

      public void setAll(final int value) {
         this.value0 = value;
         this.value1 = value;
         this.value2 = value;
         this.value3 = value;
      }
   }
}
