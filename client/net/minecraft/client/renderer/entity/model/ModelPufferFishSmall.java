package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelPufferFishSmall extends ModelBase {
   private final ModelRenderer field_203754_a;
   private final ModelRenderer field_203755_b;
   private final ModelRenderer field_203756_c;
   private final ModelRenderer field_203757_d;
   private final ModelRenderer field_203758_e;
   private final ModelRenderer field_203759_f;

   public ModelPufferFishSmall() {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      boolean var1 = true;
      this.field_203754_a = new ModelRenderer(this, 0, 27);
      this.field_203754_a.func_78789_a(-1.5F, -2.0F, -1.5F, 3, 2, 3);
      this.field_203754_a.func_78793_a(0.0F, 23.0F, 0.0F);
      this.field_203755_b = new ModelRenderer(this, 24, 6);
      this.field_203755_b.func_78789_a(-1.5F, 0.0F, -1.5F, 1, 1, 1);
      this.field_203755_b.func_78793_a(0.0F, 20.0F, 0.0F);
      this.field_203756_c = new ModelRenderer(this, 28, 6);
      this.field_203756_c.func_78789_a(0.5F, 0.0F, -1.5F, 1, 1, 1);
      this.field_203756_c.func_78793_a(0.0F, 20.0F, 0.0F);
      this.field_203759_f = new ModelRenderer(this, -3, 0);
      this.field_203759_f.func_78789_a(-1.5F, 0.0F, 0.0F, 3, 0, 3);
      this.field_203759_f.func_78793_a(0.0F, 22.0F, 1.5F);
      this.field_203757_d = new ModelRenderer(this, 25, 0);
      this.field_203757_d.func_78789_a(-1.0F, 0.0F, 0.0F, 1, 0, 2);
      this.field_203757_d.func_78793_a(-1.5F, 22.0F, -1.5F);
      this.field_203758_e = new ModelRenderer(this, 25, 0);
      this.field_203758_e.func_78789_a(0.0F, 0.0F, 0.0F, 1, 0, 2);
      this.field_203758_e.func_78793_a(1.5F, 22.0F, -1.5F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_203754_a.func_78785_a(var7);
      this.field_203755_b.func_78785_a(var7);
      this.field_203756_c.func_78785_a(var7);
      this.field_203759_f.func_78785_a(var7);
      this.field_203757_d.func_78785_a(var7);
      this.field_203758_e.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_203757_d.field_78808_h = -0.2F + 0.4F * MathHelper.func_76126_a(var3 * 0.2F);
      this.field_203758_e.field_78808_h = 0.2F - 0.4F * MathHelper.func_76126_a(var3 * 0.2F);
   }
}
