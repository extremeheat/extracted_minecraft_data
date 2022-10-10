package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelWitch extends ModelVillager {
   private boolean field_82900_g;
   private final ModelRenderer field_82901_h = (new ModelRenderer(this)).func_78787_b(64, 128);
   private final ModelRenderer field_82902_i;

   public ModelWitch(float var1) {
      super(var1, 0.0F, 64, 128);
      this.field_82901_h.func_78793_a(0.0F, -2.0F, 0.0F);
      this.field_82901_h.func_78784_a(0, 0).func_78790_a(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.field_82898_f.func_78792_a(this.field_82901_h);
      this.field_82902_i = (new ModelRenderer(this)).func_78787_b(64, 128);
      this.field_82902_i.func_78793_a(-5.0F, -10.03125F, -5.0F);
      this.field_82902_i.func_78784_a(0, 64).func_78789_a(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.field_78191_a.func_78792_a(this.field_82902_i);
      ModelRenderer var2 = (new ModelRenderer(this)).func_78787_b(64, 128);
      var2.func_78793_a(1.75F, -4.0F, 2.0F);
      var2.func_78784_a(0, 76).func_78789_a(0.0F, 0.0F, 0.0F, 7, 4, 7);
      var2.field_78795_f = -0.05235988F;
      var2.field_78808_h = 0.02617994F;
      this.field_82902_i.func_78792_a(var2);
      ModelRenderer var3 = (new ModelRenderer(this)).func_78787_b(64, 128);
      var3.func_78793_a(1.75F, -4.0F, 2.0F);
      var3.func_78784_a(0, 87).func_78789_a(0.0F, 0.0F, 0.0F, 4, 4, 4);
      var3.field_78795_f = -0.10471976F;
      var3.field_78808_h = 0.05235988F;
      var2.func_78792_a(var3);
      ModelRenderer var4 = (new ModelRenderer(this)).func_78787_b(64, 128);
      var4.func_78793_a(1.75F, -2.0F, 2.0F);
      var4.func_78784_a(0, 95).func_78790_a(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      var4.field_78795_f = -0.20943952F;
      var4.field_78808_h = 0.10471976F;
      var3.func_78792_a(var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_82898_f.field_82906_o = 0.0F;
      this.field_82898_f.field_82908_p = 0.0F;
      this.field_82898_f.field_82907_q = 0.0F;
      float var8 = 0.01F * (float)(var7.func_145782_y() % 10);
      this.field_82898_f.field_78795_f = MathHelper.func_76126_a((float)var7.field_70173_aa * var8) * 4.5F * 0.017453292F;
      this.field_82898_f.field_78796_g = 0.0F;
      this.field_82898_f.field_78808_h = MathHelper.func_76134_b((float)var7.field_70173_aa * var8) * 2.5F * 0.017453292F;
      if (this.field_82900_g) {
         this.field_82898_f.field_78795_f = -0.9F;
         this.field_82898_f.field_82907_q = -0.09375F;
         this.field_82898_f.field_82908_p = 0.1875F;
      }

   }

   public ModelRenderer func_205073_b() {
      return this.field_82898_f;
   }

   public void func_205074_a(boolean var1) {
      this.field_82900_g = var1;
   }
}
