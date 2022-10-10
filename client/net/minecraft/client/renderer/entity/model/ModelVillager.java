package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelVillager extends ModelBase {
   protected ModelRenderer field_78191_a;
   protected ModelRenderer field_78189_b;
   protected ModelRenderer field_78190_c;
   protected ModelRenderer field_78187_d;
   protected ModelRenderer field_78188_e;
   protected ModelRenderer field_82898_f;

   public ModelVillager(float var1) {
      this(var1, 0.0F, 64, 64);
   }

   public ModelVillager(float var1, float var2, int var3, int var4) {
      super();
      this.field_78191_a = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_78191_a.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_78191_a.func_78784_a(0, 0).func_78790_a(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
      this.field_82898_f = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_82898_f.func_78793_a(0.0F, var2 - 2.0F, 0.0F);
      this.field_82898_f.func_78784_a(24, 0).func_78790_a(-1.0F, -1.0F, -6.0F, 2, 4, 2, var1);
      this.field_78191_a.func_78792_a(this.field_82898_f);
      this.field_78189_b = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_78189_b.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_78189_b.func_78784_a(16, 20).func_78790_a(-4.0F, 0.0F, -3.0F, 8, 12, 6, var1);
      this.field_78189_b.func_78784_a(0, 38).func_78790_a(-4.0F, 0.0F, -3.0F, 8, 18, 6, var1 + 0.5F);
      this.field_78190_c = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_78190_c.func_78793_a(0.0F, 0.0F + var2 + 2.0F, 0.0F);
      this.field_78190_c.func_78784_a(44, 22).func_78790_a(-8.0F, -2.0F, -2.0F, 4, 8, 4, var1);
      this.field_78190_c.func_78784_a(44, 22).func_205345_a(4.0F, -2.0F, -2.0F, 4, 8, 4, var1, true);
      this.field_78190_c.func_78784_a(40, 38).func_78790_a(-4.0F, 2.0F, -2.0F, 8, 4, 4, var1);
      this.field_78187_d = (new ModelRenderer(this, 0, 22)).func_78787_b(var3, var4);
      this.field_78187_d.func_78793_a(-2.0F, 12.0F + var2, 0.0F);
      this.field_78187_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_78188_e = (new ModelRenderer(this, 0, 22)).func_78787_b(var3, var4);
      this.field_78188_e.field_78809_i = true;
      this.field_78188_e.func_78793_a(2.0F, 12.0F + var2, 0.0F);
      this.field_78188_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78191_a.func_78785_a(var7);
      this.field_78189_b.func_78785_a(var7);
      this.field_78187_d.func_78785_a(var7);
      this.field_78188_e.func_78785_a(var7);
      this.field_78190_c.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_78191_a.field_78796_g = var4 * 0.017453292F;
      this.field_78191_a.field_78795_f = var5 * 0.017453292F;
      this.field_78190_c.field_78797_d = 3.0F;
      this.field_78190_c.field_78798_e = -1.0F;
      this.field_78190_c.field_78795_f = -0.75F;
      this.field_78187_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2 * 0.5F;
      this.field_78188_e.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2 * 0.5F;
      this.field_78187_d.field_78796_g = 0.0F;
      this.field_78188_e.field_78796_g = 0.0F;
   }

   public ModelRenderer func_205072_a() {
      return this.field_78191_a;
   }
}
