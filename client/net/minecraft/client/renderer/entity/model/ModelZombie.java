package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;

public class ModelZombie extends ModelBiped {
   public ModelZombie() {
      this(0.0F, false);
   }

   protected ModelZombie(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public ModelZombie(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      boolean var8 = var7 instanceof EntityZombie && ((EntityZombie)var7).func_184734_db();
      float var9 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F);
      float var10 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * 3.1415927F);
      this.field_178723_h.field_78808_h = 0.0F;
      this.field_178724_i.field_78808_h = 0.0F;
      this.field_178723_h.field_78796_g = -(0.1F - var9 * 0.6F);
      this.field_178724_i.field_78796_g = 0.1F - var9 * 0.6F;
      float var11 = -3.1415927F / (var8 ? 1.5F : 2.25F);
      this.field_178723_h.field_78795_f = var11;
      this.field_178724_i.field_78795_f = var11;
      ModelRenderer var10000 = this.field_178723_h;
      var10000.field_78795_f += var9 * 1.2F - var10 * 0.4F;
      var10000 = this.field_178724_i;
      var10000.field_78795_f += var9 * 1.2F - var10 * 0.4F;
      var10000 = this.field_178723_h;
      var10000.field_78808_h += MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.field_178724_i;
      var10000.field_78808_h -= MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.field_178723_h;
      var10000.field_78795_f += MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
      var10000 = this.field_178724_i;
      var10000.field_78795_f -= MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
   }
}
