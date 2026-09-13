package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity extends Render {
   private static final Logger field_147923_a = LogManager.getLogger();
   private static final ResourceLocation field_110814_a = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   protected ModelBase field_77045_g;
   protected ModelBase field_77046_h;

   public RendererLivingEntity(ModelBase var1, float var2) {
      super();
      this.field_77045_g = var1;
      this.field_76989_e = var2;
   }

   public void func_77042_a(ModelBase var1) {
      this.field_77046_h = var1;
   }

   private float func_77034_a(float var1, float var2, float var3) {
      float var4 = var2 - var1;

      while(var4 < -180.0F) {
         var4 += 360.0F;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void func_76986_a(EntityLivingBase var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glDisable(2884);
      this.field_77045_g.field_78095_p = this.func_77040_d(var1, var9);
      if (this.field_77046_h != null) {
         this.field_77046_h.field_78095_p = this.field_77045_g.field_78095_p;
      }

      this.field_77045_g.field_78093_q = var1.func_70115_ae();
      if (this.field_77046_h != null) {
         this.field_77046_h.field_78093_q = this.field_77045_g.field_78093_q;
      }

      this.field_77045_g.field_78091_s = var1.func_70631_g_();
      if (this.field_77046_h != null) {
         this.field_77046_h.field_78091_s = this.field_77045_g.field_78091_s;
      }

      try {
         float var10 = this.func_77034_a(var1.field_70760_ar, var1.field_70761_aq, var9);
         float var11 = this.func_77034_a(var1.field_70758_at, var1.field_70759_as, var9);
         if (var1.func_70115_ae() && var1.field_70154_o instanceof EntityLivingBase) {
            EntityLivingBase var12 = (EntityLivingBase)var1.field_70154_o;
            var10 = this.func_77034_a(var12.field_70760_ar, var12.field_70761_aq, var9);
            float var13 = MathHelper.func_76142_g(var11 - var10);
            if (var13 < -85.0F) {
               var13 = -85.0F;
            }

            if (var13 >= 85.0F) {
               var13 = 85.0F;
            }

            var10 = var11 - var13;
            if (var13 * var13 > 2500.0F) {
               var10 += var13 * 0.2F;
            }
         }

         float var27 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
         this.func_77039_a(var1, var2, var4, var6);
         float var28 = this.func_77044_a(var1, var9);
         this.func_77043_a(var1, var28, var10, var9);
         float var14 = 0.0625F;
         GL11.glEnable(32826);
         GL11.glScalef(-1.0F, -1.0F, 1.0F);
         this.func_77041_b(var1, var9);
         GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
         float var15 = var1.field_70722_aY + (var1.field_70721_aZ - var1.field_70722_aY) * var9;
         float var16 = var1.field_70754_ba - var1.field_70721_aZ * (1.0F - var9);
         if (var1.func_70631_g_()) {
            var16 *= 3.0F;
         }

         if (var15 > 1.0F) {
            var15 = 1.0F;
         }

         GL11.glEnable(3008);
         this.field_77045_g.func_78086_a(var1, var16, var15, var9);
         this.func_77036_a(var1, var16, var15, var28, var11 - var10, var27, var14);

         for(int var17 = 0; var17 < 4; ++var17) {
            int var18 = this.func_77032_a(var1, var17, var9);
            if (var18 > 0) {
               this.field_77046_h.func_78086_a(var1, var16, var15, var9);
               this.field_77046_h.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);
               if ((var18 & 240) == 16) {
                  this.func_82408_c(var1, var17, var9);
                  this.field_77046_h.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);
               }

               if ((var18 & 15) == 15) {
                  float var19 = (float)var1.field_70173_aa + var9;
                  this.func_110776_a(field_110814_a);
                  GL11.glEnable(3042);
                  float var20 = 0.5F;
                  GL11.glColor4f(var20, var20, var20, 1.0F);
                  GL11.glDepthFunc(514);
                  GL11.glDepthMask(false);

                  for(int var21 = 0; var21 < 2; ++var21) {
                     GL11.glDisable(2896);
                     float var22 = 0.76F;
                     GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
                     GL11.glBlendFunc(768, 1);
                     GL11.glMatrixMode(5890);
                     GL11.glLoadIdentity();
                     float var23 = var19 * (0.001F + (float)var21 * 0.003F) * 20.0F;
                     float var24 = 0.33333334F;
                     GL11.glScalef(var24, var24, var24);
                     GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
                     GL11.glTranslatef(0.0F, var23, 0.0F);
                     GL11.glMatrixMode(5888);
                     this.field_77046_h.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);
                  }

                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glMatrixMode(5890);
                  GL11.glDepthMask(true);
                  GL11.glLoadIdentity();
                  GL11.glMatrixMode(5888);
                  GL11.glEnable(2896);
                  GL11.glDisable(3042);
                  GL11.glDepthFunc(515);
               }

               GL11.glDisable(3042);
               GL11.glEnable(3008);
            }
         }

         GL11.glDepthMask(true);
         this.func_77029_c(var1, var9);
         float var29 = var1.func_70013_c(var9);
         int var30 = this.func_77030_a(var1, var29, var9);
         OpenGlHelper.func_77473_a(OpenGlHelper.field_77476_b);
         GL11.glDisable(3553);
         OpenGlHelper.func_77473_a(OpenGlHelper.field_77478_a);
         if ((var30 >> 24 & 0xFF) > 0 || var1.field_70737_aN > 0 || var1.field_70725_aQ > 0) {
            GL11.glDisable(3553);
            GL11.glDisable(3008);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthFunc(514);
            if (var1.field_70737_aN > 0 || var1.field_70725_aQ > 0) {
               GL11.glColor4f(var29, 0.0F, 0.0F, 0.4F);
               this.field_77045_g.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);

               for(int var31 = 0; var31 < 4; ++var31) {
                  if (this.func_77035_b(var1, var31, var9) >= 0) {
                     GL11.glColor4f(var29, 0.0F, 0.0F, 0.4F);
                     this.field_77046_h.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);
                  }
               }
            }

            if ((var30 >> 24 & 0xFF) > 0) {
               float var32 = (float)(var30 >> 16 & 0xFF) / 255.0F;
               float var33 = (float)(var30 >> 8 & 0xFF) / 255.0F;
               float var34 = (float)(var30 & 0xFF) / 255.0F;
               float var35 = (float)(var30 >> 24 & 0xFF) / 255.0F;
               GL11.glColor4f(var32, var33, var34, var35);
               this.field_77045_g.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);

               for(int var36 = 0; var36 < 4; ++var36) {
                  if (this.func_77035_b(var1, var36, var9) >= 0) {
                     GL11.glColor4f(var32, var33, var34, var35);
                     this.field_77046_h.func_78088_a(var1, var16, var15, var28, var11 - var10, var27, var14);
                  }
               }
            }

            GL11.glDepthFunc(515);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glEnable(3553);
         }

         GL11.glDisable(32826);
      } catch (Exception var25) {
         field_147923_a.error("Couldn't render entity", var25);
      }

      OpenGlHelper.func_77473_a(OpenGlHelper.field_77476_b);
      GL11.glEnable(3553);
      OpenGlHelper.func_77473_a(OpenGlHelper.field_77478_a);
      GL11.glEnable(2884);
      GL11.glPopMatrix();
      this.func_77033_b(var1, var2, var4, var6);
   }

   protected void func_77036_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_110777_b(var1);
      if (!var1.func_82150_aj()) {
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (!var1.func_98034_c(Minecraft.func_71410_x().field_71439_g)) {
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
         GL11.glDepthMask(false);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glAlphaFunc(516, 0.003921569F);
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         GL11.glDisable(3042);
         GL11.glAlphaFunc(516, 0.1F);
         GL11.glPopMatrix();
         GL11.glDepthMask(true);
      } else {
         this.field_77045_g.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      }
   }

   protected void func_77039_a(EntityLivingBase var1, double var2, double var4, double var6) {
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
   }

   protected void func_77043_a(EntityLivingBase var1, float var2, float var3, float var4) {
      GL11.glRotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      if (var1.field_70725_aQ > 0) {
         float var5 = ((float)var1.field_70725_aQ + var4 - 1.0F) / 20.0F * 1.6F;
         var5 = MathHelper.func_76129_c(var5);
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         GL11.glRotatef(var5 * this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
      } else {
         String var7 = EnumChatFormatting.func_110646_a(var1.func_70005_c_());
         if ((var7.equals("Dinnerbone") || var7.equals("Grumm")) && (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_82238_cc())) {
            GL11.glTranslatef(0.0F, var1.field_70131_O + 0.1F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   protected float func_77040_d(EntityLivingBase var1, float var2) {
      return var1.func_70678_g(var2);
   }

   protected float func_77044_a(EntityLivingBase var1, float var2) {
      return (float)var1.field_70173_aa + var2;
   }

   protected void func_77029_c(EntityLivingBase var1, float var2) {
   }

   protected void func_85093_e(EntityLivingBase var1, float var2) {
      int var3 = var1.func_85035_bI();
      if (var3 > 0) {
         EntityArrow var4 = new EntityArrow(var1.field_70170_p, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
         Random var5 = new Random((long)var1.func_145782_y());
         RenderHelper.func_74518_a();

         for(int var6 = 0; var6 < var3; ++var6) {
            GL11.glPushMatrix();
            ModelRenderer var7 = this.field_77045_g.func_85181_a(var5);
            ModelBox var8 = (ModelBox)var7.field_78804_l.get(var5.nextInt(var7.field_78804_l.size()));
            var7.func_78794_c(0.0625F);
            float var9 = var5.nextFloat();
            float var10 = var5.nextFloat();
            float var11 = var5.nextFloat();
            float var12 = (var8.field_78252_a + (var8.field_78248_d - var8.field_78252_a) * var9) / 16.0F;
            float var13 = (var8.field_78250_b + (var8.field_78249_e - var8.field_78250_b) * var10) / 16.0F;
            float var14 = (var8.field_78251_c + (var8.field_78246_f - var8.field_78251_c) * var11) / 16.0F;
            GL11.glTranslatef(var12, var13, var14);
            var9 = var9 * 2.0F - 1.0F;
            var10 = var10 * 2.0F - 1.0F;
            var11 = var11 * 2.0F - 1.0F;
            var9 *= -1.0F;
            var10 *= -1.0F;
            var11 *= -1.0F;
            float var15 = MathHelper.func_76129_c(var9 * var9 + var11 * var11);
            var4.field_70126_B = var4.field_70177_z = (float)(Math.atan2((double)var9, (double)var11) * 180.0 / 3.1415927410125732);
            var4.field_70127_C = var4.field_70125_A = (float)(Math.atan2((double)var10, (double)var15) * 180.0 / 3.1415927410125732);
            double var16 = 0.0;
            double var18 = 0.0;
            double var20 = 0.0;
            float var22 = 0.0F;
            this.field_76990_c.func_147940_a(var4, var16, var18, var20, var22, var2);
            GL11.glPopMatrix();
         }

         RenderHelper.func_74519_b();
      }
   }

   protected int func_77035_b(EntityLivingBase var1, int var2, float var3) {
      return this.func_77032_a(var1, var2, var3);
   }

   protected int func_77032_a(EntityLivingBase var1, int var2, float var3) {
      return -1;
   }

   protected void func_82408_c(EntityLivingBase var1, int var2, float var3) {
   }

   protected float func_77037_a(EntityLivingBase var1) {
      return 90.0F;
   }

   protected int func_77030_a(EntityLivingBase var1, float var2, float var3) {
      return 0;
   }

   protected void func_77041_b(EntityLivingBase var1, float var2) {
   }

   protected void func_77033_b(EntityLivingBase var1, double var2, double var4, double var6) {
      GL11.glAlphaFunc(516, 0.1F);
      if (this.func_110813_b(var1)) {
         float var8 = 1.6F;
         float var9 = 0.016666668F * var8;
         double var10 = var1.func_70068_e(this.field_76990_c.field_78734_h);
         float var12 = var1.func_70093_af() ? 32.0F : 64.0F;
         if (var10 < (double)(var12 * var12)) {
            String var13 = var1.func_145748_c_().func_150254_d();
            if (var1.func_70093_af()) {
               FontRenderer var14 = this.func_76983_a();
               GL11.glPushMatrix();
               GL11.glTranslatef((float)var2 + 0.0F, (float)var4 + var1.field_70131_O + 0.5F, (float)var6);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GL11.glRotatef(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
               GL11.glScalef(-var9, -var9, var9);
               GL11.glDisable(2896);
               GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
               GL11.glDepthMask(false);
               GL11.glEnable(3042);
               OpenGlHelper.func_148821_a(770, 771, 1, 0);
               Tessellator var15 = Tessellator.field_78398_a;
               GL11.glDisable(3553);
               var15.func_78382_b();
               int var16 = var14.func_78256_a(var13) / 2;
               var15.func_78369_a(0.0F, 0.0F, 0.0F, 0.25F);
               var15.func_78377_a((double)(-var16 - 1), -1.0, 0.0);
               var15.func_78377_a((double)(-var16 - 1), 8.0, 0.0);
               var15.func_78377_a((double)(var16 + 1), 8.0, 0.0);
               var15.func_78377_a((double)(var16 + 1), -1.0, 0.0);
               var15.func_78381_a();
               GL11.glEnable(3553);
               GL11.glDepthMask(true);
               var14.func_78276_b(var13, -var14.func_78256_a(var13) / 2, 0, 553648127);
               GL11.glEnable(2896);
               GL11.glDisable(3042);
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glPopMatrix();
            } else {
               this.func_96449_a(var1, var2, var4, var6, var13, var9, var10);
            }
         }
      }
   }

   protected boolean func_110813_b(EntityLivingBase var1) {
      return Minecraft.func_71382_s()
         && var1 != this.field_76990_c.field_78734_h
         && !var1.func_98034_c(Minecraft.func_71410_x().field_71439_g)
         && var1.field_70153_n == null;
   }

   protected void func_96449_a(EntityLivingBase var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      if (var1.func_70608_bn()) {
         this.func_147906_a(var1, var8, var2, var4 - 1.5, var6, 64);
      } else {
         this.func_147906_a(var1, var8, var2, var4, var6, 64);
      }
   }
}
