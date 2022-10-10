package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelSalmon extends ModelBase {
   private final ModelRenderer field_203761_a;
   private final ModelRenderer field_203762_b;
   private final ModelRenderer field_203763_c;
   private final ModelRenderer field_203764_d;
   private final ModelRenderer field_203765_e;
   private final ModelRenderer field_203766_f;
   private final ModelRenderer field_203767_g;
   private final ModelRenderer field_203768_h;

   public ModelSalmon() {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      boolean var1 = true;
      this.field_203761_a = new ModelRenderer(this, 0, 0);
      this.field_203761_a.func_78789_a(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203761_a.func_78793_a(0.0F, 20.0F, 0.0F);
      this.field_203762_b = new ModelRenderer(this, 0, 13);
      this.field_203762_b.func_78789_a(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203762_b.func_78793_a(0.0F, 20.0F, 8.0F);
      this.field_203763_c = new ModelRenderer(this, 22, 0);
      this.field_203763_c.func_78789_a(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203763_c.func_78793_a(0.0F, 20.0F, 0.0F);
      this.field_203766_f = new ModelRenderer(this, 20, 10);
      this.field_203766_f.func_78789_a(0.0F, -2.5F, 0.0F, 0, 5, 6);
      this.field_203766_f.func_78793_a(0.0F, 0.0F, 8.0F);
      this.field_203762_b.func_78792_a(this.field_203766_f);
      this.field_203764_d = new ModelRenderer(this, 2, 1);
      this.field_203764_d.func_78789_a(0.0F, 0.0F, 0.0F, 0, 2, 3);
      this.field_203764_d.func_78793_a(0.0F, -4.5F, 5.0F);
      this.field_203761_a.func_78792_a(this.field_203764_d);
      this.field_203765_e = new ModelRenderer(this, 0, 2);
      this.field_203765_e.func_78789_a(0.0F, 0.0F, 0.0F, 0, 2, 4);
      this.field_203765_e.func_78793_a(0.0F, -4.5F, -1.0F);
      this.field_203762_b.func_78792_a(this.field_203765_e);
      this.field_203767_g = new ModelRenderer(this, -4, 0);
      this.field_203767_g.func_78789_a(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203767_g.func_78793_a(-1.5F, 21.5F, 0.0F);
      this.field_203767_g.field_78808_h = -0.7853982F;
      this.field_203768_h = new ModelRenderer(this, 0, 0);
      this.field_203768_h.func_78789_a(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203768_h.func_78793_a(1.5F, 21.5F, 0.0F);
      this.field_203768_h.field_78808_h = 0.7853982F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_203761_a.func_78785_a(var7);
      this.field_203762_b.func_78785_a(var7);
      this.field_203763_c.func_78785_a(var7);
      this.field_203767_g.func_78785_a(var7);
      this.field_203768_h.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = 1.0F;
      float var9 = 1.0F;
      if (!var7.func_70090_H()) {
         var8 = 1.3F;
         var9 = 1.7F;
      }

      this.field_203762_b.field_78796_g = -var8 * 0.25F * MathHelper.func_76126_a(var9 * 0.6F * var3);
   }
}
