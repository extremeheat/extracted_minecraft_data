package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.MathHelper;

public class ModelOcelot extends ModelBase {
   ModelRenderer field_78161_a;
   ModelRenderer field_78159_b;
   ModelRenderer field_78160_c;
   ModelRenderer field_78157_d;
   ModelRenderer field_78158_e;
   ModelRenderer field_78155_f;
   ModelRenderer field_78156_g;
   ModelRenderer field_78162_h;
   int field_78163_i = 1;

   public ModelOcelot() {
      super();
      this.func_78085_a("head.main", 0, 0);
      this.func_78085_a("head.nose", 0, 24);
      this.func_78085_a("head.ear1", 0, 10);
      this.func_78085_a("head.ear2", 6, 10);
      this.field_78156_g = new ModelRenderer(this, "head");
      this.field_78156_g.func_78786_a("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
      this.field_78156_g.func_78786_a("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
      this.field_78156_g.func_78786_a("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
      this.field_78156_g.func_78786_a("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
      this.field_78156_g.func_78793_a(0.0F, 15.0F, -9.0F);
      this.field_78162_h = new ModelRenderer(this, 20, 0);
      this.field_78162_h.func_78790_a(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
      this.field_78162_h.func_78793_a(0.0F, 12.0F, -10.0F);
      this.field_78158_e = new ModelRenderer(this, 0, 15);
      this.field_78158_e.func_78789_a(-0.5F, 0.0F, 0.0F, 1, 8, 1);
      this.field_78158_e.field_78795_f = 0.9F;
      this.field_78158_e.func_78793_a(0.0F, 15.0F, 8.0F);
      this.field_78155_f = new ModelRenderer(this, 4, 15);
      this.field_78155_f.func_78789_a(-0.5F, 0.0F, 0.0F, 1, 8, 1);
      this.field_78155_f.func_78793_a(0.0F, 20.0F, 14.0F);
      this.field_78161_a = new ModelRenderer(this, 8, 13);
      this.field_78161_a.func_78789_a(-1.0F, 0.0F, 1.0F, 2, 6, 2);
      this.field_78161_a.func_78793_a(1.1F, 18.0F, 5.0F);
      this.field_78159_b = new ModelRenderer(this, 8, 13);
      this.field_78159_b.func_78789_a(-1.0F, 0.0F, 1.0F, 2, 6, 2);
      this.field_78159_b.func_78793_a(-1.1F, 18.0F, 5.0F);
      this.field_78160_c = new ModelRenderer(this, 40, 0);
      this.field_78160_c.func_78789_a(-1.0F, 0.0F, 0.0F, 2, 10, 2);
      this.field_78160_c.func_78793_a(1.2F, 13.8F, -5.0F);
      this.field_78157_d = new ModelRenderer(this, 40, 0);
      this.field_78157_d.func_78789_a(-1.0F, 0.0F, 0.0F, 2, 10, 2);
      this.field_78157_d.func_78793_a(-1.2F, 13.8F, -5.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.5F / var8, 1.5F / var8, 1.5F / var8);
         GlStateManager.func_179109_b(0.0F, 10.0F * var7, 4.0F * var7);
         this.field_78156_g.func_78785_a(var7);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_78162_h.func_78785_a(var7);
         this.field_78161_a.func_78785_a(var7);
         this.field_78159_b.func_78785_a(var7);
         this.field_78160_c.func_78785_a(var7);
         this.field_78157_d.func_78785_a(var7);
         this.field_78158_e.func_78785_a(var7);
         this.field_78155_f.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         this.field_78156_g.func_78785_a(var7);
         this.field_78162_h.func_78785_a(var7);
         this.field_78158_e.func_78785_a(var7);
         this.field_78155_f.func_78785_a(var7);
         this.field_78161_a.func_78785_a(var7);
         this.field_78159_b.func_78785_a(var7);
         this.field_78160_c.func_78785_a(var7);
         this.field_78157_d.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_78156_g.field_78795_f = var5 / 57.295776F;
      this.field_78156_g.field_78796_g = var4 / 57.295776F;
      if (this.field_78163_i != 3) {
         this.field_78162_h.field_78795_f = 1.5707964F;
         if (this.field_78163_i == 2) {
            this.field_78161_a.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.0F * var2;
            this.field_78159_b.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 0.3F) * 1.0F * var2;
            this.field_78160_c.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F + 0.3F) * 1.0F * var2;
            this.field_78157_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.0F * var2;
            this.field_78155_f.field_78795_f = 1.7278761F + 0.31415927F * MathHelper.func_76134_b(var1) * var2;
         } else {
            this.field_78161_a.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.0F * var2;
            this.field_78159_b.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.0F * var2;
            this.field_78160_c.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.0F * var2;
            this.field_78157_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.0F * var2;
            if (this.field_78163_i == 1) {
               this.field_78155_f.field_78795_f = 1.7278761F + 0.7853982F * MathHelper.func_76134_b(var1) * var2;
            } else {
               this.field_78155_f.field_78795_f = 1.7278761F + 0.47123894F * MathHelper.func_76134_b(var1) * var2;
            }
         }
      }

   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      EntityOcelot var5 = (EntityOcelot)var1;
      this.field_78162_h.field_78797_d = 12.0F;
      this.field_78162_h.field_78798_e = -10.0F;
      this.field_78156_g.field_78797_d = 15.0F;
      this.field_78156_g.field_78798_e = -9.0F;
      this.field_78158_e.field_78797_d = 15.0F;
      this.field_78158_e.field_78798_e = 8.0F;
      this.field_78155_f.field_78797_d = 20.0F;
      this.field_78155_f.field_78798_e = 14.0F;
      this.field_78160_c.field_78797_d = this.field_78157_d.field_78797_d = 13.8F;
      this.field_78160_c.field_78798_e = this.field_78157_d.field_78798_e = -5.0F;
      this.field_78161_a.field_78797_d = this.field_78159_b.field_78797_d = 18.0F;
      this.field_78161_a.field_78798_e = this.field_78159_b.field_78798_e = 5.0F;
      this.field_78158_e.field_78795_f = 0.9F;
      ModelRenderer var10000;
      if (var5.func_70093_af()) {
         ++this.field_78162_h.field_78797_d;
         var10000 = this.field_78156_g;
         var10000.field_78797_d += 2.0F;
         ++this.field_78158_e.field_78797_d;
         var10000 = this.field_78155_f;
         var10000.field_78797_d += -4.0F;
         var10000 = this.field_78155_f;
         var10000.field_78798_e += 2.0F;
         this.field_78158_e.field_78795_f = 1.5707964F;
         this.field_78155_f.field_78795_f = 1.5707964F;
         this.field_78163_i = 0;
      } else if (var5.func_70051_ag()) {
         this.field_78155_f.field_78797_d = this.field_78158_e.field_78797_d;
         var10000 = this.field_78155_f;
         var10000.field_78798_e += 2.0F;
         this.field_78158_e.field_78795_f = 1.5707964F;
         this.field_78155_f.field_78795_f = 1.5707964F;
         this.field_78163_i = 2;
      } else if (var5.func_70906_o()) {
         this.field_78162_h.field_78795_f = 0.7853982F;
         var10000 = this.field_78162_h;
         var10000.field_78797_d += -4.0F;
         var10000 = this.field_78162_h;
         var10000.field_78798_e += 5.0F;
         var10000 = this.field_78156_g;
         var10000.field_78797_d += -3.3F;
         ++this.field_78156_g.field_78798_e;
         var10000 = this.field_78158_e;
         var10000.field_78797_d += 8.0F;
         var10000 = this.field_78158_e;
         var10000.field_78798_e += -2.0F;
         var10000 = this.field_78155_f;
         var10000.field_78797_d += 2.0F;
         var10000 = this.field_78155_f;
         var10000.field_78798_e += -0.8F;
         this.field_78158_e.field_78795_f = 1.7278761F;
         this.field_78155_f.field_78795_f = 2.670354F;
         this.field_78160_c.field_78795_f = this.field_78157_d.field_78795_f = -0.15707964F;
         this.field_78160_c.field_78797_d = this.field_78157_d.field_78797_d = 15.8F;
         this.field_78160_c.field_78798_e = this.field_78157_d.field_78798_e = -7.0F;
         this.field_78161_a.field_78795_f = this.field_78159_b.field_78795_f = -1.5707964F;
         this.field_78161_a.field_78797_d = this.field_78159_b.field_78797_d = 21.0F;
         this.field_78161_a.field_78798_e = this.field_78159_b.field_78798_e = 1.0F;
         this.field_78163_i = 3;
      } else {
         this.field_78163_i = 1;
      }

   }
}
