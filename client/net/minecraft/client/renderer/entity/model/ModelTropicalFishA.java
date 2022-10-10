package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTropicalFishA extends ModelBase {
   private final ModelRenderer field_204235_a;
   private final ModelRenderer field_204236_b;
   private final ModelRenderer field_204237_c;
   private final ModelRenderer field_204238_d;
   private final ModelRenderer field_204239_e;

   public ModelTropicalFishA() {
      this(0.0F);
   }

   public ModelTropicalFishA(float var1) {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      boolean var2 = true;
      this.field_204235_a = new ModelRenderer(this, 0, 0);
      this.field_204235_a.func_78790_a(-1.0F, -1.5F, -3.0F, 2, 3, 6, var1);
      this.field_204235_a.func_78793_a(0.0F, 22.0F, 0.0F);
      this.field_204236_b = new ModelRenderer(this, 22, -6);
      this.field_204236_b.func_78790_a(0.0F, -1.5F, 0.0F, 0, 3, 6, var1);
      this.field_204236_b.func_78793_a(0.0F, 22.0F, 3.0F);
      this.field_204237_c = new ModelRenderer(this, 2, 16);
      this.field_204237_c.func_78790_a(-2.0F, -1.0F, 0.0F, 2, 2, 0, var1);
      this.field_204237_c.func_78793_a(-1.0F, 22.5F, 0.0F);
      this.field_204237_c.field_78796_g = 0.7853982F;
      this.field_204238_d = new ModelRenderer(this, 2, 12);
      this.field_204238_d.func_78790_a(0.0F, -1.0F, 0.0F, 2, 2, 0, var1);
      this.field_204238_d.func_78793_a(1.0F, 22.5F, 0.0F);
      this.field_204238_d.field_78796_g = -0.7853982F;
      this.field_204239_e = new ModelRenderer(this, 10, -5);
      this.field_204239_e.func_78790_a(0.0F, -3.0F, 0.0F, 0, 3, 6, var1);
      this.field_204239_e.func_78793_a(0.0F, 20.5F, -3.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_204235_a.func_78785_a(var7);
      this.field_204236_b.func_78785_a(var7);
      this.field_204237_c.func_78785_a(var7);
      this.field_204238_d.func_78785_a(var7);
      this.field_204239_e.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = 1.0F;
      if (!var7.func_70090_H()) {
         var8 = 1.5F;
      }

      this.field_204236_b.field_78796_g = -var8 * 0.45F * MathHelper.func_76126_a(0.6F * var3);
   }
}
