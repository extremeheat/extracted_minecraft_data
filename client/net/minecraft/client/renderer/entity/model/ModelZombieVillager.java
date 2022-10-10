package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;

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
         this.field_78115_e = new ModelRenderer(this, 16, 16);
         this.field_78115_e.func_78793_a(0.0F, 0.0F + var2, 0.0F);
         this.field_78115_e.func_78790_a(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1 + 0.1F);
         this.field_178721_j = new ModelRenderer(this, 0, 16);
         this.field_178721_j.func_78793_a(-2.0F, 12.0F + var2, 0.0F);
         this.field_178721_j.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.1F);
         this.field_178722_k = new ModelRenderer(this, 0, 16);
         this.field_178722_k.field_78809_i = true;
         this.field_178722_k.func_78793_a(2.0F, 12.0F + var2, 0.0F);
         this.field_178722_k.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.1F);
      } else {
         this.field_78116_c = new ModelRenderer(this, 0, 0);
         this.field_78116_c.func_78793_a(0.0F, var2, 0.0F);
         this.field_78116_c.func_78784_a(0, 0).func_78790_a(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
         this.field_78116_c.func_78784_a(24, 0).func_78790_a(-1.0F, -3.0F, -6.0F, 2, 4, 2, var1);
         this.field_78115_e = new ModelRenderer(this, 16, 20);
         this.field_78115_e.func_78793_a(0.0F, 0.0F + var2, 0.0F);
         this.field_78115_e.func_78790_a(-4.0F, 0.0F, -3.0F, 8, 12, 6, var1);
         this.field_78115_e.func_78784_a(0, 38).func_78790_a(-4.0F, 0.0F, -3.0F, 8, 18, 6, var1 + 0.05F);
         this.field_178723_h = new ModelRenderer(this, 44, 38);
         this.field_178723_h.func_78790_a(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
         this.field_178723_h.func_78793_a(-5.0F, 2.0F + var2, 0.0F);
         this.field_178724_i = new ModelRenderer(this, 44, 38);
         this.field_178724_i.field_78809_i = true;
         this.field_178724_i.func_78790_a(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
         this.field_178724_i.func_78793_a(5.0F, 2.0F + var2, 0.0F);
         this.field_178721_j = new ModelRenderer(this, 0, 22);
         this.field_178721_j.func_78793_a(-2.0F, 12.0F + var2, 0.0F);
         this.field_178721_j.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
         this.field_178722_k = new ModelRenderer(this, 0, 22);
         this.field_178722_k.field_78809_i = true;
         this.field_178722_k.func_78793_a(2.0F, 12.0F + var2, 0.0F);
         this.field_178722_k.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      EntityZombie var8 = (EntityZombie)var7;
      float var9 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F);
      float var10 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * 3.1415927F);
      this.field_178723_h.field_78808_h = 0.0F;
      this.field_178724_i.field_78808_h = 0.0F;
      this.field_178723_h.field_78796_g = -(0.1F - var9 * 0.6F);
      this.field_178724_i.field_78796_g = 0.1F - var9 * 0.6F;
      float var11 = -3.1415927F / (var8.func_184734_db() ? 1.5F : 2.25F);
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
