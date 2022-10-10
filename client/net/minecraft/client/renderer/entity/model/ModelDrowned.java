package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelDrowned extends ModelZombie {
   public ModelDrowned(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.field_178723_h = new ModelRenderer(this, 32, 48);
      this.field_178723_h.func_78790_a(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.field_178723_h.func_78793_a(-5.0F, 2.0F + var2, 0.0F);
      this.field_178721_j = new ModelRenderer(this, 16, 48);
      this.field_178721_j.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_178721_j.func_78793_a(-1.9F, 12.0F + var2, 0.0F);
   }

   public ModelDrowned(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      this.field_187076_m = ModelBiped.ArmPose.EMPTY;
      this.field_187075_l = ModelBiped.ArmPose.EMPTY;
      ItemStack var5 = var1.func_184586_b(EnumHand.MAIN_HAND);
      if (var5.func_77973_b() == Items.field_203184_eO && ((EntityDrowned)var1).func_184734_db()) {
         if (var1.func_184591_cq() == EnumHandSide.RIGHT) {
            this.field_187076_m = ModelBiped.ArmPose.THROW_SPEAR;
         } else {
            this.field_187075_l = ModelBiped.ArmPose.THROW_SPEAR;
         }
      }

      super.func_78086_a(var1, var2, var3, var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      if (this.field_187075_l == ModelBiped.ArmPose.THROW_SPEAR) {
         this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - 3.1415927F;
         this.field_178724_i.field_78796_g = 0.0F;
      }

      if (this.field_187076_m == ModelBiped.ArmPose.THROW_SPEAR) {
         this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - 3.1415927F;
         this.field_178723_h.field_78796_g = 0.0F;
      }

      if (this.field_205061_a > 0.0F) {
         this.field_178723_h.field_78795_f = this.func_205060_a(this.field_178723_h.field_78795_f, -2.5132742F, this.field_205061_a) + this.field_205061_a * 0.35F * MathHelper.func_76126_a(0.1F * var3);
         this.field_178724_i.field_78795_f = this.func_205060_a(this.field_178724_i.field_78795_f, -2.5132742F, this.field_205061_a) - this.field_205061_a * 0.35F * MathHelper.func_76126_a(0.1F * var3);
         this.field_178723_h.field_78808_h = this.func_205060_a(this.field_178723_h.field_78808_h, -0.15F, this.field_205061_a);
         this.field_178724_i.field_78808_h = this.func_205060_a(this.field_178724_i.field_78808_h, 0.15F, this.field_205061_a);
         ModelRenderer var10000 = this.field_178722_k;
         var10000.field_78795_f -= this.field_205061_a * 0.55F * MathHelper.func_76126_a(0.1F * var3);
         var10000 = this.field_178721_j;
         var10000.field_78795_f += this.field_205061_a * 0.55F * MathHelper.func_76126_a(0.1F * var3);
         this.field_78116_c.field_78795_f = 0.0F;
      }

   }
}
