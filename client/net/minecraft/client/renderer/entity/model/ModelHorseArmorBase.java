package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.util.math.MathHelper;

public class ModelHorseArmorBase extends ModelBase {
   protected ModelRenderer field_199049_a;
   protected ModelRenderer field_199050_b;
   private final ModelRenderer field_199051_c;
   private final ModelRenderer field_199052_d;
   private final ModelRenderer field_199053_e;
   private final ModelRenderer field_199054_f;
   private final ModelRenderer field_199055_g;
   private final ModelRenderer[] field_199056_h;
   private final ModelRenderer[] field_209234_i;

   public ModelHorseArmorBase() {
      super();
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.field_199049_a = new ModelRenderer(this, 0, 32);
      this.field_199049_a.func_78790_a(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
      this.field_199049_a.func_78793_a(0.0F, 11.0F, 5.0F);
      this.field_199050_b = new ModelRenderer(this, 0, 35);
      this.field_199050_b.func_78789_a(-2.05F, -6.0F, -2.0F, 4, 12, 7);
      this.field_199050_b.field_78795_f = 0.5235988F;
      ModelRenderer var1 = new ModelRenderer(this, 0, 13);
      var1.func_78789_a(-3.0F, -11.0F, -2.0F, 6, 5, 7);
      ModelRenderer var2 = new ModelRenderer(this, 56, 36);
      var2.func_78789_a(-1.0F, -11.0F, 5.01F, 2, 16, 2);
      ModelRenderer var3 = new ModelRenderer(this, 0, 25);
      var3.func_78789_a(-2.0F, -11.0F, -7.0F, 4, 5, 5);
      this.field_199050_b.func_78792_a(var1);
      this.field_199050_b.func_78792_a(var2);
      this.field_199050_b.func_78792_a(var3);
      this.func_199047_a(this.field_199050_b);
      this.field_199051_c = new ModelRenderer(this, 48, 21);
      this.field_199051_c.field_78809_i = true;
      this.field_199051_c.func_78789_a(-3.0F, -1.01F, -1.0F, 4, 11, 4);
      this.field_199051_c.func_78793_a(4.0F, 14.0F, 7.0F);
      this.field_199052_d = new ModelRenderer(this, 48, 21);
      this.field_199052_d.func_78789_a(-1.0F, -1.01F, -1.0F, 4, 11, 4);
      this.field_199052_d.func_78793_a(-4.0F, 14.0F, 7.0F);
      this.field_199053_e = new ModelRenderer(this, 48, 21);
      this.field_199053_e.field_78809_i = true;
      this.field_199053_e.func_78789_a(-3.0F, -1.01F, -1.9F, 4, 11, 4);
      this.field_199053_e.func_78793_a(4.0F, 6.0F, -12.0F);
      this.field_199054_f = new ModelRenderer(this, 48, 21);
      this.field_199054_f.func_78789_a(-1.0F, -1.01F, -1.9F, 4, 11, 4);
      this.field_199054_f.func_78793_a(-4.0F, 6.0F, -12.0F);
      this.field_199055_g = new ModelRenderer(this, 42, 36);
      this.field_199055_g.func_78789_a(-1.5F, 0.0F, 0.0F, 3, 14, 4);
      this.field_199055_g.func_78793_a(0.0F, -5.0F, 2.0F);
      this.field_199055_g.field_78795_f = 0.5235988F;
      this.field_199049_a.func_78792_a(this.field_199055_g);
      ModelRenderer var4 = new ModelRenderer(this, 26, 0);
      var4.func_78790_a(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
      this.field_199049_a.func_78792_a(var4);
      ModelRenderer var5 = new ModelRenderer(this, 29, 5);
      var5.func_78789_a(2.0F, -9.0F, -6.0F, 1, 2, 2);
      this.field_199050_b.func_78792_a(var5);
      ModelRenderer var6 = new ModelRenderer(this, 29, 5);
      var6.func_78789_a(-3.0F, -9.0F, -6.0F, 1, 2, 2);
      this.field_199050_b.func_78792_a(var6);
      ModelRenderer var7 = new ModelRenderer(this, 32, 2);
      var7.func_78789_a(3.1F, -6.0F, -8.0F, 0, 3, 16);
      var7.field_78795_f = -0.5235988F;
      this.field_199050_b.func_78792_a(var7);
      ModelRenderer var8 = new ModelRenderer(this, 32, 2);
      var8.func_78789_a(-3.1F, -6.0F, -8.0F, 0, 3, 16);
      var8.field_78795_f = -0.5235988F;
      this.field_199050_b.func_78792_a(var8);
      ModelRenderer var9 = new ModelRenderer(this, 1, 1);
      var9.func_78790_a(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
      this.field_199050_b.func_78792_a(var9);
      ModelRenderer var10 = new ModelRenderer(this, 19, 0);
      var10.func_78790_a(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
      this.field_199050_b.func_78792_a(var10);
      this.field_199056_h = new ModelRenderer[]{var4, var5, var6, var9, var10};
      this.field_209234_i = new ModelRenderer[]{var7, var8};
   }

   protected void func_199047_a(ModelRenderer var1) {
      ModelRenderer var2 = new ModelRenderer(this, 19, 16);
      var2.func_78790_a(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      ModelRenderer var3 = new ModelRenderer(this, 19, 16);
      var3.func_78790_a(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      var1.func_78792_a(var2);
      var1.func_78792_a(var3);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      AbstractHorse var8 = (AbstractHorse)var1;
      boolean var9 = var8.func_70631_g_();
      float var10 = var8.func_110254_bY();
      boolean var11 = var8.func_110257_ck();
      boolean var12 = var8.func_184207_aI();
      ModelRenderer[] var13 = this.field_199056_h;
      int var14 = var13.length;

      int var15;
      ModelRenderer var16;
      for(var15 = 0; var15 < var14; ++var15) {
         var16 = var13[var15];
         var16.field_78806_j = var11;
      }

      var13 = this.field_209234_i;
      var14 = var13.length;

      for(var15 = 0; var15 < var14; ++var15) {
         var16 = var13[var15];
         var16.field_78806_j = var12 && var11;
      }

      if (var9) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(var10, 0.5F + var10 * 0.5F, var10);
         GlStateManager.func_179109_b(0.0F, 0.95F * (1.0F - var10), 0.0F);
      }

      this.field_199051_c.func_78785_a(var7);
      this.field_199052_d.func_78785_a(var7);
      this.field_199053_e.func_78785_a(var7);
      this.field_199054_f.func_78785_a(var7);
      if (var9) {
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(var10, var10, var10);
         GlStateManager.func_179109_b(0.0F, 2.3F * (1.0F - var10), 0.0F);
      }

      this.field_199049_a.func_78785_a(var7);
      if (var9) {
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         float var17 = var10 + 0.1F * var10;
         GlStateManager.func_179152_a(var17, var17, var17);
         GlStateManager.func_179109_b(0.0F, 2.25F * (1.0F - var17), 0.1F * (1.4F - var17));
      }

      this.field_199050_b.func_78785_a(var7);
      if (var9) {
         GlStateManager.func_179121_F();
      }

   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      super.func_78086_a(var1, var2, var3, var4);
      float var5 = this.func_199048_a(var1.field_70760_ar, var1.field_70761_aq, var4);
      float var6 = this.func_199048_a(var1.field_70758_at, var1.field_70759_as, var4);
      float var7 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var4;
      float var8 = var6 - var5;
      float var9 = var7 * 0.017453292F;
      if (var8 > 20.0F) {
         var8 = 20.0F;
      }

      if (var8 < -20.0F) {
         var8 = -20.0F;
      }

      if (var3 > 0.2F) {
         var9 += MathHelper.func_76134_b(var2 * 0.4F) * 0.15F * var3;
      }

      AbstractHorse var10 = (AbstractHorse)var1;
      float var11 = var10.func_110258_o(var4);
      float var12 = var10.func_110223_p(var4);
      float var13 = 1.0F - var12;
      float var14 = var10.func_110201_q(var4);
      boolean var15 = var10.field_110278_bp != 0;
      float var16 = (float)var1.field_70173_aa + var4;
      this.field_199050_b.field_78797_d = 4.0F;
      this.field_199050_b.field_78798_e = -12.0F;
      this.field_199049_a.field_78795_f = 0.0F;
      this.field_199050_b.field_78795_f = 0.5235988F + var9;
      this.field_199050_b.field_78796_g = var8 * 0.017453292F;
      float var17 = var10.func_70090_H() ? 0.2F : 1.0F;
      float var18 = MathHelper.func_76134_b(var17 * var2 * 0.6662F + 3.1415927F);
      float var19 = var18 * 0.8F * var3;
      float var20 = (1.0F - Math.max(var12, var11)) * (0.5235988F + var9 + var14 * MathHelper.func_76126_a(var16) * 0.05F);
      this.field_199050_b.field_78795_f = var12 * (0.2617994F + var9) + var11 * (2.1816616F + MathHelper.func_76126_a(var16) * 0.05F) + var20;
      this.field_199050_b.field_78796_g = var12 * var8 * 0.017453292F + (1.0F - Math.max(var12, var11)) * this.field_199050_b.field_78796_g;
      this.field_199050_b.field_78797_d = var12 * -4.0F + var11 * 11.0F + (1.0F - Math.max(var12, var11)) * this.field_199050_b.field_78797_d;
      this.field_199050_b.field_78798_e = var12 * -4.0F + var11 * -12.0F + (1.0F - Math.max(var12, var11)) * this.field_199050_b.field_78798_e;
      this.field_199049_a.field_78795_f = var12 * -0.7853982F + var13 * this.field_199049_a.field_78795_f;
      float var21 = 0.2617994F * var12;
      float var22 = MathHelper.func_76134_b(var16 * 0.6F + 3.1415927F);
      this.field_199053_e.field_78797_d = 2.0F * var12 + 14.0F * var13;
      this.field_199053_e.field_78798_e = -6.0F * var12 - 10.0F * var13;
      this.field_199054_f.field_78797_d = this.field_199053_e.field_78797_d;
      this.field_199054_f.field_78798_e = this.field_199053_e.field_78798_e;
      float var23 = (-1.0471976F + var22) * var12 + var19 * var13;
      float var24 = (-1.0471976F - var22) * var12 - var19 * var13;
      this.field_199051_c.field_78795_f = var21 - var18 * 0.5F * var3 * var13;
      this.field_199052_d.field_78795_f = var21 + var18 * 0.5F * var3 * var13;
      this.field_199053_e.field_78795_f = var23;
      this.field_199054_f.field_78795_f = var24;
      this.field_199055_g.field_78795_f = 0.5235988F + var3 * 0.75F;
      this.field_199055_g.field_78797_d = -5.0F + var3;
      this.field_199055_g.field_78798_e = 2.0F + var3 * 2.0F;
      if (var15) {
         this.field_199055_g.field_78796_g = MathHelper.func_76134_b(var16 * 0.7F);
      } else {
         this.field_199055_g.field_78796_g = 0.0F;
      }

   }

   private float func_199048_a(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }
}
