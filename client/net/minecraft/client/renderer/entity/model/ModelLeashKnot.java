package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;

public class ModelLeashKnot extends ModelBase {
   private final ModelRenderer field_110723_a;

   public ModelLeashKnot() {
      this(0, 0, 32, 32);
   }

   public ModelLeashKnot(int var1, int var2, int var3, int var4) {
      super();
      this.field_78090_t = var3;
      this.field_78089_u = var4;
      this.field_110723_a = new ModelRenderer(this, var1, var2);
      this.field_110723_a.func_78790_a(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
      this.field_110723_a.func_78793_a(0.0F, 0.0F, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_110723_a.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_110723_a.field_78796_g = var4 * 0.017453292F;
      this.field_110723_a.field_78795_f = var5 * 0.017453292F;
   }
}
