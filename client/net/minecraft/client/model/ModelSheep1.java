package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;

public class ModelSheep1 extends ModelQuadruped {
   private float field_78152_i;

   public ModelSheep1() {
      super(12, 0.0F);
      this.field_78150_a = new ModelRenderer(this, 0, 0);
      this.field_78150_a.func_78790_a(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
      this.field_78150_a.func_78793_a(0.0F, 6.0F, -8.0F);
      this.field_78148_b = new ModelRenderer(this, 28, 8);
      this.field_78148_b.func_78790_a(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
      this.field_78148_b.func_78793_a(0.0F, 5.0F, 2.0F);
      float var1 = 0.5F;
      this.field_78149_c = new ModelRenderer(this, 0, 16);
      this.field_78149_c.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78149_c.func_78793_a(-3.0F, 12.0F, 7.0F);
      this.field_78146_d = new ModelRenderer(this, 0, 16);
      this.field_78146_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78146_d.func_78793_a(3.0F, 12.0F, 7.0F);
      this.field_78147_e = new ModelRenderer(this, 0, 16);
      this.field_78147_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78147_e.func_78793_a(-3.0F, 12.0F, -5.0F);
      this.field_78144_f = new ModelRenderer(this, 0, 16);
      this.field_78144_f.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78144_f.func_78793_a(3.0F, 12.0F, -5.0F);
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      super.func_78086_a(var1, var2, var3, var4);
      this.field_78150_a.field_78797_d = 6.0F + ((EntitySheep)var1).func_70894_j(var4) * 9.0F;
      this.field_78152_i = ((EntitySheep)var1).func_70890_k(var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_78150_a.field_78795_f = this.field_78152_i;
   }
}
