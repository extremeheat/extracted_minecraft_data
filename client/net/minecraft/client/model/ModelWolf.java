package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;

public class ModelWolf extends ModelBase {
   public ModelRenderer field_78185_a;
   public ModelRenderer field_78183_b;
   public ModelRenderer field_78184_c;
   public ModelRenderer field_78181_d;
   public ModelRenderer field_78182_e;
   public ModelRenderer field_78179_f;
   ModelRenderer field_78180_g;
   ModelRenderer field_78186_h;

   public ModelWolf() {
      super();
      float var1 = 0.0F;
      float var2 = 13.5F;
      this.field_78185_a = new ModelRenderer(this, 0, 0);
      this.field_78185_a.func_78790_a(-3.0F, -3.0F, -2.0F, 6, 6, 4, var1);
      this.field_78185_a.func_78793_a(-1.0F, var2, -7.0F);
      this.field_78183_b = new ModelRenderer(this, 18, 14);
      this.field_78183_b.func_78790_a(-4.0F, -2.0F, -3.0F, 6, 9, 6, var1);
      this.field_78183_b.func_78793_a(0.0F, 14.0F, 2.0F);
      this.field_78186_h = new ModelRenderer(this, 21, 0);
      this.field_78186_h.func_78790_a(-4.0F, -3.0F, -3.0F, 8, 6, 7, var1);
      this.field_78186_h.func_78793_a(-1.0F, 14.0F, 2.0F);
      this.field_78184_c = new ModelRenderer(this, 0, 18);
      this.field_78184_c.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.field_78184_c.func_78793_a(-2.5F, 16.0F, 7.0F);
      this.field_78181_d = new ModelRenderer(this, 0, 18);
      this.field_78181_d.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.field_78181_d.func_78793_a(0.5F, 16.0F, 7.0F);
      this.field_78182_e = new ModelRenderer(this, 0, 18);
      this.field_78182_e.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.field_78182_e.func_78793_a(-2.5F, 16.0F, -4.0F);
      this.field_78179_f = new ModelRenderer(this, 0, 18);
      this.field_78179_f.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.field_78179_f.func_78793_a(0.5F, 16.0F, -4.0F);
      this.field_78180_g = new ModelRenderer(this, 9, 18);
      this.field_78180_g.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.field_78180_g.func_78793_a(-1.0F, 12.0F, 8.0F);
      this.field_78185_a.func_78784_a(16, 14).func_78790_a(-3.0F, -5.0F, 0.0F, 2, 2, 1, var1);
      this.field_78185_a.func_78784_a(16, 14).func_78790_a(1.0F, -5.0F, 0.0F, 2, 2, 1, var1);
      this.field_78185_a.func_78784_a(0, 10).func_78790_a(-1.5F, 0.0F, -5.0F, 3, 3, 4, var1);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 5.0F * var7, 2.0F * var7);
         this.field_78185_a.func_78791_b(var7);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_78183_b.func_78785_a(var7);
         this.field_78184_c.func_78785_a(var7);
         this.field_78181_d.func_78785_a(var7);
         this.field_78182_e.func_78785_a(var7);
         this.field_78179_f.func_78785_a(var7);
         this.field_78180_g.func_78791_b(var7);
         this.field_78186_h.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         this.field_78185_a.func_78791_b(var7);
         this.field_78183_b.func_78785_a(var7);
         this.field_78184_c.func_78785_a(var7);
         this.field_78181_d.func_78785_a(var7);
         this.field_78182_e.func_78785_a(var7);
         this.field_78179_f.func_78785_a(var7);
         this.field_78180_g.func_78791_b(var7);
         this.field_78186_h.func_78785_a(var7);
      }

   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      EntityWolf var5 = (EntityWolf)var1;
      if (var5.func_70919_bu()) {
         this.field_78180_g.field_78796_g = 0.0F;
      } else {
         this.field_78180_g.field_78796_g = MathHelper.func_76134_b(var2 * 0.6662F) * 1.4F * var3;
      }

      if (var5.func_70906_o()) {
         this.field_78186_h.func_78793_a(-1.0F, 16.0F, -3.0F);
         this.field_78186_h.field_78795_f = 1.2566371F;
         this.field_78186_h.field_78796_g = 0.0F;
         this.field_78183_b.func_78793_a(0.0F, 18.0F, 0.0F);
         this.field_78183_b.field_78795_f = 0.7853982F;
         this.field_78180_g.func_78793_a(-1.0F, 21.0F, 6.0F);
         this.field_78184_c.func_78793_a(-2.5F, 22.0F, 2.0F);
         this.field_78184_c.field_78795_f = 4.712389F;
         this.field_78181_d.func_78793_a(0.5F, 22.0F, 2.0F);
         this.field_78181_d.field_78795_f = 4.712389F;
         this.field_78182_e.field_78795_f = 5.811947F;
         this.field_78182_e.func_78793_a(-2.49F, 17.0F, -4.0F);
         this.field_78179_f.field_78795_f = 5.811947F;
         this.field_78179_f.func_78793_a(0.51F, 17.0F, -4.0F);
      } else {
         this.field_78183_b.func_78793_a(0.0F, 14.0F, 2.0F);
         this.field_78183_b.field_78795_f = 1.5707964F;
         this.field_78186_h.func_78793_a(-1.0F, 14.0F, -3.0F);
         this.field_78186_h.field_78795_f = this.field_78183_b.field_78795_f;
         this.field_78180_g.func_78793_a(-1.0F, 12.0F, 8.0F);
         this.field_78184_c.func_78793_a(-2.5F, 16.0F, 7.0F);
         this.field_78181_d.func_78793_a(0.5F, 16.0F, 7.0F);
         this.field_78182_e.func_78793_a(-2.5F, 16.0F, -4.0F);
         this.field_78179_f.func_78793_a(0.5F, 16.0F, -4.0F);
         this.field_78184_c.field_78795_f = MathHelper.func_76134_b(var2 * 0.6662F) * 1.4F * var3;
         this.field_78181_d.field_78795_f = MathHelper.func_76134_b(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.field_78182_e.field_78795_f = MathHelper.func_76134_b(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.field_78179_f.field_78795_f = MathHelper.func_76134_b(var2 * 0.6662F) * 1.4F * var3;
      }

      this.field_78185_a.field_78808_h = var5.func_70917_k(var4) + var5.func_70923_f(var4, 0.0F);
      this.field_78186_h.field_78808_h = var5.func_70923_f(var4, -0.08F);
      this.field_78183_b.field_78808_h = var5.func_70923_f(var4, -0.16F);
      this.field_78180_g.field_78808_h = var5.func_70923_f(var4, -0.2F);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_78185_a.field_78795_f = var5 / 57.295776F;
      this.field_78185_a.field_78796_g = var4 / 57.295776F;
      this.field_78180_g.field_78795_f = var3;
   }
}
