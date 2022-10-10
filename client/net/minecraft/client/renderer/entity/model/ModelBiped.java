package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelBiped extends ModelBase {
   public ModelRenderer field_78116_c;
   public ModelRenderer field_178720_f;
   public ModelRenderer field_78115_e;
   public ModelRenderer field_178723_h;
   public ModelRenderer field_178724_i;
   public ModelRenderer field_178721_j;
   public ModelRenderer field_178722_k;
   public ModelBiped.ArmPose field_187075_l;
   public ModelBiped.ArmPose field_187076_m;
   public boolean field_78117_n;
   public float field_205061_a;

   public ModelBiped() {
      this(0.0F);
   }

   public ModelBiped(float var1) {
      this(var1, 0.0F, 64, 32);
   }

   public ModelBiped(float var1, float var2, int var3, int var4) {
      super();
      this.field_187075_l = ModelBiped.ArmPose.EMPTY;
      this.field_187076_m = ModelBiped.ArmPose.EMPTY;
      this.field_78090_t = var3;
      this.field_78089_u = var4;
      this.field_78116_c = new ModelRenderer(this, 0, 0);
      this.field_78116_c.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
      this.field_78116_c.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_178720_f = new ModelRenderer(this, 32, 0);
      this.field_178720_f.func_78790_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
      this.field_178720_f.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_78115_e = new ModelRenderer(this, 16, 16);
      this.field_78115_e.func_78790_a(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
      this.field_78115_e.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_178723_h = new ModelRenderer(this, 40, 16);
      this.field_178723_h.func_78790_a(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.field_178723_h.func_78793_a(-5.0F, 2.0F + var2, 0.0F);
      this.field_178724_i = new ModelRenderer(this, 40, 16);
      this.field_178724_i.field_78809_i = true;
      this.field_178724_i.func_78790_a(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.field_178724_i.func_78793_a(5.0F, 2.0F + var2, 0.0F);
      this.field_178721_j = new ModelRenderer(this, 0, 16);
      this.field_178721_j.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_178721_j.func_78793_a(-1.9F, 12.0F + var2, 0.0F);
      this.field_178722_k = new ModelRenderer(this, 0, 16);
      this.field_178722_k.field_78809_i = true;
      this.field_178722_k.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_178722_k.func_78793_a(1.9F, 12.0F + var2, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      GlStateManager.func_179094_E();
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GlStateManager.func_179152_a(0.75F, 0.75F, 0.75F);
         GlStateManager.func_179109_b(0.0F, 16.0F * var7, 0.0F);
         this.field_78116_c.func_78785_a(var7);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_78115_e.func_78785_a(var7);
         this.field_178723_h.func_78785_a(var7);
         this.field_178724_i.func_78785_a(var7);
         this.field_178721_j.func_78785_a(var7);
         this.field_178722_k.func_78785_a(var7);
         this.field_178720_f.func_78785_a(var7);
      } else {
         if (var1.func_70093_af()) {
            GlStateManager.func_179109_b(0.0F, 0.2F, 0.0F);
         }

         this.field_78116_c.func_78785_a(var7);
         this.field_78115_e.func_78785_a(var7);
         this.field_178723_h.func_78785_a(var7);
         this.field_178724_i.func_78785_a(var7);
         this.field_178721_j.func_78785_a(var7);
         this.field_178722_k.func_78785_a(var7);
         this.field_178720_f.func_78785_a(var7);
      }

      GlStateManager.func_179121_F();
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      this.field_205061_a = var1.func_205015_b(var4);
      super.func_78086_a(var1, var2, var3, var4);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      boolean var8 = var7 instanceof EntityLivingBase && ((EntityLivingBase)var7).func_184599_cB() > 4;
      boolean var9 = var7.func_203007_ba();
      this.field_78116_c.field_78796_g = var4 * 0.017453292F;
      if (var8) {
         this.field_78116_c.field_78795_f = -0.7853982F;
      } else if (this.field_205061_a > 0.0F) {
         if (var9) {
            this.field_78116_c.field_78795_f = this.func_205060_a(this.field_78116_c.field_78795_f, -0.7853982F, this.field_205061_a);
         } else {
            this.field_78116_c.field_78795_f = this.func_205060_a(this.field_78116_c.field_78795_f, var5 * 0.017453292F, this.field_205061_a);
         }
      } else {
         this.field_78116_c.field_78795_f = var5 * 0.017453292F;
      }

      this.field_78115_e.field_78796_g = 0.0F;
      this.field_178723_h.field_78798_e = 0.0F;
      this.field_178723_h.field_78800_c = -5.0F;
      this.field_178724_i.field_78798_e = 0.0F;
      this.field_178724_i.field_78800_c = 5.0F;
      float var10 = 1.0F;
      if (var8) {
         var10 = (float)(var7.field_70159_w * var7.field_70159_w + var7.field_70181_x * var7.field_70181_x + var7.field_70179_y * var7.field_70179_y);
         var10 /= 0.2F;
         var10 *= var10 * var10;
      }

      if (var10 < 1.0F) {
         var10 = 1.0F;
      }

      this.field_178723_h.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 2.0F * var2 * 0.5F / var10;
      this.field_178724_i.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 2.0F * var2 * 0.5F / var10;
      this.field_178723_h.field_78808_h = 0.0F;
      this.field_178724_i.field_78808_h = 0.0F;
      this.field_178721_j.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2 / var10;
      this.field_178722_k.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2 / var10;
      this.field_178721_j.field_78796_g = 0.0F;
      this.field_178722_k.field_78796_g = 0.0F;
      this.field_178721_j.field_78808_h = 0.0F;
      this.field_178722_k.field_78808_h = 0.0F;
      ModelRenderer var10000;
      if (this.field_78093_q) {
         var10000 = this.field_178723_h;
         var10000.field_78795_f += -0.62831855F;
         var10000 = this.field_178724_i;
         var10000.field_78795_f += -0.62831855F;
         this.field_178721_j.field_78795_f = -1.4137167F;
         this.field_178721_j.field_78796_g = 0.31415927F;
         this.field_178721_j.field_78808_h = 0.07853982F;
         this.field_178722_k.field_78795_f = -1.4137167F;
         this.field_178722_k.field_78796_g = -0.31415927F;
         this.field_178722_k.field_78808_h = -0.07853982F;
      }

      this.field_178723_h.field_78796_g = 0.0F;
      this.field_178723_h.field_78808_h = 0.0F;
      switch(this.field_187075_l) {
      case EMPTY:
         this.field_178724_i.field_78796_g = 0.0F;
         break;
      case BLOCK:
         this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - 0.9424779F;
         this.field_178724_i.field_78796_g = 0.5235988F;
         break;
      case ITEM:
         this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - 0.31415927F;
         this.field_178724_i.field_78796_g = 0.0F;
      }

      switch(this.field_187076_m) {
      case EMPTY:
         this.field_178723_h.field_78796_g = 0.0F;
         break;
      case BLOCK:
         this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - 0.9424779F;
         this.field_178723_h.field_78796_g = -0.5235988F;
         break;
      case ITEM:
         this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - 0.31415927F;
         this.field_178723_h.field_78796_g = 0.0F;
         break;
      case THROW_SPEAR:
         this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - 3.1415927F;
         this.field_178723_h.field_78796_g = 0.0F;
      }

      if (this.field_187075_l == ModelBiped.ArmPose.THROW_SPEAR && this.field_187076_m != ModelBiped.ArmPose.BLOCK && this.field_187076_m != ModelBiped.ArmPose.THROW_SPEAR && this.field_187076_m != ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - 3.1415927F;
         this.field_178724_i.field_78796_g = 0.0F;
      }

      float var13;
      float var14;
      if (this.field_78095_p > 0.0F) {
         EnumHandSide var11 = this.func_187072_a(var7);
         ModelRenderer var12 = this.func_187074_a(var11);
         var13 = this.field_78095_p;
         this.field_78115_e.field_78796_g = MathHelper.func_76126_a(MathHelper.func_76129_c(var13) * 6.2831855F) * 0.2F;
         if (var11 == EnumHandSide.LEFT) {
            var10000 = this.field_78115_e;
            var10000.field_78796_g *= -1.0F;
         }

         this.field_178723_h.field_78798_e = MathHelper.func_76126_a(this.field_78115_e.field_78796_g) * 5.0F;
         this.field_178723_h.field_78800_c = -MathHelper.func_76134_b(this.field_78115_e.field_78796_g) * 5.0F;
         this.field_178724_i.field_78798_e = -MathHelper.func_76126_a(this.field_78115_e.field_78796_g) * 5.0F;
         this.field_178724_i.field_78800_c = MathHelper.func_76134_b(this.field_78115_e.field_78796_g) * 5.0F;
         var10000 = this.field_178723_h;
         var10000.field_78796_g += this.field_78115_e.field_78796_g;
         var10000 = this.field_178724_i;
         var10000.field_78796_g += this.field_78115_e.field_78796_g;
         var10000 = this.field_178724_i;
         var10000.field_78795_f += this.field_78115_e.field_78796_g;
         var13 = 1.0F - this.field_78095_p;
         var13 *= var13;
         var13 *= var13;
         var13 = 1.0F - var13;
         var14 = MathHelper.func_76126_a(var13 * 3.1415927F);
         float var15 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F) * -(this.field_78116_c.field_78795_f - 0.7F) * 0.75F;
         var12.field_78795_f = (float)((double)var12.field_78795_f - ((double)var14 * 1.2D + (double)var15));
         var12.field_78796_g += this.field_78115_e.field_78796_g * 2.0F;
         var12.field_78808_h += MathHelper.func_76126_a(this.field_78095_p * 3.1415927F) * -0.4F;
      }

      if (this.field_78117_n) {
         this.field_78115_e.field_78795_f = 0.5F;
         var10000 = this.field_178723_h;
         var10000.field_78795_f += 0.4F;
         var10000 = this.field_178724_i;
         var10000.field_78795_f += 0.4F;
         this.field_178721_j.field_78798_e = 4.0F;
         this.field_178722_k.field_78798_e = 4.0F;
         this.field_178721_j.field_78797_d = 9.0F;
         this.field_178722_k.field_78797_d = 9.0F;
         this.field_78116_c.field_78797_d = 1.0F;
      } else {
         this.field_78115_e.field_78795_f = 0.0F;
         this.field_178721_j.field_78798_e = 0.1F;
         this.field_178722_k.field_78798_e = 0.1F;
         this.field_178721_j.field_78797_d = 12.0F;
         this.field_178722_k.field_78797_d = 12.0F;
         this.field_78116_c.field_78797_d = 0.0F;
      }

      var10000 = this.field_178723_h;
      var10000.field_78808_h += MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.field_178724_i;
      var10000.field_78808_h -= MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.field_178723_h;
      var10000.field_78795_f += MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
      var10000 = this.field_178724_i;
      var10000.field_78795_f -= MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
      if (this.field_187076_m == ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.field_178723_h.field_78796_g = -0.1F + this.field_78116_c.field_78796_g;
         this.field_178724_i.field_78796_g = 0.1F + this.field_78116_c.field_78796_g + 0.4F;
         this.field_178723_h.field_78795_f = -1.5707964F + this.field_78116_c.field_78795_f;
         this.field_178724_i.field_78795_f = -1.5707964F + this.field_78116_c.field_78795_f;
      } else if (this.field_187075_l == ModelBiped.ArmPose.BOW_AND_ARROW && this.field_187076_m != ModelBiped.ArmPose.THROW_SPEAR && this.field_187076_m != ModelBiped.ArmPose.BLOCK) {
         this.field_178723_h.field_78796_g = -0.1F + this.field_78116_c.field_78796_g - 0.4F;
         this.field_178724_i.field_78796_g = 0.1F + this.field_78116_c.field_78796_g;
         this.field_178723_h.field_78795_f = -1.5707964F + this.field_78116_c.field_78795_f;
         this.field_178724_i.field_78795_f = -1.5707964F + this.field_78116_c.field_78795_f;
      }

      if (this.field_205061_a > 0.0F) {
         float var16 = var1 % 26.0F;
         float var17 = this.field_78095_p > 0.0F ? 0.0F : this.field_205061_a;
         if (var16 < 14.0F) {
            this.field_178724_i.field_78795_f = this.func_205060_a(this.field_178724_i.field_78795_f, 0.0F, this.field_205061_a);
            this.field_178723_h.field_78795_f = this.func_205059_b(this.field_178723_h.field_78795_f, 0.0F, var17);
            this.field_178724_i.field_78796_g = this.func_205060_a(this.field_178724_i.field_78796_g, 3.1415927F, this.field_205061_a);
            this.field_178723_h.field_78796_g = this.func_205059_b(this.field_178723_h.field_78796_g, 3.1415927F, var17);
            this.field_178724_i.field_78808_h = this.func_205060_a(this.field_178724_i.field_78808_h, 3.1415927F + 1.8707964F * this.func_203068_a(var16) / this.func_203068_a(14.0F), this.field_205061_a);
            this.field_178723_h.field_78808_h = this.func_205059_b(this.field_178723_h.field_78808_h, 3.1415927F - 1.8707964F * this.func_203068_a(var16) / this.func_203068_a(14.0F), var17);
         } else if (var16 >= 14.0F && var16 < 22.0F) {
            var13 = (var16 - 14.0F) / 8.0F;
            this.field_178724_i.field_78795_f = this.func_205060_a(this.field_178724_i.field_78795_f, 1.5707964F * var13, this.field_205061_a);
            this.field_178723_h.field_78795_f = this.func_205059_b(this.field_178723_h.field_78795_f, 1.5707964F * var13, var17);
            this.field_178724_i.field_78796_g = this.func_205060_a(this.field_178724_i.field_78796_g, 3.1415927F, this.field_205061_a);
            this.field_178723_h.field_78796_g = this.func_205059_b(this.field_178723_h.field_78796_g, 3.1415927F, var17);
            this.field_178724_i.field_78808_h = this.func_205060_a(this.field_178724_i.field_78808_h, 5.012389F - 1.8707964F * var13, this.field_205061_a);
            this.field_178723_h.field_78808_h = this.func_205059_b(this.field_178723_h.field_78808_h, 1.2707963F + 1.8707964F * var13, var17);
         } else if (var16 >= 22.0F && var16 < 26.0F) {
            var13 = (var16 - 22.0F) / 4.0F;
            this.field_178724_i.field_78795_f = this.func_205060_a(this.field_178724_i.field_78795_f, 1.5707964F - 1.5707964F * var13, this.field_205061_a);
            this.field_178723_h.field_78795_f = this.func_205059_b(this.field_178723_h.field_78795_f, 1.5707964F - 1.5707964F * var13, var17);
            this.field_178724_i.field_78796_g = this.func_205060_a(this.field_178724_i.field_78796_g, 3.1415927F, this.field_205061_a);
            this.field_178723_h.field_78796_g = this.func_205059_b(this.field_178723_h.field_78796_g, 3.1415927F, var17);
            this.field_178724_i.field_78808_h = this.func_205060_a(this.field_178724_i.field_78808_h, 3.1415927F, this.field_205061_a);
            this.field_178723_h.field_78808_h = this.func_205059_b(this.field_178723_h.field_78808_h, 3.1415927F, var17);
         }

         var13 = 0.3F;
         var14 = 0.33333334F;
         this.field_178722_k.field_78795_f = this.func_205059_b(this.field_178722_k.field_78795_f, 0.3F * MathHelper.func_76134_b(var1 * 0.33333334F + 3.1415927F), this.field_205061_a);
         this.field_178721_j.field_78795_f = this.func_205059_b(this.field_178721_j.field_78795_f, 0.3F * MathHelper.func_76134_b(var1 * 0.33333334F), this.field_205061_a);
      }

      func_178685_a(this.field_78116_c, this.field_178720_f);
   }

   protected float func_205060_a(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -3.1415927F; var4 += 6.2831855F) {
      }

      while(var4 >= 3.1415927F) {
         var4 -= 6.2831855F;
      }

      return var1 + var3 * var4;
   }

   private float func_205059_b(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }

   private float func_203068_a(float var1) {
      return -65.0F * var1 + var1 * var1;
   }

   public void func_178686_a(ModelBase var1) {
      super.func_178686_a(var1);
      if (var1 instanceof ModelBiped) {
         ModelBiped var2 = (ModelBiped)var1;
         this.field_187075_l = var2.field_187075_l;
         this.field_187076_m = var2.field_187076_m;
         this.field_78117_n = var2.field_78117_n;
      }

   }

   public void func_178719_a(boolean var1) {
      this.field_78116_c.field_78806_j = var1;
      this.field_178720_f.field_78806_j = var1;
      this.field_78115_e.field_78806_j = var1;
      this.field_178723_h.field_78806_j = var1;
      this.field_178724_i.field_78806_j = var1;
      this.field_178721_j.field_78806_j = var1;
      this.field_178722_k.field_78806_j = var1;
   }

   public void func_187073_a(float var1, EnumHandSide var2) {
      this.func_187074_a(var2).func_78794_c(var1);
   }

   protected ModelRenderer func_187074_a(EnumHandSide var1) {
      return var1 == EnumHandSide.LEFT ? this.field_178724_i : this.field_178723_h;
   }

   protected EnumHandSide func_187072_a(Entity var1) {
      if (var1 instanceof EntityLivingBase) {
         EntityLivingBase var2 = (EntityLivingBase)var1;
         EnumHandSide var3 = var2.func_184591_cq();
         return var2.field_184622_au == EnumHand.MAIN_HAND ? var3 : var3.func_188468_a();
      } else {
         return EnumHandSide.RIGHT;
      }
   }

   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR;

      private ArmPose() {
      }
   }
}
