package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPolarBear;

public class ModelPolarBear extends ModelQuadruped {
   public ModelPolarBear() {
      super(12, 0.0F);
      this.field_78090_t = 128;
      this.field_78089_u = 64;
      this.field_78150_a = new ModelRenderer(this, 0, 0);
      this.field_78150_a.func_78790_a(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
      this.field_78150_a.func_78793_a(0.0F, 10.0F, -16.0F);
      this.field_78150_a.func_78784_a(0, 44).func_78790_a(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
      this.field_78150_a.func_78784_a(26, 0).func_78790_a(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      ModelRenderer var1 = this.field_78150_a.func_78784_a(26, 0);
      var1.field_78809_i = true;
      var1.func_78790_a(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      this.field_78148_b = new ModelRenderer(this);
      this.field_78148_b.func_78784_a(0, 19).func_78790_a(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
      this.field_78148_b.func_78784_a(39, 0).func_78790_a(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
      this.field_78148_b.func_78793_a(-2.0F, 9.0F, 12.0F);
      boolean var2 = true;
      this.field_78149_c = new ModelRenderer(this, 50, 22);
      this.field_78149_c.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.field_78149_c.func_78793_a(-3.5F, 14.0F, 6.0F);
      this.field_78146_d = new ModelRenderer(this, 50, 22);
      this.field_78146_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.field_78146_d.func_78793_a(3.5F, 14.0F, 6.0F);
      this.field_78147_e = new ModelRenderer(this, 50, 40);
      this.field_78147_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.field_78147_e.func_78793_a(-2.5F, 14.0F, -7.0F);
      this.field_78144_f = new ModelRenderer(this, 50, 40);
      this.field_78144_f.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.field_78144_f.func_78793_a(2.5F, 14.0F, -7.0F);
      --this.field_78149_c.field_78800_c;
      ++this.field_78146_d.field_78800_c;
      ModelRenderer var10000 = this.field_78149_c;
      var10000.field_78798_e += 0.0F;
      var10000 = this.field_78146_d;
      var10000.field_78798_e += 0.0F;
      --this.field_78147_e.field_78800_c;
      ++this.field_78144_f.field_78800_c;
      --this.field_78147_e.field_78798_e;
      --this.field_78144_f.field_78798_e;
      this.field_78151_h += 2.0F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      if (this.field_78091_s) {
         float var8 = 2.0F;
         this.field_78145_g = 16.0F;
         this.field_78151_h = 4.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.6666667F, 0.6666667F, 0.6666667F);
         GlStateManager.func_179109_b(0.0F, this.field_78145_g * var7, this.field_78151_h * var7);
         this.field_78150_a.func_78785_a(var7);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_78148_b.func_78785_a(var7);
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         this.field_78150_a.func_78785_a(var7);
         this.field_78148_b.func_78785_a(var7);
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      float var8 = var3 - (float)var7.field_70173_aa;
      float var9 = ((EntityPolarBear)var7).func_189795_r(var8);
      var9 *= var9;
      float var10 = 1.0F - var9;
      this.field_78148_b.field_78795_f = 1.5707964F - var9 * 3.1415927F * 0.35F;
      this.field_78148_b.field_78797_d = 9.0F * var10 + 11.0F * var9;
      this.field_78147_e.field_78797_d = 14.0F * var10 - 6.0F * var9;
      this.field_78147_e.field_78798_e = -8.0F * var10 - 4.0F * var9;
      this.field_78147_e.field_78795_f -= var9 * 3.1415927F * 0.45F;
      this.field_78144_f.field_78797_d = this.field_78147_e.field_78797_d;
      this.field_78144_f.field_78798_e = this.field_78147_e.field_78798_e;
      this.field_78144_f.field_78795_f -= var9 * 3.1415927F * 0.45F;
      this.field_78150_a.field_78797_d = 10.0F * var10 - 12.0F * var9;
      this.field_78150_a.field_78798_e = -16.0F * var10 - 3.0F * var9;
      this.field_78150_a.field_78795_f += var9 * 3.1415927F * 0.15F;
   }
}
