package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class ModelElytra extends ModelBase {
   private final ModelRenderer field_187060_a;
   private final ModelRenderer field_187061_b = new ModelRenderer(this, 22, 0);

   public ModelElytra() {
      super();
      this.field_187061_b.func_78790_a(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
      this.field_187060_a = new ModelRenderer(this, 22, 0);
      this.field_187060_a.field_78809_i = true;
      this.field_187060_a.func_78790_a(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.func_179101_C();
      GlStateManager.func_179129_p();
      if (var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).func_70631_g_()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         GlStateManager.func_179109_b(0.0F, 1.5F, -0.1F);
         this.field_187061_b.func_78785_a(var7);
         this.field_187060_a.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         this.field_187061_b.func_78785_a(var7);
         this.field_187060_a.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      float var8 = 0.2617994F;
      float var9 = -0.2617994F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      if (var7 instanceof EntityLivingBase && ((EntityLivingBase)var7).func_184613_cA()) {
         float var12 = 1.0F;
         if (var7.field_70181_x < 0.0D) {
            Vec3d var13 = (new Vec3d(var7.field_70159_w, var7.field_70181_x, var7.field_70179_y)).func_72432_b();
            var12 = 1.0F - (float)Math.pow(-var13.field_72448_b, 1.5D);
         }

         var8 = var12 * 0.34906584F + (1.0F - var12) * var8;
         var9 = var12 * -1.5707964F + (1.0F - var12) * var9;
      } else if (var7.func_70093_af()) {
         var8 = 0.6981317F;
         var9 = -0.7853982F;
         var10 = 3.0F;
         var11 = 0.08726646F;
      }

      this.field_187061_b.field_78800_c = 5.0F;
      this.field_187061_b.field_78797_d = var10;
      if (var7 instanceof AbstractClientPlayer) {
         AbstractClientPlayer var14 = (AbstractClientPlayer)var7;
         var14.field_184835_a = (float)((double)var14.field_184835_a + (double)(var8 - var14.field_184835_a) * 0.1D);
         var14.field_184836_b = (float)((double)var14.field_184836_b + (double)(var11 - var14.field_184836_b) * 0.1D);
         var14.field_184837_c = (float)((double)var14.field_184837_c + (double)(var9 - var14.field_184837_c) * 0.1D);
         this.field_187061_b.field_78795_f = var14.field_184835_a;
         this.field_187061_b.field_78796_g = var14.field_184836_b;
         this.field_187061_b.field_78808_h = var14.field_184837_c;
      } else {
         this.field_187061_b.field_78795_f = var8;
         this.field_187061_b.field_78808_h = var9;
         this.field_187061_b.field_78796_g = var11;
      }

      this.field_187060_a.field_78800_c = -this.field_187061_b.field_78800_c;
      this.field_187060_a.field_78796_g = -this.field_187061_b.field_78796_g;
      this.field_187060_a.field_78797_d = this.field_187061_b.field_78797_d;
      this.field_187060_a.field_78795_f = this.field_187061_b.field_78795_f;
      this.field_187060_a.field_78808_h = -this.field_187061_b.field_78808_h;
   }
}
