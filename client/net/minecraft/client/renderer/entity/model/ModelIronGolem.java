package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelIronGolem extends ModelBase {
   private final ModelRenderer field_78178_a;
   private final ModelRenderer field_78176_b;
   public ModelRenderer field_78177_c;
   private final ModelRenderer field_78174_d;
   private final ModelRenderer field_78175_e;
   private final ModelRenderer field_78173_f;

   public ModelIronGolem() {
      this(0.0F);
   }

   public ModelIronGolem(float var1) {
      this(var1, -7.0F);
   }

   public ModelIronGolem(float var1, float var2) {
      super();
      boolean var3 = true;
      boolean var4 = true;
      this.field_78178_a = (new ModelRenderer(this)).func_78787_b(128, 128);
      this.field_78178_a.func_78793_a(0.0F, 0.0F + var2, -2.0F);
      this.field_78178_a.func_78784_a(0, 0).func_78790_a(-4.0F, -12.0F, -5.5F, 8, 10, 8, var1);
      this.field_78178_a.func_78784_a(24, 0).func_78790_a(-1.0F, -5.0F, -7.5F, 2, 4, 2, var1);
      this.field_78176_b = (new ModelRenderer(this)).func_78787_b(128, 128);
      this.field_78176_b.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_78176_b.func_78784_a(0, 40).func_78790_a(-9.0F, -2.0F, -6.0F, 18, 12, 11, var1);
      this.field_78176_b.func_78784_a(0, 70).func_78790_a(-4.5F, 10.0F, -3.0F, 9, 5, 6, var1 + 0.5F);
      this.field_78177_c = (new ModelRenderer(this)).func_78787_b(128, 128);
      this.field_78177_c.func_78793_a(0.0F, -7.0F, 0.0F);
      this.field_78177_c.func_78784_a(60, 21).func_78790_a(-13.0F, -2.5F, -3.0F, 4, 30, 6, var1);
      this.field_78174_d = (new ModelRenderer(this)).func_78787_b(128, 128);
      this.field_78174_d.func_78793_a(0.0F, -7.0F, 0.0F);
      this.field_78174_d.func_78784_a(60, 58).func_78790_a(9.0F, -2.5F, -3.0F, 4, 30, 6, var1);
      this.field_78175_e = (new ModelRenderer(this, 0, 22)).func_78787_b(128, 128);
      this.field_78175_e.func_78793_a(-4.0F, 18.0F + var2, 0.0F);
      this.field_78175_e.func_78784_a(37, 0).func_78790_a(-3.5F, -3.0F, -3.0F, 6, 16, 5, var1);
      this.field_78173_f = (new ModelRenderer(this, 0, 22)).func_78787_b(128, 128);
      this.field_78173_f.field_78809_i = true;
      this.field_78173_f.func_78784_a(60, 0).func_78793_a(5.0F, 18.0F + var2, 0.0F);
      this.field_78173_f.func_78790_a(-3.5F, -3.0F, -3.0F, 6, 16, 5, var1);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78178_a.func_78785_a(var7);
      this.field_78176_b.func_78785_a(var7);
      this.field_78175_e.func_78785_a(var7);
      this.field_78173_f.func_78785_a(var7);
      this.field_78177_c.func_78785_a(var7);
      this.field_78174_d.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_78178_a.field_78796_g = var4 * 0.017453292F;
      this.field_78178_a.field_78795_f = var5 * 0.017453292F;
      this.field_78175_e.field_78795_f = -1.5F * this.func_78172_a(var1, 13.0F) * var2;
      this.field_78173_f.field_78795_f = 1.5F * this.func_78172_a(var1, 13.0F) * var2;
      this.field_78175_e.field_78796_g = 0.0F;
      this.field_78173_f.field_78796_g = 0.0F;
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      EntityIronGolem var5 = (EntityIronGolem)var1;
      int var6 = var5.func_70854_o();
      if (var6 > 0) {
         this.field_78177_c.field_78795_f = -2.0F + 1.5F * this.func_78172_a((float)var6 - var4, 10.0F);
         this.field_78174_d.field_78795_f = -2.0F + 1.5F * this.func_78172_a((float)var6 - var4, 10.0F);
      } else {
         int var7 = var5.func_70853_p();
         if (var7 > 0) {
            this.field_78177_c.field_78795_f = -0.8F + 0.025F * this.func_78172_a((float)var7, 70.0F);
            this.field_78174_d.field_78795_f = 0.0F;
         } else {
            this.field_78177_c.field_78795_f = (-0.2F + 1.5F * this.func_78172_a(var2, 13.0F)) * var3;
            this.field_78174_d.field_78795_f = (-0.2F - 1.5F * this.func_78172_a(var2, 13.0F)) * var3;
         }
      }

   }

   private float func_78172_a(float var1, float var2) {
      return (Math.abs(var1 % var2 - var2 * 0.5F) - var2 * 0.25F) / (var2 * 0.25F);
   }

   public ModelRenderer func_205071_a() {
      return this.field_78177_c;
   }
}
