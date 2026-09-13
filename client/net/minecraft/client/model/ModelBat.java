package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;

public class ModelBat extends ModelBase {
   private ModelRenderer field_82895_a;
   private ModelRenderer field_82893_b;
   private ModelRenderer field_82894_c;
   private ModelRenderer field_82891_d;
   private ModelRenderer field_82892_e;
   private ModelRenderer field_82890_f;

   public ModelBat() {
      super();
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.field_82895_a = new ModelRenderer(this, 0, 0);
      this.field_82895_a.func_78789_a(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      ModelRenderer var1 = new ModelRenderer(this, 24, 0);
      var1.func_78789_a(-4.0F, -6.0F, -2.0F, 3, 4, 1);
      this.field_82895_a.func_78792_a(var1);
      ModelRenderer var2 = new ModelRenderer(this, 24, 0);
      var2.field_78809_i = true;
      var2.func_78789_a(1.0F, -6.0F, -2.0F, 3, 4, 1);
      this.field_82895_a.func_78792_a(var2);
      this.field_82893_b = new ModelRenderer(this, 0, 16);
      this.field_82893_b.func_78789_a(-3.0F, 4.0F, -3.0F, 6, 12, 6);
      this.field_82893_b.func_78784_a(0, 34).func_78789_a(-5.0F, 16.0F, 0.0F, 10, 6, 1);
      this.field_82894_c = new ModelRenderer(this, 42, 0);
      this.field_82894_c.func_78789_a(-12.0F, 1.0F, 1.5F, 10, 16, 1);
      this.field_82892_e = new ModelRenderer(this, 24, 16);
      this.field_82892_e.func_78793_a(-12.0F, 1.0F, 1.5F);
      this.field_82892_e.func_78789_a(-8.0F, 1.0F, 0.0F, 8, 12, 1);
      this.field_82891_d = new ModelRenderer(this, 42, 0);
      this.field_82891_d.field_78809_i = true;
      this.field_82891_d.func_78789_a(2.0F, 1.0F, 1.5F, 10, 16, 1);
      this.field_82890_f = new ModelRenderer(this, 24, 16);
      this.field_82890_f.field_78809_i = true;
      this.field_82890_f.func_78793_a(12.0F, 1.0F, 1.5F);
      this.field_82890_f.func_78789_a(0.0F, 1.0F, 0.0F, 8, 12, 1);
      this.field_82893_b.func_78792_a(this.field_82894_c);
      this.field_82893_b.func_78792_a(this.field_82891_d);
      this.field_82894_c.func_78792_a(this.field_82892_e);
      this.field_82891_d.func_78792_a(this.field_82890_f);
   }

   public int func_82889_a() {
      return 36;
   }

   @Override
   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      EntityBat var8 = (EntityBat)var1;
      if (var8.func_82235_h()) {
         float var9 = 57.295776F;
         this.field_82895_a.field_78795_f = var6 / 57.295776F;
         this.field_82895_a.field_78796_g = 3.1415927F - var5 / 57.295776F;
         this.field_82895_a.field_78808_h = 3.1415927F;
         this.field_82895_a.func_78793_a(0.0F, -2.0F, 0.0F);
         this.field_82894_c.func_78793_a(-3.0F, 0.0F, 3.0F);
         this.field_82891_d.func_78793_a(3.0F, 0.0F, 3.0F);
         this.field_82893_b.field_78795_f = 3.1415927F;
         this.field_82894_c.field_78795_f = -0.15707964F;
         this.field_82894_c.field_78796_g = -1.2566371F;
         this.field_82892_e.field_78796_g = -1.7278761F;
         this.field_82891_d.field_78795_f = this.field_82894_c.field_78795_f;
         this.field_82891_d.field_78796_g = -this.field_82894_c.field_78796_g;
         this.field_82890_f.field_78796_g = -this.field_82892_e.field_78796_g;
      } else {
         float var10 = 57.295776F;
         this.field_82895_a.field_78795_f = var6 / 57.295776F;
         this.field_82895_a.field_78796_g = var5 / 57.295776F;
         this.field_82895_a.field_78808_h = 0.0F;
         this.field_82895_a.func_78793_a(0.0F, 0.0F, 0.0F);
         this.field_82894_c.func_78793_a(0.0F, 0.0F, 0.0F);
         this.field_82891_d.func_78793_a(0.0F, 0.0F, 0.0F);
         this.field_82893_b.field_78795_f = 0.7853982F + MathHelper.func_76134_b(var4 * 0.1F) * 0.15F;
         this.field_82893_b.field_78796_g = 0.0F;
         this.field_82894_c.field_78796_g = MathHelper.func_76134_b(var4 * 1.3F) * 3.1415927F * 0.25F;
         this.field_82891_d.field_78796_g = -this.field_82894_c.field_78796_g;
         this.field_82892_e.field_78796_g = this.field_82894_c.field_78796_g * 0.5F;
         this.field_82890_f.field_78796_g = -this.field_82894_c.field_78796_g * 0.5F;
      }

      this.field_82895_a.func_78785_a(var7);
      this.field_82893_b.func_78785_a(var7);
   }
}
