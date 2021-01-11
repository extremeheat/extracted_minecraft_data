package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;

public class ModelSkeleton extends ModelZombie {
   public ModelSkeleton() {
      this(0.0F, false);
   }

   public ModelSkeleton(float var1, boolean var2) {
      super(var1, 0.0F, 64, 32);
      if (!var2) {
         this.field_178723_h = new ModelRenderer(this, 40, 16);
         this.field_178723_h.func_78790_a(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
         this.field_178723_h.func_78793_a(-5.0F, 2.0F, 0.0F);
         this.field_178724_i = new ModelRenderer(this, 40, 16);
         this.field_178724_i.field_78809_i = true;
         this.field_178724_i.func_78790_a(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
         this.field_178724_i.func_78793_a(5.0F, 2.0F, 0.0F);
         this.field_178721_j = new ModelRenderer(this, 0, 16);
         this.field_178721_j.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
         this.field_178721_j.func_78793_a(-2.0F, 12.0F, 0.0F);
         this.field_178722_k = new ModelRenderer(this, 0, 16);
         this.field_178722_k.field_78809_i = true;
         this.field_178722_k.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
         this.field_178722_k.func_78793_a(2.0F, 12.0F, 0.0F);
      }

   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      this.field_78118_o = ((EntitySkeleton)var1).func_82202_m() == 1;
      super.func_78086_a(var1, var2, var3, var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
   }
}
