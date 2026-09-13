package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render {
   private static final ResourceLocation field_110778_a = new ResourceLocation("textures/misc/shadow.png");
   protected RenderManager field_76990_c;
   protected RenderBlocks field_147909_c = new RenderBlocks();
   protected float field_76989_e;
   protected float field_76987_f = 1.0F;
   private boolean field_147908_f = false;

   public Render() {
      super();
   }

   public abstract void func_76986_a(Entity var1, double var2, double var4, double var6, float var8, float var9);

   protected abstract ResourceLocation func_110775_a(Entity var1);

   public boolean func_147905_a() {
      return this.field_147908_f;
   }

   protected void func_110777_b(Entity var1) {
      this.func_110776_a(this.func_110775_a(var1));
   }

   protected void func_110776_a(ResourceLocation var1) {
      this.field_76990_c.field_78724_e.func_110577_a(var1);
   }

   private void func_76977_a(Entity var1, double var2, double var4, double var6, float var8) {
      GL11.glDisable(2896);
      IIcon var9 = Blocks.field_150480_ab.func_149840_c(0);
      IIcon var10 = Blocks.field_150480_ab.func_149840_c(1);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      float var11 = var1.field_70130_N * 1.4F;
      GL11.glScalef(var11, var11, var11);
      Tessellator var12 = Tessellator.field_78398_a;
      float var13 = 0.5F;
      float var14 = 0.0F;
      float var15 = var1.field_70131_O / var11;
      float var16 = (float)(var1.field_70163_u - var1.field_70121_D.field_72338_b);
      GL11.glRotatef(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)var15) * 0.02F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var17 = 0.0F;
      int var18 = 0;
      var12.func_78382_b();

      while(var15 > 0.0F) {
         IIcon var19 = var18 % 2 == 0 ? var9 : var10;
         this.func_110776_a(TextureMap.field_110575_b);
         float var20 = var19.func_94209_e();
         float var21 = var19.func_94206_g();
         float var22 = var19.func_94212_f();
         float var23 = var19.func_94210_h();
         if (var18 / 2 % 2 == 0) {
            float var24 = var22;
            var22 = var20;
            var20 = var24;
         }

         var12.func_78374_a((double)(var13 - var14), (double)(0.0F - var16), (double)var17, (double)var22, (double)var23);
         var12.func_78374_a((double)(-var13 - var14), (double)(0.0F - var16), (double)var17, (double)var20, (double)var23);
         var12.func_78374_a((double)(-var13 - var14), (double)(1.4F - var16), (double)var17, (double)var20, (double)var21);
         var12.func_78374_a((double)(var13 - var14), (double)(1.4F - var16), (double)var17, (double)var22, (double)var21);
         var15 -= 0.45F;
         var16 -= 0.45F;
         var13 *= 0.9F;
         var17 += 0.03F;
         ++var18;
      }

      var12.func_78381_a();
      GL11.glPopMatrix();
      GL11.glEnable(2896);
   }

   private void func_76975_c(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.field_76990_c.field_78724_e.func_110577_a(field_110778_a);
      World var10 = this.func_76982_b();
      GL11.glDepthMask(false);
      float var11 = this.field_76989_e;
      if (var1 instanceof EntityLiving) {
         EntityLiving var12 = (EntityLiving)var1;
         var11 *= var12.func_70603_bj();
         if (var12.func_70631_g_()) {
            var11 *= 0.5F;
         }
      }

      double var35 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var9;
      double var14 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var9 + (double)var1.func_70053_R();
      double var16 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var9;
      int var18 = MathHelper.func_76128_c(var35 - (double)var11);
      int var19 = MathHelper.func_76128_c(var35 + (double)var11);
      int var20 = MathHelper.func_76128_c(var14 - (double)var11);
      int var21 = MathHelper.func_76128_c(var14);
      int var22 = MathHelper.func_76128_c(var16 - (double)var11);
      int var23 = MathHelper.func_76128_c(var16 + (double)var11);
      double var24 = var2 - var35;
      double var26 = var4 - var14;
      double var28 = var6 - var16;
      Tessellator var30 = Tessellator.field_78398_a;
      var30.func_78382_b();

      for(int var31 = var18; var31 <= var19; ++var31) {
         for(int var32 = var20; var32 <= var21; ++var32) {
            for(int var33 = var22; var33 <= var23; ++var33) {
               Block var34 = var10.func_147439_a(var31, var32 - 1, var33);
               if (var34.func_149688_o() != Material.field_151579_a && var10.func_72957_l(var31, var32, var33) > 3) {
                  this.func_147907_a(
                     var34,
                     var2,
                     var4 + (double)var1.func_70053_R(),
                     var6,
                     var31,
                     var32,
                     var33,
                     var8,
                     var11,
                     var24,
                     var26 + (double)var1.func_70053_R(),
                     var28
                  );
               }
            }
         }
      }

      var30.func_78381_a();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
   }

   private World func_76982_b() {
      return this.field_76990_c.field_78722_g;
   }

   private void func_147907_a(
      Block var1, double var2, double var4, double var6, int var8, int var9, int var10, float var11, float var12, double var13, double var15, double var17
   ) {
      Tessellator var19 = Tessellator.field_78398_a;
      if (var1.func_149686_d()) {
         double var20 = ((double)var11 - (var4 - ((double)var9 + var15)) / 2.0) * 0.5 * (double)this.func_76982_b().func_72801_o(var8, var9, var10);
         if (!(var20 < 0.0)) {
            if (var20 > 1.0) {
               var20 = 1.0;
            }

            var19.func_78369_a(1.0F, 1.0F, 1.0F, (float)var20);
            double var22 = (double)var8 + var1.func_149704_x() + var13;
            double var24 = (double)var8 + var1.func_149753_y() + var13;
            double var26 = (double)var9 + var1.func_149665_z() + var15 + 0.015625;
            double var28 = (double)var10 + var1.func_149706_B() + var17;
            double var30 = (double)var10 + var1.func_149693_C() + var17;
            float var32 = (float)((var2 - var22) / 2.0 / (double)var12 + 0.5);
            float var33 = (float)((var2 - var24) / 2.0 / (double)var12 + 0.5);
            float var34 = (float)((var6 - var28) / 2.0 / (double)var12 + 0.5);
            float var35 = (float)((var6 - var30) / 2.0 / (double)var12 + 0.5);
            var19.func_78374_a(var22, var26, var28, (double)var32, (double)var34);
            var19.func_78374_a(var22, var26, var30, (double)var32, (double)var35);
            var19.func_78374_a(var24, var26, var30, (double)var33, (double)var35);
            var19.func_78374_a(var24, var26, var28, (double)var33, (double)var34);
         }
      }
   }

   public static void func_76978_a(AxisAlignedBB var0, double var1, double var3, double var5) {
      GL11.glDisable(3553);
      Tessellator var7 = Tessellator.field_78398_a;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      var7.func_78382_b();
      var7.func_78373_b(var1, var3, var5);
      var7.func_78375_b(0.0F, 0.0F, -1.0F);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var7.func_78375_b(0.0F, 0.0F, 1.0F);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var7.func_78375_b(0.0F, -1.0F, 0.0F);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var7.func_78375_b(0.0F, 1.0F, 0.0F);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var7.func_78375_b(-1.0F, 0.0F, 0.0F);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var7.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var7.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var7.func_78375_b(1.0F, 0.0F, 0.0F);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var7.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var7.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var7.func_78373_b(0.0, 0.0, 0.0);
      var7.func_78381_a();
      GL11.glEnable(3553);
   }

   public static void func_76980_a(AxisAlignedBB var0) {
      Tessellator var1 = Tessellator.field_78398_a;
      var1.func_78382_b();
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var1.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var1.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var1.func_78381_a();
   }

   public void func_76976_a(RenderManager var1) {
      this.field_76990_c = var1;
   }

   public void func_76979_b(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      if (this.field_76990_c.field_78733_k.field_74347_j && this.field_76989_e > 0.0F && !var1.func_82150_aj()) {
         double var10 = this.field_76990_c.func_78714_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
         float var12 = (float)((1.0 - var10 / 256.0) * (double)this.field_76987_f);
         if (var12 > 0.0F) {
            this.func_76975_c(var1, var2, var4, var6, var12, var9);
         }
      }

      if (var1.func_90999_ad()) {
         this.func_76977_a(var1, var2, var4, var6, var9);
      }
   }

   public FontRenderer func_76983_a() {
      return this.field_76990_c.func_78716_a();
   }

   public void func_94143_a(IIconRegister var1) {
   }

   protected void func_147906_a(Entity var1, String var2, double var3, double var5, double var7, int var9) {
      double var10 = var1.func_70068_e(this.field_76990_c.field_78734_h);
      if (!(var10 > (double)(var9 * var9))) {
         FontRenderer var12 = this.func_76983_a();
         float var13 = 1.6F;
         float var14 = 0.016666668F * var13;
         GL11.glPushMatrix();
         GL11.glTranslatef((float)var3 + 0.0F, (float)var5 + var1.field_70131_O + 0.5F, (float)var7);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         GL11.glScalef(-var14, -var14, var14);
         GL11.glDisable(2896);
         GL11.glDepthMask(false);
         GL11.glDisable(2929);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         Tessellator var15 = Tessellator.field_78398_a;
         byte var16 = 0;
         if (var2.equals("deadmau5")) {
            var16 = -10;
         }

         GL11.glDisable(3553);
         var15.func_78382_b();
         int var17 = var12.func_78256_a(var2) / 2;
         var15.func_78369_a(0.0F, 0.0F, 0.0F, 0.25F);
         var15.func_78377_a((double)(-var17 - 1), (double)(-1 + var16), 0.0);
         var15.func_78377_a((double)(-var17 - 1), (double)(8 + var16), 0.0);
         var15.func_78377_a((double)(var17 + 1), (double)(8 + var16), 0.0);
         var15.func_78377_a((double)(var17 + 1), (double)(-1 + var16), 0.0);
         var15.func_78381_a();
         GL11.glEnable(3553);
         var12.func_78276_b(var2, -var12.func_78256_a(var2) / 2, var16, 553648127);
         GL11.glEnable(2929);
         GL11.glDepthMask(true);
         var12.func_78276_b(var2, -var12.func_78256_a(var2) / 2, var16, -1);
         GL11.glEnable(2896);
         GL11.glDisable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }
   }
}
