package net.minecraft.client.renderer.entity.model;

public class ModelPig extends ModelQuadruped {
   public ModelPig() {
      this(0.0F);
   }

   public ModelPig(float var1) {
      super(6, var1);
      this.field_78150_a.func_78784_a(16, 16).func_78790_a(-2.0F, 0.0F, -9.0F, 4, 3, 1, var1);
      this.field_78145_g = 4.0F;
   }
}
