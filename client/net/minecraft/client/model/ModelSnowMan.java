package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSnowMan extends ModelBase {
   public ModelRenderer field_78196_a;
   public ModelRenderer field_78194_b;
   public ModelRenderer field_78195_c;
   public ModelRenderer field_78192_d;
   public ModelRenderer field_78193_e;

   public ModelSnowMan() {
      super();
      float var1 = 4.0F;
      float var2 = 0.0F;
      this.field_78195_c = (new ModelRenderer(this, 0, 0)).func_78787_b(64, 64);
      this.field_78195_c.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, var2 - 0.5F);
      this.field_78195_c.func_78793_a(0.0F, 0.0F + var1, 0.0F);
      this.field_78192_d = (new ModelRenderer(this, 32, 0)).func_78787_b(64, 64);
      this.field_78192_d.func_78790_a(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
      this.field_78192_d.func_78793_a(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
      this.field_78193_e = (new ModelRenderer(this, 32, 0)).func_78787_b(64, 64);
      this.field_78193_e.func_78790_a(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
      this.field_78193_e.func_78793_a(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
      this.field_78196_a = (new ModelRenderer(this, 0, 16)).func_78787_b(64, 64);
      this.field_78196_a.func_78790_a(-5.0F, -10.0F, -5.0F, 10, 10, 10, var2 - 0.5F);
      this.field_78196_a.func_78793_a(0.0F, 0.0F + var1 + 9.0F, 0.0F);
      this.field_78194_b = (new ModelRenderer(this, 0, 36)).func_78787_b(64, 64);
      this.field_78194_b.func_78790_a(-6.0F, -12.0F, -6.0F, 12, 12, 12, var2 - 0.5F);
      this.field_78194_b.func_78793_a(0.0F, 0.0F + var1 + 20.0F, 0.0F);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_78195_c.field_78796_g = var4 / 57.295776F;
      this.field_78195_c.field_78795_f = var5 / 57.295776F;
      this.field_78196_a.field_78796_g = var4 / 57.295776F * 0.25F;
      float var8 = MathHelper.func_76126_a(this.field_78196_a.field_78796_g);
      float var9 = MathHelper.func_76134_b(this.field_78196_a.field_78796_g);
      this.field_78192_d.field_78808_h = 1.0F;
      this.field_78193_e.field_78808_h = -1.0F;
      this.field_78192_d.field_78796_g = 0.0F + this.field_78196_a.field_78796_g;
      this.field_78193_e.field_78796_g = 3.1415927F + this.field_78196_a.field_78796_g;
      this.field_78192_d.field_78800_c = var9 * 5.0F;
      this.field_78192_d.field_78798_e = -var8 * 5.0F;
      this.field_78193_e.field_78800_c = -var9 * 5.0F;
      this.field_78193_e.field_78798_e = var8 * 5.0F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78196_a.func_78785_a(var7);
      this.field_78194_b.func_78785_a(var7);
      this.field_78195_c.func_78785_a(var7);
      this.field_78192_d.func_78785_a(var7);
      this.field_78193_e.func_78785_a(var7);
   }
}
