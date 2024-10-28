package net.minecraft.client.model.geom;

public record PartPose(float x, float y, float z, float xRot, float yRot, float zRot, float xScale, float yScale, float zScale) {
   public static final PartPose ZERO = offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

   public PartPose(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.xRot = var4;
      this.yRot = var5;
      this.zRot = var6;
      this.xScale = var7;
      this.yScale = var8;
      this.zScale = var9;
   }

   public static PartPose offset(float var0, float var1, float var2) {
      return offsetAndRotation(var0, var1, var2, 0.0F, 0.0F, 0.0F);
   }

   public static PartPose rotation(float var0, float var1, float var2) {
      return offsetAndRotation(0.0F, 0.0F, 0.0F, var0, var1, var2);
   }

   public static PartPose offsetAndRotation(float var0, float var1, float var2, float var3, float var4, float var5) {
      return new PartPose(var0, var1, var2, var3, var4, var5, 1.0F, 1.0F, 1.0F);
   }

   public PartPose translated(float var1, float var2, float var3) {
      return new PartPose(this.x + var1, this.y + var2, this.z + var3, this.xRot, this.yRot, this.zRot, this.xScale, this.yScale, this.zScale);
   }

   public PartPose withScale(float var1) {
      return new PartPose(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot, var1, var1, var1);
   }

   public PartPose scaled(float var1) {
      return var1 == 1.0F ? this : this.scaled(var1, var1, var1);
   }

   public PartPose scaled(float var1, float var2, float var3) {
      return new PartPose(this.x * var1, this.y * var2, this.z * var3, this.xRot, this.yRot, this.zRot, this.xScale * var1, this.yScale * var2, this.zScale * var3);
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public float xRot() {
      return this.xRot;
   }

   public float yRot() {
      return this.yRot;
   }

   public float zRot() {
      return this.zRot;
   }

   public float xScale() {
      return this.xScale;
   }

   public float yScale() {
      return this.yScale;
   }

   public float zScale() {
      return this.zScale;
   }
}
