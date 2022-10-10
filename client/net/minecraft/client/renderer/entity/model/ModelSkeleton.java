package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelSkeleton extends ModelBiped {
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
      this.field_187076_m = ModelBiped.ArmPose.EMPTY;
      this.field_187075_l = ModelBiped.ArmPose.EMPTY;
      ItemStack var5 = var1.func_184586_b(EnumHand.MAIN_HAND);
      if (var5.func_77973_b() == Items.field_151031_f && ((AbstractSkeleton)var1).func_184725_db()) {
         if (var1.func_184591_cq() == EnumHandSide.RIGHT) {
            this.field_187076_m = ModelBiped.ArmPose.BOW_AND_ARROW;
         } else {
            this.field_187075_l = ModelBiped.ArmPose.BOW_AND_ARROW;
         }
      }

      super.func_78086_a(var1, var2, var3, var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      ItemStack var8 = ((EntityLivingBase)var7).func_184614_ca();
      AbstractSkeleton var9 = (AbstractSkeleton)var7;
      if (var9.func_184725_db() && (var8.func_190926_b() || var8.func_77973_b() != Items.field_151031_f)) {
         float var10 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F);
         float var11 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * 3.1415927F);
         this.field_178723_h.field_78808_h = 0.0F;
         this.field_178724_i.field_78808_h = 0.0F;
         this.field_178723_h.field_78796_g = -(0.1F - var10 * 0.6F);
         this.field_178724_i.field_78796_g = 0.1F - var10 * 0.6F;
         this.field_178723_h.field_78795_f = -1.5707964F;
         this.field_178724_i.field_78795_f = -1.5707964F;
         ModelRenderer var10000 = this.field_178723_h;
         var10000.field_78795_f -= var10 * 1.2F - var11 * 0.4F;
         var10000 = this.field_178724_i;
         var10000.field_78795_f -= var10 * 1.2F - var11 * 0.4F;
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

   public void func_187073_a(float var1, EnumHandSide var2) {
      float var3 = var2 == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      ModelRenderer var4 = this.func_187074_a(var2);
      var4.field_78800_c += var3;
      var4.func_78794_c(var1);
      var4.field_78800_c -= var3;
   }
}
