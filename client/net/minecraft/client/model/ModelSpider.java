package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSpider extends ModelBase {
   public ModelRenderer field_78209_a;
   public ModelRenderer field_78207_b;
   public ModelRenderer field_78208_c;
   public ModelRenderer field_78205_d;
   public ModelRenderer field_78206_e;
   public ModelRenderer field_78203_f;
   public ModelRenderer field_78204_g;
   public ModelRenderer field_78212_h;
   public ModelRenderer field_78213_i;
   public ModelRenderer field_78210_j;
   public ModelRenderer field_78211_k;

   public ModelSpider() {
      super();
      float var1 = 0.0F;
      byte var2 = 15;
      this.field_78209_a = new ModelRenderer(this, 32, 4);
      this.field_78209_a.func_78790_a(-4.0F, -4.0F, -8.0F, 8, 8, 8, var1);
      this.field_78209_a.func_78793_a(0.0F, (float)var2, -3.0F);
      this.field_78207_b = new ModelRenderer(this, 0, 0);
      this.field_78207_b.func_78790_a(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
      this.field_78207_b.func_78793_a(0.0F, (float)var2, 0.0F);
      this.field_78208_c = new ModelRenderer(this, 0, 12);
      this.field_78208_c.func_78790_a(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
      this.field_78208_c.func_78793_a(0.0F, (float)var2, 9.0F);
      this.field_78205_d = new ModelRenderer(this, 18, 0);
      this.field_78205_d.func_78790_a(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78205_d.func_78793_a(-4.0F, (float)var2, 2.0F);
      this.field_78206_e = new ModelRenderer(this, 18, 0);
      this.field_78206_e.func_78790_a(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78206_e.func_78793_a(4.0F, (float)var2, 2.0F);
      this.field_78203_f = new ModelRenderer(this, 18, 0);
      this.field_78203_f.func_78790_a(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78203_f.func_78793_a(-4.0F, (float)var2, 1.0F);
      this.field_78204_g = new ModelRenderer(this, 18, 0);
      this.field_78204_g.func_78790_a(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78204_g.func_78793_a(4.0F, (float)var2, 1.0F);
      this.field_78212_h = new ModelRenderer(this, 18, 0);
      this.field_78212_h.func_78790_a(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78212_h.func_78793_a(-4.0F, (float)var2, 0.0F);
      this.field_78213_i = new ModelRenderer(this, 18, 0);
      this.field_78213_i.func_78790_a(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78213_i.func_78793_a(4.0F, (float)var2, 0.0F);
      this.field_78210_j = new ModelRenderer(this, 18, 0);
      this.field_78210_j.func_78790_a(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78210_j.func_78793_a(-4.0F, (float)var2, -1.0F);
      this.field_78211_k = new ModelRenderer(this, 18, 0);
      this.field_78211_k.func_78790_a(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.field_78211_k.func_78793_a(4.0F, (float)var2, -1.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78209_a.func_78785_a(var7);
      this.field_78207_b.func_78785_a(var7);
      this.field_78208_c.func_78785_a(var7);
      this.field_78205_d.func_78785_a(var7);
      this.field_78206_e.func_78785_a(var7);
      this.field_78203_f.func_78785_a(var7);
      this.field_78204_g.func_78785_a(var7);
      this.field_78212_h.func_78785_a(var7);
      this.field_78213_i.func_78785_a(var7);
      this.field_78210_j.func_78785_a(var7);
      this.field_78211_k.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_78209_a.field_78796_g = var4 / 57.295776F;
      this.field_78209_a.field_78795_f = var5 / 57.295776F;
      float var8 = 0.7853982F;
      this.field_78205_d.field_78808_h = -var8;
      this.field_78206_e.field_78808_h = var8;
      this.field_78203_f.field_78808_h = -var8 * 0.74F;
      this.field_78204_g.field_78808_h = var8 * 0.74F;
      this.field_78212_h.field_78808_h = -var8 * 0.74F;
      this.field_78213_i.field_78808_h = var8 * 0.74F;
      this.field_78210_j.field_78808_h = -var8;
      this.field_78211_k.field_78808_h = var8;
      float var9 = -0.0F;
      float var10 = 0.3926991F;
      this.field_78205_d.field_78796_g = var10 * 2.0F + var9;
      this.field_78206_e.field_78796_g = -var10 * 2.0F - var9;
      this.field_78203_f.field_78796_g = var10 * 1.0F + var9;
      this.field_78204_g.field_78796_g = -var10 * 1.0F - var9;
      this.field_78212_h.field_78796_g = -var10 * 1.0F + var9;
      this.field_78213_i.field_78796_g = var10 * 1.0F - var9;
      this.field_78210_j.field_78796_g = -var10 * 2.0F + var9;
      this.field_78211_k.field_78796_g = var10 * 2.0F - var9;
      float var11 = -(MathHelper.func_76134_b(var1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * var2;
      float var12 = -(MathHelper.func_76134_b(var1 * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * var2;
      float var13 = -(MathHelper.func_76134_b(var1 * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * var2;
      float var14 = -(MathHelper.func_76134_b(var1 * 0.6662F * 2.0F + 4.712389F) * 0.4F) * var2;
      float var15 = Math.abs(MathHelper.func_76126_a(var1 * 0.6662F + 0.0F) * 0.4F) * var2;
      float var16 = Math.abs(MathHelper.func_76126_a(var1 * 0.6662F + 3.1415927F) * 0.4F) * var2;
      float var17 = Math.abs(MathHelper.func_76126_a(var1 * 0.6662F + 1.5707964F) * 0.4F) * var2;
      float var18 = Math.abs(MathHelper.func_76126_a(var1 * 0.6662F + 4.712389F) * 0.4F) * var2;
      ModelRenderer var10000 = this.field_78205_d;
      var10000.field_78796_g += var11;
      var10000 = this.field_78206_e;
      var10000.field_78796_g += -var11;
      var10000 = this.field_78203_f;
      var10000.field_78796_g += var12;
      var10000 = this.field_78204_g;
      var10000.field_78796_g += -var12;
      var10000 = this.field_78212_h;
      var10000.field_78796_g += var13;
      var10000 = this.field_78213_i;
      var10000.field_78796_g += -var13;
      var10000 = this.field_78210_j;
      var10000.field_78796_g += var14;
      var10000 = this.field_78211_k;
      var10000.field_78796_g += -var14;
      var10000 = this.field_78205_d;
      var10000.field_78808_h += var15;
      var10000 = this.field_78206_e;
      var10000.field_78808_h += -var15;
      var10000 = this.field_78203_f;
      var10000.field_78808_h += var16;
      var10000 = this.field_78204_g;
      var10000.field_78808_h += -var16;
      var10000 = this.field_78212_h;
      var10000.field_78808_h += var17;
      var10000 = this.field_78213_i;
      var10000.field_78808_h += -var17;
      var10000 = this.field_78210_j;
      var10000.field_78808_h += var18;
      var10000 = this.field_78211_k;
      var10000.field_78808_h += -var18;
   }
}
