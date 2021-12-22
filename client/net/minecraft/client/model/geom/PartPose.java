package net.minecraft.client.model.geom;

public class PartPose {
   public static final PartPose ZERO = offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   // $FF: renamed from: x float
   public final float field_262;
   // $FF: renamed from: y float
   public final float field_263;
   // $FF: renamed from: z float
   public final float field_264;
   public final float xRot;
   public final float yRot;
   public final float zRot;

   private PartPose(float var1, float var2, float var3, float var4, float var5, float var6) {
      super();
      this.field_262 = var1;
      this.field_263 = var2;
      this.field_264 = var3;
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
