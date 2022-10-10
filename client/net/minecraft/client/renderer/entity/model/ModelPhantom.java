package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelPhantom extends ModelBase {
   private final ModelRenderer field_203070_a;
   private final ModelRenderer field_203071_b;
   private final ModelRenderer field_203072_c;
   private final ModelRenderer field_203073_d;
   private final ModelRenderer field_203074_e;
   private final ModelRenderer field_203075_f;
   private final ModelRenderer field_204233_g;
   private final ModelRenderer field_204234_h;

   public ModelPhantom() {
      super();
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.field_203070_a = new ModelRenderer(this, 0, 8);
      this.field_203070_a.func_78789_a(-3.0F, -2.0F, -8.0F, 5, 3, 9);
      this.field_204233_g = new ModelRenderer(this, 3, 20);
      this.field_204233_g.func_78789_a(-2.0F, 0.0F, 0.0F, 3, 2, 6);
      this.field_204233_g.func_78793_a(0.0F, -2.0F, 1.0F);
      this.field_203070_a.func_78792_a(this.field_204233_g);
      this.field_204234_h = new ModelRenderer(this, 4, 29);
      this.field_204234_h.func_78789_a(-1.0F, 0.0F, 0.0F, 1, 1, 6);
      this.field_204234_h.func_78793_a(0.0F, 0.5F, 6.0F);
      this.field_204233_g.func_78792_a(this.field_204234_h);
      this.field_203071_b = new ModelRenderer(this, 23, 12);
      this.field_203071_b.func_78789_a(0.0F, 0.0F, 0.0F, 6, 2, 9);
      this.field_203071_b.func_78793_a(2.0F, -2.0F, -8.0F);
      this.field_203072_c = new ModelRenderer(this, 16, 24);
      this.field_203072_c.func_78789_a(0.0F, 0.0F, 0.0F, 13, 1, 9);
      this.field_203072_c.func_78793_a(6.0F, 0.0F, 0.0F);
      this.field_203071_b.func_78792_a(this.field_203072_c);
      this.field_203073_d = new ModelRenderer(this, 23, 12);
      this.field_203073_d.field_78809_i = true;
      this.field_203073_d.func_78789_a(-6.0F, 0.0F, 0.0F, 6, 2, 9);
      this.field_203073_d.func_78793_a(-3.0F, -2.0F, -8.0F);
      this.field_203074_e = new ModelRenderer(this, 16, 24);
      this.field_203074_e.field_78809_i = true;
      this.field_203074_e.func_78789_a(-13.0F, 0.0F, 0.0F, 13, 1, 9);
      this.field_203074_e.func_78793_a(-6.0F, 0.0F, 0.0F);
      this.field_203073_d.func_78792_a(this.field_203074_e);
      this.field_203071_b.field_78808_h = 0.1F;
      this.field_203072_c.field_78808_h = 0.1F;
      this.field_203073_d.field_78808_h = -0.1F;
      this.field_203074_e.field_78808_h = -0.1F;
      this.field_203070_a.field_78795_f = -0.1F;
      this.field_203075_f = new ModelRenderer(this, 0, 0);
      this.field_203075_f.func_78789_a(-4.0F, -2.0F, -5.0F, 7, 3, 5);
      this.field_203075_f.func_78793_a(0.0F, 1.0F, -7.0F);
      this.field_203075_f.field_78795_f = 0.2F;
      this.field_203070_a.func_78792_a(this.field_203075_f);
      this.field_203070_a.func_78792_a(this.field_203071_b);
      this.field_203070_a.func_78792_a(this.field_203073_d);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.field_203070_a.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = ((float)(var7.func_145782_y() * 3) + var3) * 0.13F;
      float var9 = 16.0F;
      this.field_203071_b.field_78808_h = (0.0F + MathHelper.func_76134_b(var8) * 16.0F) * 0.017453292F;
      this.field_203072_c.field_78808_h = (0.0F + MathHelper.func_76134_b(var8) * 16.0F) * 0.017453292F;
      this.field_203073_d.field_78808_h = -this.field_203071_b.field_78808_h;
      this.field_203074_e.field_78808_h = -this.field_203072_c.field_78808_h;
      this.field_204233_g.field_78795_f = -(5.0F + MathHelper.func_76134_b(var8 * 2.0F) * 5.0F) * 0.017453292F;
      this.field_204234_h.field_78795_f = -(5.0F + MathHelper.func_76134_b(var8 * 2.0F) * 5.0F) * 0.017453292F;
   }
}
