package com.mojang.blaze3d.vertex;

public abstract class DefaultedVertexConsumer implements VertexConsumer {
   protected boolean defaultColorSet = false;
   protected int defaultR = 255;
   protected int defaultG = 255;
   protected int defaultB = 255;
   protected int defaultA = 255;

   public void defaultColor(int var1, int var2, int var3, int var4) {
      this.defaultR = var1;
      this.defaultG = var2;
      this.defaultB = var3;
      this.defaultA = var4;
      this.defaultColorSet = true;
   }
}
