package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public abstract class RenderLiving extends RendererLivingEntity {
   public RenderLiving(ModelBase var1, float var2) {
      super(var1, var2);
   }

   protected boolean func_110813_b(EntityLiving var1) {
      return super.func_110813_b(var1) && (var1.func_94059_bO() || var1.func_94056_bM() && var1 == this.field_76990_c.field_147941_i);
   }

   public void func_76986_a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLivingBase)var1, var2, var4, var6, var8, var9);
      this.func_110827_b(var1, var2, var4, var6, var8, var9);
   }

   private double func_110828_a(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   protected void func_110827_b(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
      Entity var10 = var1.func_110166_bE();
      if (var10 != null) {
         var4 -= (1.6 - (double)var1.field_70131_O) * 0.5;
         Tessellator var11 = Tessellator.field_78398_a;
         double var12 = this.func_110828_a((double)var10.field_70126_B, (double)var10.field_70177_z, (double)(var9 * 0.5F)) * 0.01745329238474369;
         double var14 = this.func_110828_a((double)var10.field_70127_C, (double)var10.field_70125_A, (double)(var9 * 0.5F)) * 0.01745329238474369;
         double var16 = Math.cos(var12);
         double var18 = Math.sin(var12);
         double var20 = Math.sin(var14);
         if (var10 instanceof EntityHanging) {
            var16 = 0.0;
            var18 = 0.0;
            var20 = -1.0;
         }

         double var22 = Math.cos(var14);
         double var24 = this.func_110828_a(var10.field_70169_q, var10.field_70165_t, (double)var9) - var16 * 0.7 - var18 * 0.5 * var22;
         double var26 = this.func_110828_a(
               var10.field_70167_r + (double)var10.func_70047_e() * 0.7, var10.field_70163_u + (double)var10.func_70047_e() * 0.7, (double)var9
            )
            - var20 * 0.5
            - 0.25;
         double var28 = this.func_110828_a(var10.field_70166_s, var10.field_70161_v, (double)var9) - var18 * 0.7 + var16 * 0.5 * var22;
         double var30 = this.func_110828_a((double)var1.field_70760_ar, (double)var1.field_70761_aq, (double)var9) * 0.01745329238474369 + 1.5707963267948966;
         var16 = Math.cos(var30) * (double)var1.field_70130_N * 0.4;
         var18 = Math.sin(var30) * (double)var1.field_70130_N * 0.4;
         double var32 = this.func_110828_a(var1.field_70169_q, var1.field_70165_t, (double)var9) + var16;
         double var34 = this.func_110828_a(var1.field_70167_r, var1.field_70163_u, (double)var9);
         double var36 = this.func_110828_a(var1.field_70166_s, var1.field_70161_v, (double)var9) + var18;
         var2 += var16;
         var6 += var18;
         double var38 = (double)((float)(var24 - var32));
         double var40 = (double)((float)(var26 - var34));
         double var42 = (double)((float)(var28 - var36));
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glDisable(2884);
         boolean var44 = true;
         double var45 = 0.025;
         var11.func_78371_b(5);

         for(int var47 = 0; var47 <= 24; ++var47) {
            if (var47 % 2 == 0) {
               var11.func_78369_a(0.5F, 0.4F, 0.3F, 1.0F);
            } else {
               var11.func_78369_a(0.35F, 0.28F, 0.21000001F, 1.0F);
            }

            float var48 = (float)var47 / 24.0F;
            var11.func_78377_a(
               var2 + var38 * (double)var48 + 0.0,
               var4 + var40 * (double)(var48 * var48 + var48) * 0.5 + (double)((24.0F - (float)var47) / 18.0F + 0.125F),
               var6 + var42 * (double)var48
            );
            var11.func_78377_a(
               var2 + var38 * (double)var48 + 0.025,
               var4 + var40 * (double)(var48 * var48 + var48) * 0.5 + (double)((24.0F - (float)var47) / 18.0F + 0.125F) + 0.025,
               var6 + var42 * (double)var48
            );
         }

         var11.func_78381_a();
         var11.func_78371_b(5);

         for(int var54 = 0; var54 <= 24; ++var54) {
            if (var54 % 2 == 0) {
               var11.func_78369_a(0.5F, 0.4F, 0.3F, 1.0F);
            } else {
               var11.func_78369_a(0.35F, 0.28F, 0.21000001F, 1.0F);
            }

            float var55 = (float)var54 / 24.0F;
            var11.func_78377_a(
               var2 + var38 * (double)var55 + 0.0,
               var4 + var40 * (double)(var55 * var55 + var55) * 0.5 + (double)((24.0F - (float)var54) / 18.0F + 0.125F) + 0.025,
               var6 + var42 * (double)var55
            );
            var11.func_78377_a(
               var2 + var38 * (double)var55 + 0.025,
               var4 + var40 * (double)(var55 * var55 + var55) * 0.5 + (double)((24.0F - (float)var54) / 18.0F + 0.125F),
               var6 + var42 * (double)var55 + 0.025
            );
         }

         var11.func_78381_a();
         GL11.glEnable(2896);
         GL11.glEnable(3553);
         GL11.glEnable(2884);
      }
   }
}
