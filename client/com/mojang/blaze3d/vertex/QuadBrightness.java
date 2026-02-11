package com.mojang.blaze3d.vertex;

import net.minecraft.util.ARGB;

public class QuadBrightness {
   public static final QuadBrightness ALL_BRIGHT = new QuadBrightness(1.0F, 1.0F, 1.0F, 1.0F);
   protected float value0;
   protected float value1;
   protected float value2;
   protected float value3;

   private QuadBrightness(final float value0, final float value1, final float value2, final float value3) {
      super();
      this.value0 = value0;
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
   }

   public float get(final int vertex) {
      float var10000;
      switch (vertex) {
         case 0 -> var10000 = this.value0;
         case 1 -> var10000 = this.value1;
         case 2 -> var10000 = this.value2;
         case 3 -> var10000 = this.value3;
         default -> throw new IndexOutOfBoundsException();
      }

      return var10000;
   }

   public int scaleColor(final int vertex, final int baseColor) {
      return ARGB.scaleRGB(baseColor, this.get(vertex));
   }

   public static class Mutable extends QuadBrightness {
      public Mutable() {
         super(0.0F, 0.0F, 0.0F, 0.0F);
      }

      public void set(final int vertex, final float value) {
         switch (vertex) {
            case 0 -> this.value0 = value;
            case 1 -> this.value1 = value;
            case 2 -> this.value2 = value;
            case 3 -> this.value3 = value;
            default -> throw new IndexOutOfBoundsException();
         }

      }

      public void setAll(final float value) {
         this.value0 = value;
         this.value1 = value;
         this.value2 = value;
         this.value3 = value;
      }

      public void multiplyAll(final float value) {
         this.value0 *= value;
         this.value1 *= value;
         this.value2 *= value;
         this.value3 *= value;
      }
   }
}
