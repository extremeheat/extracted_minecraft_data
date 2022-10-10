package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelCod extends ModelBase {
   private final ModelRenderer field_203723_a;
   private final ModelRenderer field_203724_b;
   private final ModelRenderer field_203725_c;
   private final ModelRenderer field_203726_d;
   private final ModelRenderer field_203727_e;
   private final ModelRenderer field_203728_f;
   private final ModelRenderer field_203729_g;

   public ModelCod() {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      boolean var1 = true;
      this.field_203723_a = new ModelRenderer(this, 0, 0);
      this.field_203723_a.func_78789_a(-1.0F, -2.0F, 0.0F, 2, 4, 7);
      this.field_203723_a.func_78793_a(0.0F, 22.0F, 0.0F);
      this.field_203725_c = new ModelRenderer(this, 11, 0);
      this.field_203725_c.func_78789_a(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203725_c.func_78793_a(0.0F, 22.0F, 0.0F);
      this.field_203726_d = new ModelRenderer(this, 0, 0);
      this.field_203726_d.func_78789_a(-1.0F, -2.0F, -1.0F, 2, 3, 1);
      this.field_203726_d.func_78793_a(0.0F, 22.0F, -3.0F);
      this.field_203727_e = new ModelRenderer(this, 22, 1);
      this.field_203727_e.func_78789_a(-2.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203727_e.func_78793_a(-1.0F, 23.0F, 0.0F);
      this.field_203727_e.field_78808_h = -0.7853982F;
      this.field_203728_f = new ModelRenderer(this, 22, 4);
      this.field_203728_f.func_78789_a(0.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203728_f.func_78793_a(1.0F, 23.0F, 0.0F);
      this.field_203728_f.field_78808_h = 0.7853982F;
      this.field_203729_g = new ModelRenderer(this, 22, 3);
      this.field_203729_g.func_78789_a(0.0F, -2.0F, 0.0F, 0, 4, 4);
      this.field_203729_g.func_78793_a(0.0F, 22.0F, 7.0F);
      this.field_203724_b = new ModelRenderer(this, 20, -6);
      this.field_203724_b.func_78789_a(0.0F, -1.0F, -1.0F, 0, 1, 6);
      this.field_203724_b.func_78793_a(0.0F, 20.0F, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_203723_a.func_78785_a(var7);
      this.field_203725_c.func_78785_a(var7);
      this.field_203726_d.func_78785_a(var7);
      this.field_203727_e.func_78785_a(var7);
      this.field_203728_f.func_78785_a(var7);
      this.field_203729_g.func_78785_a(var7);
      this.field_203724_b.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = 1.0F;
      if (!var7.func_70090_H()) {
         var8 = 1.5F;
      }

      this.field_203729_g.field_78796_g = -var8 * 0.45F * MathHelper.func_76126_a(0.6F * var3);
   }
}
