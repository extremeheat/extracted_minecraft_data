package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;

public class ModelSkeleton extends ModelZombie {
   public ModelSkeleton() {
      this(0.0F);
   }

   public ModelSkeleton(float var1) {
      super(var1, 0.0F, 64, 32);
      this.field_78112_f = new ModelRenderer(this, 40, 16);
      this.field_78112_f.func_78790_a(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.field_78112_f.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.field_78113_g = new ModelRenderer(this, 40, 16);
      this.field_78113_g.field_78809_i = true;
      this.field_78113_g.func_78790_a(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.field_78113_g.func_78793_a(5.0F, 2.0F, 0.0F);
      this.field_78123_h = new ModelRenderer(this, 0, 16);
      this.field_78123_h.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
      this.field_78123_h.func_78793_a(-2.0F, 12.0F, 0.0F);
      this.field_78124_i = new ModelRenderer(this, 0, 16);
      this.field_78124_i.field_78809_i = true;
      this.field_78124_i.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
      this.field_78124_i.func_78793_a(2.0F, 12.0F, 0.0F);
   }

   @Override
   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      this.field_78118_o = ((EntitySkeleton)var1).func_82202_m() == 1;
      super.func_78086_a(var1, var2, var3, var4);
   }

   @Override
   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
   }
}
