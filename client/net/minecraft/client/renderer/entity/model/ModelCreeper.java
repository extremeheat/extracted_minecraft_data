package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelCreeper extends ModelBase {
   private final ModelRenderer field_78135_a;
   private final ModelRenderer field_78133_b;
   private final ModelRenderer field_78134_c;
   private final ModelRenderer field_78131_d;
   private final ModelRenderer field_78132_e;
   private final ModelRenderer field_78129_f;
   private final ModelRenderer field_78130_g;

   public ModelCreeper() {
      this(0.0F);
   }

   public ModelCreeper(float var1) {
      super();
      boolean var2 = true;
      this.field_78135_a = new ModelRenderer(this, 0, 0);
      this.field_78135_a.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
      this.field_78135_a.func_78793_a(0.0F, 6.0F, 0.0F);
      this.field_78133_b = new ModelRenderer(this, 32, 0);
      this.field_78133_b.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
      this.field_78133_b.func_78793_a(0.0F, 6.0F, 0.0F);
      this.field_78134_c = new ModelRenderer(this, 16, 16);
      this.field_78134_c.func_78790_a(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
      this.field_78134_c.func_78793_a(0.0F, 6.0F, 0.0F);
      this.field_78131_d = new ModelRenderer(this, 0, 16);
      this.field_78131_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78131_d.func_78793_a(-2.0F, 18.0F, 4.0F);
      this.field_78132_e = new ModelRenderer(this, 0, 16);
      this.field_78132_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78132_e.func_78793_a(2.0F, 18.0F, 4.0F);
      this.field_78129_f = new ModelRenderer(this, 0, 16);
      this.field_78129_f.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78129_f.func_78793_a(-2.0F, 18.0F, -4.0F);
      this.field_78130_g = new ModelRenderer(this, 0, 16);
      this.field_78130_g.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.field_78130_g.func_78793_a(2.0F, 18.0F, -4.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78135_a.func_78785_a(var7);
      this.field_78134_c.func_78785_a(var7);
      this.field_78131_d.func_78785_a(var7);
      this.field_78132_e.func_78785_a(var7);
      this.field_78129_f.func_78785_a(var7);
      this.field_78130_g.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_78135_a.field_78796_g = var4 * 0.017453292F;
      this.field_78135_a.field_78795_f = var5 * 0.017453292F;
      this.field_78131_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2;
      this.field_78132_e.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.field_78129_f.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.field_78130_g.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2;
   }
}
