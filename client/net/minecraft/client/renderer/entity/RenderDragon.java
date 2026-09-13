package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderDragon extends RenderLiving {
   private static final ResourceLocation field_110842_f = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation field_110843_g = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
   private static final ResourceLocation field_110845_h = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private static final ResourceLocation field_110844_k = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   protected ModelDragon field_77084_b = (ModelDragon)this.field_77045_g;

   public RenderDragon() {
      super(new ModelDragon(0.0F), 0.5F);
      this.func_77042_a(this.field_77045_g);
   }

   protected void func_77043_a(EntityDragon var1, float var2, float var3, float var4) {
      float var5 = (float)var1.func_70974_a(7, var4)[0];
      float var6 = (float)(var1.func_70974_a(5, var4)[1] - var1.func_70974_a(10, var4)[1]);
      GL11.glRotatef(-var5, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glTranslatef(0.0F, 0.0F, 1.0F);
      if (var1.field_70725_aQ > 0) {
         float var7 = ((float)var1.field_70725_aQ + var4 - 1.0F) / 20.0F * 1.6F;
         var7 = MathHelper.func_76129_c(var7);
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         GL11.glRotatef(var7 * this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
      }
   }

   protected void func_77036_a(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (var1.field_70995_bG > 0) {
         float var8 = (float)var1.field_70995_bG / 200.0F;
         GL11.glDepthFunc(515);
         GL11.glEnable(3008);
         GL11.glAlphaFunc(516, var8);
         this.func_110776_a(field_110842_f);
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         GL11.glAlphaFunc(516, 0.1F);
         GL11.glDepthFunc(514);
      }

      this.func_110777_b(var1);
      this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      if (var1.field_70737_aN > 0) {
         GL11.glDepthFunc(514);
         GL11.glDisable(3553);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDepthFunc(515);
      }
   }

   public void func_76986_a(EntityDragon var1, double var2, double var4, double var6, float var8, float var9) {
      BossStatus.func_82824_a(var1, false);
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
      if (var1.field_70992_bH != null) {
         float var10 = (float)var1.field_70992_bH.field_70261_a + var9;
         float var11 = MathHelper.func_76126_a(var10 * 0.2F) / 2.0F + 0.5F;
         var11 = (var11 * var11 + var11) * 0.2F;
         float var12 = (float)(var1.field_70992_bH.field_70165_t - var1.field_70165_t - (var1.field_70169_q - var1.field_70165_t) * (double)(1.0F - var9));
         float var13 = (float)(
            (double)var11 + var1.field_70992_bH.field_70163_u - 1.0 - var1.field_70163_u - (var1.field_70167_r - var1.field_70163_u) * (double)(1.0F - var9)
         );
         float var14 = (float)(var1.field_70992_bH.field_70161_v - var1.field_70161_v - (var1.field_70166_s - var1.field_70161_v) * (double)(1.0F - var9));
         float var15 = MathHelper.func_76129_c(var12 * var12 + var14 * var14);
         float var16 = MathHelper.func_76129_c(var12 * var12 + var13 * var13 + var14 * var14);
         GL11.glPushMatrix();
         GL11.glTranslatef((float)var2, (float)var4 + 2.0F, (float)var6);
         GL11.glRotatef((float)(-Math.atan2((double)var14, (double)var12)) * 180.0F / 3.1415927F - 90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef((float)(-Math.atan2((double)var15, (double)var13)) * 180.0F / 3.1415927F - 90.0F, 1.0F, 0.0F, 0.0F);
         Tessellator var17 = Tessellator.field_78398_a;
         RenderHelper.func_74518_a();
         GL11.glDisable(2884);
         this.func_110776_a(field_110843_g);
         GL11.glShadeModel(7425);
         float var18 = 0.0F - ((float)var1.field_70173_aa + var9) * 0.01F;
         float var19 = MathHelper.func_76129_c(var12 * var12 + var13 * var13 + var14 * var14) / 32.0F - ((float)var1.field_70173_aa + var9) * 0.01F;
         var17.func_78371_b(5);
         byte var20 = 8;

         for(int var21 = 0; var21 <= var20; ++var21) {
            float var22 = MathHelper.func_76126_a((float)(var21 % var20) * 3.1415927F * 2.0F / (float)var20) * 0.75F;
            float var23 = MathHelper.func_76134_b((float)(var21 % var20) * 3.1415927F * 2.0F / (float)var20) * 0.75F;
            float var24 = (float)(var21 % var20) * 1.0F / (float)var20;
            var17.func_78378_d(0);
            var17.func_78374_a((double)(var22 * 0.2F), (double)(var23 * 0.2F), 0.0, (double)var24, (double)var19);
            var17.func_78378_d(16777215);
            var17.func_78374_a((double)var22, (double)var23, (double)var16, (double)var24, (double)var18);
         }

         var17.func_78381_a();
         GL11.glEnable(2884);
         GL11.glShadeModel(7424);
         RenderHelper.func_74519_b();
         GL11.glPopMatrix();
      }
   }

   protected ResourceLocation func_110775_a(EntityDragon var1) {
      return field_110844_k;
   }

   protected void func_77029_c(EntityDragon var1, float var2) {
      super.func_77029_c(var1, var2);
      Tessellator var3 = Tessellator.field_78398_a;
      if (var1.field_70995_bG > 0) {
         RenderHelper.func_74518_a();
         float var4 = ((float)var1.field_70995_bG + var2) / 200.0F;
         float var5 = 0.0F;
         if (var4 > 0.8F) {
            var5 = (var4 - 0.8F) / 0.2F;
         }

         Random var6 = new Random(432L);
         GL11.glDisable(3553);
         GL11.glShadeModel(7425);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 1);
         GL11.glDisable(3008);
         GL11.glEnable(2884);
         GL11.glDepthMask(false);
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -1.0F, -2.0F);

         for(int var7 = 0; (float)var7 < (var4 + var4 * var4) / 2.0F * 60.0F; ++var7) {
            GL11.glRotatef(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var6.nextFloat() * 360.0F + var4 * 90.0F, 0.0F, 0.0F, 1.0F);
            var3.func_78371_b(6);
            float var8 = var6.nextFloat() * 20.0F + 5.0F + var5 * 10.0F;
            float var9 = var6.nextFloat() * 2.0F + 1.0F + var5 * 2.0F;
            var3.func_78384_a(16777215, (int)(255.0F * (1.0F - var5)));
            var3.func_78377_a(0.0, 0.0, 0.0);
            var3.func_78384_a(16711935, 0);
            var3.func_78377_a(-0.866 * (double)var9, (double)var8, (double)(-0.5F * var9));
            var3.func_78377_a(0.866 * (double)var9, (double)var8, (double)(-0.5F * var9));
            var3.func_78377_a(0.0, (double)var8, (double)(1.0F * var9));
            var3.func_78377_a(-0.866 * (double)var9, (double)var8, (double)(-0.5F * var9));
            var3.func_78381_a();
         }

         GL11.glPopMatrix();
         GL11.glDepthMask(true);
         GL11.glDisable(2884);
         GL11.glDisable(3042);
         GL11.glShadeModel(7424);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
         RenderHelper.func_74519_b();
      }
   }

   protected int func_77032_a(EntityDragon var1, int var2, float var3) {
      if (var2 == 1) {
         GL11.glDepthFunc(515);
      }

      if (var2 != 0) {
         return -1;
      } else {
         this.func_110776_a(field_110845_h);
         GL11.glEnable(3042);
         GL11.glDisable(3008);
         GL11.glBlendFunc(1, 1);
         GL11.glDisable(2896);
         GL11.glDepthFunc(514);
         char var4 = '\uf0f0';
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GL11.glEnable(2896);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return 1;
      }
   }
}
