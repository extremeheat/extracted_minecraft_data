package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelHumanoidHead extends ModelSkeletonHead {
   private final ModelRenderer field_178717_b = new ModelRenderer(this, 32, 0);

   public ModelHumanoidHead() {
      super(0, 0, 64, 64);
      this.field_178717_b.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
      this.field_178717_b.func_78793_a(0.0F, 0.0F, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_178717_b.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_178717_b.field_78796_g = this.field_82896_a.field_78796_g;
      this.field_178717_b.field_78795_f = this.field_82896_a.field_78795_f;
   }
}
