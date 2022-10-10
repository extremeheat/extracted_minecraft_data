package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;

public class ModelShulkerBullet extends ModelBase {
   private final ModelRenderer field_187069_a;

   public ModelShulkerBullet() {
      super();
      this.field_78090_t = 64;
      this.field_78089_u = 32;
      this.field_187069_a = new ModelRenderer(this);
      this.field_187069_a.func_78784_a(0, 0).func_78790_a(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
      this.field_187069_a.func_78784_a(0, 10).func_78790_a(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
      this.field_187069_a.func_78784_a(20, 0).func_78790_a(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
      this.field_187069_a.func_78793_a(0.0F, 0.0F, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_187069_a.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_187069_a.field_78796_g = var4 * 0.017453292F;
      this.field_187069_a.field_78795_f = var5 * 0.017453292F;
   }
}
