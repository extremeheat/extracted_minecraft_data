package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.math.MathHelper;

public class ModelTurtle extends ModelQuadruped {
   private final ModelRenderer field_203078_i;

   public ModelTurtle(float var1) {
      super(12, var1);
      this.field_78090_t = 128;
      this.field_78089_u = 64;
      this.field_78150_a = new ModelRenderer(this, 3, 0);
      this.field_78150_a.func_78790_a(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
      this.field_78150_a.func_78793_a(0.0F, 19.0F, -10.0F);
      this.field_78148_b = new ModelRenderer(this);
      this.field_78148_b.func_78784_a(7, 37).func_78790_a(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
      this.field_78148_b.func_78784_a(31, 1).func_78790_a(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
      this.field_78148_b.func_78793_a(0.0F, 11.0F, -10.0F);
      this.field_203078_i = new ModelRenderer(this);
      this.field_203078_i.func_78784_a(70, 33).func_78790_a(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
      this.field_203078_i.func_78793_a(0.0F, 11.0F, -10.0F);
      boolean var2 = true;
      this.field_78149_c = new ModelRenderer(this, 1, 23);
      this.field_78149_c.func_78790_a(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.field_78149_c.func_78793_a(-3.5F, 22.0F, 11.0F);
      this.field_78146_d = new ModelRenderer(this, 1, 12);
      this.field_78146_d.func_78790_a(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.field_78146_d.func_78793_a(3.5F, 22.0F, 11.0F);
      this.field_78147_e = new ModelRenderer(this, 27, 30);
      this.field_78147_e.func_78790_a(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.field_78147_e.func_78793_a(-5.0F, 21.0F, -4.0F);
      this.field_78144_f = new ModelRenderer(this, 27, 24);
      this.field_78144_f.func_78790_a(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.field_78144_f.func_78793_a(5.0F, 21.0F, -4.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      EntityTurtle var8 = (EntityTurtle)var1;
      if (this.field_78091_s) {
         float var9 = 6.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.16666667F, 0.16666667F, 0.16666667F);
         GlStateManager.func_179109_b(0.0F, 120.0F * var7, 0.0F);
         this.field_78150_a.func_78785_a(var7);
         this.field_78148_b.func_78785_a(var7);
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         GlStateManager.func_179094_E();
         if (var8.func_203020_dx()) {
            GlStateManager.func_179109_b(0.0F, -0.08F, 0.0F);
         }

         this.field_78150_a.func_78785_a(var7);
         this.field_78148_b.func_78785_a(var7);
         GlStateManager.func_179094_E();
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         GlStateManager.func_179121_F();
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
         if (var8.func_203020_dx()) {
            this.field_203078_i.func_78785_a(var7);
         }

         GlStateManager.func_179121_F();
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      EntityTurtle var8 = (EntityTurtle)var7;
      this.field_78149_c.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F * 0.6F) * 0.5F * var2;
      this.field_78146_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var2;
      this.field_78147_e.field_78808_h = MathHelper.func_76134_b(var1 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var2;
      this.field_78144_f.field_78808_h = MathHelper.func_76134_b(var1 * 0.6662F * 0.6F) * 0.5F * var2;
      this.field_78147_e.field_78795_f = 0.0F;
      this.field_78144_f.field_78795_f = 0.0F;
      this.field_78147_e.field_78796_g = 0.0F;
      this.field_78144_f.field_78796_g = 0.0F;
      this.field_78149_c.field_78796_g = 0.0F;
      this.field_78146_d.field_78796_g = 0.0F;
      this.field_203078_i.field_78795_f = 1.5707964F;
      if (!var8.func_70090_H() && var8.field_70122_E) {
         float var9 = var8.func_203023_dy() ? 4.0F : 1.0F;
         float var10 = var8.func_203023_dy() ? 2.0F : 1.0F;
         float var11 = 5.0F;
         this.field_78147_e.field_78796_g = MathHelper.func_76134_b(var9 * var1 * 5.0F + 3.1415927F) * 8.0F * var2 * var10;
         this.field_78147_e.field_78808_h = 0.0F;
         this.field_78144_f.field_78796_g = MathHelper.func_76134_b(var9 * var1 * 5.0F) * 8.0F * var2 * var10;
         this.field_78144_f.field_78808_h = 0.0F;
         this.field_78149_c.field_78796_g = MathHelper.func_76134_b(var1 * 5.0F + 3.1415927F) * 3.0F * var2;
         this.field_78149_c.field_78795_f = 0.0F;
         this.field_78146_d.field_78796_g = MathHelper.func_76134_b(var1 * 5.0F) * 3.0F * var2;
         this.field_78146_d.field_78795_f = 0.0F;
      }

   }
}
