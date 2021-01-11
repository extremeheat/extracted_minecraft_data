package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelZombieVillager extends ModelBiped {
   public ModelZombieVillager() {
      this(0.0F, 0.0F, false);
   }

   public ModelZombieVillager(float var1, float var2, boolean var3) {
      super(var1, 0.0F, 64, var3 ? 32 : 64);
      if (var3) {
         this.field_78116_c = new ModelRenderer(this, 0, 0);
         this.field_78116_c.func_78790_a(-4.0F, -10.0F, -4.0F, 8, 8, 8, var1);
         this.field_78116_c.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      } else {
         this.field_78116_c = new ModelRenderer(this);
         this.field_78116_c.func_78793_a(0.0F, 0.0F + var2, 0.0F);
         this.field_78116_c.func_78784_a(0, 32).func_78790_a(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
         this.field_78116_c.func_78784_a(24, 32).func_78790_a(-1.0F, -3.0F, -6.0F, 2, 4, 2, var1);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      float var8 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F);
      float var9 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * 3.1415927F);
      this.field_178723_h.field_78808_h = 0.0F;
      this.field_178724_i.field_78808_h = 0.0F;
      this.field_178723_h.field_78796_g = -(0.1F - var8 * 0.6F);
      this.field_178724_i.field_78796_g = 0.1F - var8 * 0.6F;
      this.field_178723_h.field_78795_f = -1.5707964F;
      this.field_178724_i.field_78795_f = -1.5707964F;
      ModelRenderer var10000 = this.field_178723_h;
      var10000.field_78795_f -= var8 * 1.2F - var9 * 0.4F;
      var10000 = this.field_178724_i;
      var10000.field_78795_f -= var8 * 1.2F - var9 * 0.4F;
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
