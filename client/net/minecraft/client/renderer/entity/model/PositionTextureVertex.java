package net.minecraft.client.renderer.entity.model;

import net.minecraft.util.math.Vec3d;

public class PositionTextureVertex {
   public Vec3d field_78243_a;
   public float field_78241_b;
   public float field_78242_c;

   public PositionTextureVertex(float var1, float var2, float var3, float var4, float var5) {
      this(new Vec3d((double)var1, (double)var2, (double)var3), var4, var5);
   }

   public PositionTextureVertex func_78240_a(float var1, float var2) {
      return new PositionTextureVertex(this, var1, var2);
   }

   public PositionTextureVertex(PositionTextureVertex var1, float var2, float var3) {
      super();
      this.field_78243_a = var1.field_78243_a;
      this.field_78241_b = var2;
      this.field_78242_c = var3;
   }

   public PositionTextureVertex(Vec3d var1, float var2, float var3) {
      super();
      this.field_78243_a = var1;
      this.field_78241_b = var2;
      this.field_78242_c = var3;
   }
}
