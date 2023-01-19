package net.minecraft.client.model.geom;

public class PartPose {
   public static final PartPose ZERO = offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public final float x;
   public final float y;
   public final float z;
   public final float xRot;
   public final float yRot;
   public final float zRot;

   private PartPose(float var1, float var2, float var3, float var4, float var5, float var6) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.xRot = var4;
      this.yRot = var5;
      this.zRot = var6;
   }

   public static PartPose offset(float var0, float var1, float var2) {
      return offsetAndRotation(var0, var1, var2, 0.0F, 0.0F, 0.0F);
   }

   public static PartPose rotation(float var0, float var1, float var2) {
      return offsetAndRotation(0.0F, 0.0F, 0.0F, var0, var1, var2);
   }

   public static PartPose offsetAndRotation(float var0, float var1, float var2, float var3, float var4, float var5) {
      return new PartPose(var0, var1, var2, var3, var4, var5);
   }
}
