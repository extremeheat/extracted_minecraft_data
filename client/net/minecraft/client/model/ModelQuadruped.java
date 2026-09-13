package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelQuadruped extends ModelBase {
   public ModelRenderer field_78150_a;
   public ModelRenderer field_78148_b;
   public ModelRenderer field_78149_c;
   public ModelRenderer field_78146_d;
   public ModelRenderer field_78147_e;
   public ModelRenderer field_78144_f;
   protected float field_78145_g = 8.0F;
   protected float field_78151_h = 4.0F;

   public ModelQuadruped(int var1, float var2) {
      super();
      this.field_78150_a = new ModelRenderer(this, 0, 0);
      this.field_78150_a.func_78790_a(-4.0F, -4.0F, -8.0F, 8, 8, 8, var2);
      this.field_78150_a.func_78793_a(0.0F, (float)(18 - var1), -6.0F);
      this.field_78148_b = new ModelRenderer(this, 28, 8);
      this.field_78148_b.func_78790_a(-5.0F, -10.0F, -7.0F, 10, 16, 8, var2);
      this.field_78148_b.func_78793_a(0.0F, (float)(17 - var1), 2.0F);
      this.field_78149_c = new ModelRenderer(this, 0, 16);
      this.field_78149_c.func_78790_a(-2.0F, 0.0F, -2.0F, 4, var1, 4, var2);
      this.field_78149_c.func_78793_a(-3.0F, (float)(24 - var1), 7.0F);
      this.field_78146_d = new ModelRenderer(this, 0, 16);
      this.field_78146_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, var1, 4, var2);
      this.field_78146_d.func_78793_a(3.0F, (float)(24 - var1), 7.0F);
      this.field_78147_e = new ModelRenderer(this, 0, 16);
      this.field_78147_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, var1, 4, var2);
      this.field_78147_e.func_78793_a(-3.0F, (float)(24 - var1), -5.0F);
      this.field_78144_f = new ModelRenderer(this, 0, 16);
      this.field_78144_f.func_78790_a(-2.0F, 0.0F, -2.0F, 4, var1, 4, var2);
      this.field_78144_f.func_78793_a(3.0F, (float)(24 - var1), -5.0F);
   }

   @Override
   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, this.field_78145_g * var7, this.field_78151_h * var7);
         this.field_78150_a.func_78785_a(var7);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glScalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GL11.glTranslatef(0.0F, 24.0F * var7, 0.0F);
         this.field_78148_b.func_78785_a(var7);
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
         GL11.glPopMatrix();
      } else {
         this.field_78150_a.func_78785_a(var7);
         this.field_78148_b.func_78785_a(var7);
         this.field_78149_c.func_78785_a(var7);
         this.field_78146_d.func_78785_a(var7);
         this.field_78147_e.func_78785_a(var7);
         this.field_78144_f.func_78785_a(var7);
      }
   }

   @Override
   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = 57.295776F;
      this.field_78150_a.field_78795_f = var5 / 57.295776F;
      this.field_78150_a.field_78796_g = var4 / 57.295776F;
      this.field_78148_b.field_78795_f = 1.5707964F;
      this.field_78149_c.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2;
      this.field_78146_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.field_78147_e.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.field_78144_f.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2;
   }
}
