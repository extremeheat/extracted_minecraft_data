package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.math.MathHelper;

public class ModelWither extends ModelBase {
   private final ModelRenderer[] field_82905_a;
   private final ModelRenderer[] field_82904_b;

   public ModelWither(float var1) {
      super();
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.field_82905_a = new ModelRenderer[3];
      this.field_82905_a[0] = new ModelRenderer(this, 0, 16);
      this.field_82905_a[0].func_78790_a(-10.0F, 3.9F, -0.5F, 20, 3, 3, var1);
      this.field_82905_a[1] = (new ModelRenderer(this)).func_78787_b(this.field_78090_t, this.field_78089_u);
      this.field_82905_a[1].func_78793_a(-2.0F, 6.9F, -0.5F);
      this.field_82905_a[1].func_78784_a(0, 22).func_78790_a(0.0F, 0.0F, 0.0F, 3, 10, 3, var1);
      this.field_82905_a[1].func_78784_a(24, 22).func_78790_a(-4.0F, 1.5F, 0.5F, 11, 2, 2, var1);
      this.field_82905_a[1].func_78784_a(24, 22).func_78790_a(-4.0F, 4.0F, 0.5F, 11, 2, 2, var1);
      this.field_82905_a[1].func_78784_a(24, 22).func_78790_a(-4.0F, 6.5F, 0.5F, 11, 2, 2, var1);
      this.field_82905_a[2] = new ModelRenderer(this, 12, 22);
      this.field_82905_a[2].func_78790_a(0.0F, 0.0F, 0.0F, 3, 6, 3, var1);
      this.field_82904_b = new ModelRenderer[3];
      this.field_82904_b[0] = new ModelRenderer(this, 0, 0);
      this.field_82904_b[0].func_78790_a(-4.0F, -4.0F, -4.0F, 8, 8, 8, var1);
      this.field_82904_b[1] = new ModelRenderer(this, 32, 0);
      this.field_82904_b[1].func_78790_a(-4.0F, -4.0F, -4.0F, 6, 6, 6, var1);
      this.field_82904_b[1].field_78800_c = -8.0F;
      this.field_82904_b[1].field_78797_d = 4.0F;
      this.field_82904_b[2] = new ModelRenderer(this, 32, 0);
      this.field_82904_b[2].func_78790_a(-4.0F, -4.0F, -4.0F, 6, 6, 6, var1);
      this.field_82904_b[2].field_78800_c = 10.0F;
      this.field_82904_b[2].field_78797_d = 4.0F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      ModelRenderer[] var8 = this.field_82904_b;
      int var9 = var8.length;

      int var10;
      ModelRenderer var11;
      for(var10 = 0; var10 < var9; ++var10) {
         var11 = var8[var10];
         var11.func_78785_a(var7);
      }

      var8 = this.field_82905_a;
      var9 = var8.length;

      for(var10 = 0; var10 < var9; ++var10) {
         var11 = var8[var10];
         var11.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = MathHelper.func_76134_b(var3 * 0.1F);
      this.field_82905_a[1].field_78795_f = (0.065F + 0.05F * var8) * 3.1415927F;
      this.field_82905_a[2].func_78793_a(-2.0F, 6.9F + MathHelper.func_76134_b(this.field_82905_a[1].field_78795_f) * 10.0F, -0.5F + MathHelper.func_76126_a(this.field_82905_a[1].field_78795_f) * 10.0F);
      this.field_82905_a[2].field_78795_f = (0.265F + 0.1F * var8) * 3.1415927F;
      this.field_82904_b[0].field_78796_g = var4 * 0.017453292F;
      this.field_82904_b[0].field_78795_f = var5 * 0.017453292F;
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      EntityWither var5 = (EntityWither)var1;

      for(int var6 = 1; var6 < 3; ++var6) {
         this.field_82904_b[var6].field_78796_g = (var5.func_82207_a(var6 - 1) - var1.field_70761_aq) * 0.017453292F;
         this.field_82904_b[var6].field_78795_f = var5.func_82210_r(var6 - 1) * 0.017453292F;
      }

   }
}
