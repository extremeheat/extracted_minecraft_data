package net.minecraft.client.model.geom.builders;

public class CubeDeformation {
   public static final CubeDeformation NONE = new CubeDeformation(0.0F);
   final float growX;
   final float growY;
   final float growZ;

   public CubeDeformation(float var1, float var2, float var3) {
      super();
      this.growX = var1;
      this.growY = var2;
      this.growZ = var3;
   }

   public CubeDeformation(float var1) {
      this(var1, var1, var1);
   }

   public CubeDeformation extend(float var1) {
      return new CubeDeformation(this.growX + var1, this.growY + var1, this.growZ + var1);
   }

   public CubeDeformation extend(float var1, float var2, float var3) {
      return new CubeDeformation(this.growX + var1, this.growY + var2, this.growZ + var3);
   }
}
