package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLiving<T extends EntityLiving> extends RenderLivingBase<T> {
   public RenderLiving(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
   }

   protected boolean func_177070_b(T var1) {
      return super.func_177070_b((EntityLivingBase)var1) && (var1.func_94059_bO() || var1.func_145818_k_() && var1 == this.field_76990_c.field_147941_i);
   }

   public boolean func_177071_a(T var1, ICamera var2, double var3, double var5, double var7) {
      if (super.func_177071_a(var1, var2, var3, var5, var7)) {
         return true;
      } else if (var1.func_110167_bD() && var1.func_110166_bE() != null) {
         Entity var9 = var1.func_110166_bE();
         return var2.func_78546_a(var9.func_184177_bl());
      } else {
         return false;
      }
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLivingBase)var1, var2, var4, var6, var8, var9);
      if (!this.field_188301_f) {
         this.func_110827_b(var1, var2, var4, var6, var8, var9);
      }

   }

   public void func_177105_a(T var1) {
      int var2 = var1.func_70070_b();
      int var3 = var2 % 65536;
      int var4 = var2 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var3, (float)var4);
   }

   private double func_110828_a(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   protected void func_110827_b(T var1, double var2, double var4, double var6, float var8, float var9) {
      Entity var10 = var1.func_110166_bE();
      if (var10 != null) {
         var4 -= (1.6D - (double)var1.field_70131_O) * 0.5D;
         Tessellator var11 = Tessellator.func_178181_a();
         BufferBuilder var12 = var11.func_178180_c();
         double var13 = this.func_110828_a((double)var10.field_70126_B, (double)var10.field_70177_z, (double)(var9 * 0.5F)) * 0.01745329238474369D;
         double var15 = this.func_110828_a((double)var10.field_70127_C, (double)var10.field_70125_A, (double)(var9 * 0.5F)) * 0.01745329238474369D;
         double var17 = Math.cos(var13);
         double var19 = Math.sin(var13);
         double var21 = Math.sin(var15);
         if (var10 instanceof EntityHanging) {
            var17 = 0.0D;
            var19 = 0.0D;
            var21 = -1.0D;
         }

         double var23 = Math.cos(var15);
         double var25 = this.func_110828_a(var10.field_70169_q, var10.field_70165_t, (double)var9) - var17 * 0.7D - var19 * 0.5D * var23;
         double var27 = this.func_110828_a(var10.field_70167_r + (double)var10.func_70047_e() * 0.7D, var10.field_70163_u + (double)var10.func_70047_e() * 0.7D, (double)var9) - var21 * 0.5D - 0.25D;
         double var29 = this.func_110828_a(var10.field_70166_s, var10.field_70161_v, (double)var9) - var19 * 0.7D + var17 * 0.5D * var23;
         double var31 = this.func_110828_a((double)var1.field_70760_ar, (double)var1.field_70761_aq, (double)var9) * 0.01745329238474369D + 1.5707963267948966D;
         var17 = Math.cos(var31) * (double)var1.field_70130_N * 0.4D;
         var19 = Math.sin(var31) * (double)var1.field_70130_N * 0.4D;
         double var33 = this.func_110828_a(var1.field_70169_q, var1.field_70165_t, (double)var9) + var17;
         double var35 = this.func_110828_a(var1.field_70167_r, var1.field_70163_u, (double)var9);
         double var37 = this.func_110828_a(var1.field_70166_s, var1.field_70161_v, (double)var9) + var19;
         var2 += var17;
         var6 += var19;
         double var39 = (double)((float)(var25 - var33));
         double var41 = (double)((float)(var27 - var35));
         double var43 = (double)((float)(var29 - var37));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         boolean var45 = true;
         double var46 = 0.025D;
         var12.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         int var48;
         float var49;
         float var50;
         float var51;
         float var52;
         for(var48 = 0; var48 <= 24; ++var48) {
            var49 = 0.5F;
            var50 = 0.4F;
            var51 = 0.3F;
            if (var48 % 2 == 0) {
               var49 *= 0.7F;
               var50 *= 0.7F;
               var51 *= 0.7F;
            }

            var52 = (float)var48 / 24.0F;
            var12.func_181662_b(var2 + var39 * (double)var52 + 0.0D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), var6 + var43 * (double)var52).func_181666_a(var49, var50, var51, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var39 * (double)var52 + 0.025D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, var6 + var43 * (double)var52).func_181666_a(var49, var50, var51, 1.0F).func_181675_d();
         }

         var11.func_78381_a();
         var12.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         for(var48 = 0; var48 <= 24; ++var48) {
            var49 = 0.5F;
            var50 = 0.4F;
            var51 = 0.3F;
            if (var48 % 2 == 0) {
               var49 *= 0.7F;
               var50 *= 0.7F;
               var51 *= 0.7F;
            }

            var52 = (float)var48 / 24.0F;
            var12.func_181662_b(var2 + var39 * (double)var52 + 0.0D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, var6 + var43 * (double)var52).func_181666_a(var49, var50, var51, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var39 * (double)var52 + 0.025D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), var6 + var43 * (double)var52 + 0.025D).func_181666_a(var49, var50, var51, 1.0F).func_181675_d();
         }

         var11.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         GlStateManager.func_179089_o();
      }
   }

   // $FF: synthetic method
   protected boolean func_177070_b(EntityLivingBase var1) {
      return this.func_177070_b((EntityLiving)var1);
   }
}
