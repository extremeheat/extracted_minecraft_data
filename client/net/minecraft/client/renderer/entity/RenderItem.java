package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderItem extends Render {
   private static final ResourceLocation field_110798_h = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private RenderBlocks field_147913_i = new RenderBlocks();
   private Random field_77025_h = new Random();
   public boolean field_77024_a = true;
   public float field_77023_b;
   public static boolean field_82407_g;

   public RenderItem() {
      super();
      this.field_76989_e = 0.15F;
      this.field_76987_f = 0.75F;
   }

   public void func_76986_a(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = var1.func_92059_d();
      if (var10.func_77973_b() != null) {
         this.func_110777_b(var1);
         TextureUtil.func_152777_a(false, false, 1.0F);
         this.field_77025_h.setSeed(187L);
         GL11.glPushMatrix();
         float var11 = MathHelper.func_76126_a(((float)var1.field_70292_b + var9) / 10.0F + var1.field_70290_d) * 0.1F + 0.1F;
         float var12 = (((float)var1.field_70292_b + var9) / 20.0F + var1.field_70290_d) * 57.295776F;
         byte var13 = 1;
         if (var1.func_92059_d().field_77994_a > 1) {
            var13 = 2;
         }

         if (var1.func_92059_d().field_77994_a > 5) {
            var13 = 3;
         }

         if (var1.func_92059_d().field_77994_a > 20) {
            var13 = 4;
         }

         if (var1.func_92059_d().field_77994_a > 40) {
            var13 = 5;
         }

         GL11.glTranslatef((float)var2, (float)var4 + var11, (float)var6);
         GL11.glEnable(32826);
         if (var10.func_94608_d() == 0
            && var10.func_77973_b() instanceof ItemBlock
            && RenderBlocks.func_147739_a(Block.func_149634_a(var10.func_77973_b()).func_149645_b())) {
            Block var22 = Block.func_149634_a(var10.func_77973_b());
            GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
            if (field_82407_g) {
               GL11.glScalef(1.25F, 1.25F, 1.25F);
               GL11.glTranslatef(0.0F, 0.05F, 0.0F);
               GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            float var24 = 0.25F;
            int var26 = var22.func_149645_b();
            if (var26 == 1 || var26 == 19 || var26 == 12 || var26 == 2) {
               var24 = 0.5F;
            }

            if (var22.func_149701_w() > 0) {
               GL11.glAlphaFunc(516, 0.1F);
               GL11.glEnable(3042);
               OpenGlHelper.func_148821_a(770, 771, 1, 0);
            }

            GL11.glScalef(var24, var24, var24);

            for(int var28 = 0; var28 < var13; ++var28) {
               GL11.glPushMatrix();
               if (var28 > 0) {
                  float var30 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                  float var31 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                  float var20 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                  GL11.glTranslatef(var30, var31, var20);
               }

               this.field_147913_i.func_147800_a(var22, var10.func_77960_j(), 1.0F);
               GL11.glPopMatrix();
            }

            if (var22.func_149701_w() > 0) {
               GL11.glDisable(3042);
            }
         } else if (var10.func_94608_d() == 1 && var10.func_77973_b().func_77623_v()) {
            if (field_82407_g) {
               GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
               GL11.glTranslatef(0.0F, -0.05F, 0.0F);
            } else {
               GL11.glScalef(0.5F, 0.5F, 0.5F);
            }

            for(int var21 = 0; var21 <= 1; ++var21) {
               this.field_77025_h.setSeed(187L);
               IIcon var23 = var10.func_77973_b().func_77618_c(var10.func_77960_j(), var21);
               if (this.field_77024_a) {
                  int var25 = var10.func_77973_b().func_82790_a(var10, var21);
                  float var27 = (float)(var25 >> 16 & 0xFF) / 255.0F;
                  float var29 = (float)(var25 >> 8 & 0xFF) / 255.0F;
                  float var19 = (float)(var25 & 0xFF) / 255.0F;
                  GL11.glColor4f(var27, var29, var19, 1.0F);
                  this.func_77020_a(var1, var23, var13, var9, var27, var29, var19);
               } else {
                  this.func_77020_a(var1, var23, var13, var9, 1.0F, 1.0F, 1.0F);
               }
            }
         } else {
            if (var10 != null && var10.func_77973_b() instanceof ItemCloth) {
               GL11.glAlphaFunc(516, 0.1F);
               GL11.glEnable(3042);
               OpenGlHelper.func_148821_a(770, 771, 1, 0);
            }

            if (field_82407_g) {
               GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
               GL11.glTranslatef(0.0F, -0.05F, 0.0F);
            } else {
               GL11.glScalef(0.5F, 0.5F, 0.5F);
            }

            IIcon var14 = var10.func_77954_c();
            if (this.field_77024_a) {
               int var15 = var10.func_77973_b().func_82790_a(var10, 0);
               float var16 = (float)(var15 >> 16 & 0xFF) / 255.0F;
               float var17 = (float)(var15 >> 8 & 0xFF) / 255.0F;
               float var18 = (float)(var15 & 0xFF) / 255.0F;
               this.func_77020_a(var1, var14, var13, var9, var16, var17, var18);
            } else {
               this.func_77020_a(var1, var14, var13, var9, 1.0F, 1.0F, 1.0F);
            }

            if (var10 != null && var10.func_77973_b() instanceof ItemCloth) {
               GL11.glDisable(3042);
            }
         }

         GL11.glDisable(32826);
         GL11.glPopMatrix();
         this.func_110777_b(var1);
         TextureUtil.func_147945_b();
      }
   }

   protected ResourceLocation func_110775_a(EntityItem var1) {
      return this.field_76990_c.field_78724_e.func_130087_a(var1.func_92059_d().func_94608_d());
   }

   private void func_77020_a(EntityItem var1, IIcon var2, int var3, float var4, float var5, float var6, float var7) {
      Tessellator var8 = Tessellator.field_78398_a;
      if (var2 == null) {
         TextureManager var9 = Minecraft.func_71410_x().func_110434_K();
         ResourceLocation var10 = var9.func_130087_a(var1.func_92059_d().func_94608_d());
         var2 = ((TextureMap)var9.func_110581_b(var10)).func_110572_b("missingno");
      }

      float var25 = ((IIcon)var2).func_94209_e();
      float var26 = ((IIcon)var2).func_94212_f();
      float var11 = ((IIcon)var2).func_94206_g();
      float var12 = ((IIcon)var2).func_94210_h();
      float var13 = 1.0F;
      float var14 = 0.5F;
      float var15 = 0.25F;
      if (this.field_76990_c.field_78733_k.field_74347_j) {
         GL11.glPushMatrix();
         if (field_82407_g) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GL11.glRotatef((((float)var1.field_70292_b + var4) / 20.0F + var1.field_70290_d) * 57.295776F, 0.0F, 1.0F, 0.0F);
         }

         float var16 = 0.0625F;
         float var17 = 0.021875F;
         ItemStack var18 = var1.func_92059_d();
         int var19 = var18.field_77994_a;
         byte var24;
         if (var19 < 2) {
            var24 = 1;
         } else if (var19 < 16) {
            var24 = 2;
         } else if (var19 < 32) {
            var24 = 3;
         } else {
            var24 = 4;
         }

         GL11.glTranslatef(-var14, -var15, -((var16 + var17) * (float)var24 / 2.0F));

         for(int var20 = 0; var20 < var24; ++var20) {
            GL11.glTranslatef(0.0F, 0.0F, var16 + var17);
            if (var18.func_94608_d() == 0) {
               this.func_110776_a(TextureMap.field_110575_b);
            } else {
               this.func_110776_a(TextureMap.field_110576_c);
            }

            GL11.glColor4f(var5, var6, var7, 1.0F);
            ItemRenderer.func_78439_a(var8, var26, var11, var25, var12, ((IIcon)var2).func_94211_a(), ((IIcon)var2).func_94216_b(), var16);
            if (var18.func_77962_s()) {
               GL11.glDepthFunc(514);
               GL11.glDisable(2896);
               this.field_76990_c.field_78724_e.func_110577_a(field_110798_h);
               GL11.glEnable(3042);
               GL11.glBlendFunc(768, 1);
               float var21 = 0.76F;
               GL11.glColor4f(0.5F * var21, 0.25F * var21, 0.8F * var21, 1.0F);
               GL11.glMatrixMode(5890);
               GL11.glPushMatrix();
               float var22 = 0.125F;
               GL11.glScalef(var22, var22, var22);
               float var23 = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0F * 8.0F;
               GL11.glTranslatef(var23, 0.0F, 0.0F);
               GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
               ItemRenderer.func_78439_a(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glScalef(var22, var22, var22);
               var23 = (float)(Minecraft.func_71386_F() % 4873L) / 4873.0F * 8.0F;
               GL11.glTranslatef(-var23, 0.0F, 0.0F);
               GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
               ItemRenderer.func_78439_a(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
               GL11.glPopMatrix();
               GL11.glMatrixMode(5888);
               GL11.glDisable(3042);
               GL11.glEnable(2896);
               GL11.glDepthFunc(515);
            }
         }

         GL11.glPopMatrix();
      } else {
         for(int var27 = 0; var27 < var3; ++var27) {
            GL11.glPushMatrix();
            if (var27 > 0) {
               float var28 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.3F;
               float var29 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.3F;
               float var30 = (this.field_77025_h.nextFloat() * 2.0F - 1.0F) * 0.3F;
               GL11.glTranslatef(var28, var29, var30);
            }

            if (!field_82407_g) {
               GL11.glRotatef(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
            }

            GL11.glColor4f(var5, var6, var7, 1.0F);
            var8.func_78382_b();
            var8.func_78375_b(0.0F, 1.0F, 0.0F);
            var8.func_78374_a((double)(0.0F - var14), (double)(0.0F - var15), 0.0, (double)var25, (double)var12);
            var8.func_78374_a((double)(var13 - var14), (double)(0.0F - var15), 0.0, (double)var26, (double)var12);
            var8.func_78374_a((double)(var13 - var14), (double)(1.0F - var15), 0.0, (double)var26, (double)var11);
            var8.func_78374_a((double)(0.0F - var14), (double)(1.0F - var15), 0.0, (double)var25, (double)var11);
            var8.func_78381_a();
            GL11.glPopMatrix();
         }
      }
   }

   public void func_77015_a(FontRenderer var1, TextureManager var2, ItemStack var3, int var4, int var5) {
      int var6 = var3.func_77960_j();
      Object var7 = var3.func_77954_c();
      if (var3.func_94608_d() == 0 && RenderBlocks.func_147739_a(Block.func_149634_a(var3.func_77973_b()).func_149645_b())) {
         var2.func_110577_a(TextureMap.field_110575_b);
         Block var16 = Block.func_149634_a(var3.func_77973_b());
         GL11.glEnable(3008);
         if (var16.func_149701_w() != 0) {
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glEnable(3042);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
         } else {
            GL11.glAlphaFunc(516, 0.5F);
            GL11.glDisable(3042);
         }

         GL11.glPushMatrix();
         GL11.glTranslatef((float)(var4 - 2), (float)(var5 + 3), -3.0F + this.field_77023_b);
         GL11.glScalef(10.0F, 10.0F, 10.0F);
         GL11.glTranslatef(1.0F, 0.5F, 1.0F);
         GL11.glScalef(1.0F, 1.0F, -1.0F);
         GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         int var18 = var3.func_77973_b().func_82790_a(var3, 0);
         float var20 = (float)(var18 >> 16 & 0xFF) / 255.0F;
         float var22 = (float)(var18 >> 8 & 0xFF) / 255.0F;
         float var24 = (float)(var18 & 0xFF) / 255.0F;
         if (this.field_77024_a) {
            GL11.glColor4f(var20, var22, var24, 1.0F);
         }

         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         this.field_147913_i.field_147844_c = this.field_77024_a;
         this.field_147913_i.func_147800_a(var16, var6, 1.0F);
         this.field_147913_i.field_147844_c = true;
         if (var16.func_149701_w() == 0) {
            GL11.glAlphaFunc(516, 0.1F);
         }

         GL11.glPopMatrix();
      } else if (var3.func_77973_b().func_77623_v()) {
         GL11.glDisable(2896);
         GL11.glEnable(3008);
         var2.func_110577_a(TextureMap.field_110576_c);
         GL11.glDisable(3008);
         GL11.glDisable(3553);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(0, 0, 0, 0);
         GL11.glColorMask(false, false, false, true);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Tessellator var8 = Tessellator.field_78398_a;
         var8.func_78382_b();
         var8.func_78378_d(-1);
         var8.func_78377_a((double)(var4 - 2), (double)(var5 + 18), (double)this.field_77023_b);
         var8.func_78377_a((double)(var4 + 18), (double)(var5 + 18), (double)this.field_77023_b);
         var8.func_78377_a((double)(var4 + 18), (double)(var5 - 2), (double)this.field_77023_b);
         var8.func_78377_a((double)(var4 - 2), (double)(var5 - 2), (double)this.field_77023_b);
         var8.func_78381_a();
         GL11.glColorMask(true, true, true, true);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);

         for(int var9 = 0; var9 <= 1; ++var9) {
            IIcon var10 = var3.func_77973_b().func_77618_c(var6, var9);
            int var11 = var3.func_77973_b().func_82790_a(var3, var9);
            float var12 = (float)(var11 >> 16 & 0xFF) / 255.0F;
            float var13 = (float)(var11 >> 8 & 0xFF) / 255.0F;
            float var14 = (float)(var11 & 0xFF) / 255.0F;
            if (this.field_77024_a) {
               GL11.glColor4f(var12, var13, var14, 1.0F);
            }

            this.func_94149_a(var4, var5, var10, 16, 16);
         }

         GL11.glEnable(2896);
      } else {
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         ResourceLocation var15 = var2.func_130087_a(var3.func_94608_d());
         var2.func_110577_a(var15);
         if (var7 == null) {
            var7 = ((TextureMap)Minecraft.func_71410_x().func_110434_K().func_110581_b(var15)).func_110572_b("missingno");
         }

         int var17 = var3.func_77973_b().func_82790_a(var3, 0);
         float var19 = (float)(var17 >> 16 & 0xFF) / 255.0F;
         float var21 = (float)(var17 >> 8 & 0xFF) / 255.0F;
         float var23 = (float)(var17 & 0xFF) / 255.0F;
         if (this.field_77024_a) {
            GL11.glColor4f(var19, var21, var23, 1.0F);
         }

         this.func_94149_a(var4, var5, (IIcon)var7, 16, 16);
         GL11.glEnable(2896);
         GL11.glDisable(3042);
      }

      GL11.glEnable(2884);
   }

   public void func_82406_b(FontRenderer var1, TextureManager var2, ItemStack var3, int var4, int var5) {
      if (var3 != null) {
         this.field_77023_b += 50.0F;

         try {
            this.func_77015_a(var1, var2, var3, var4, var5);
         } catch (Throwable var9) {
            CrashReport var7 = CrashReport.func_85055_a(var9, "Rendering item");
            CrashReportCategory var8 = var7.func_85058_a("Item being rendered");
            var8.func_71500_a("Item Type", new RenderItem$1(this, var3));
            var8.func_71500_a("Item Aux", new RenderItem$2(this, var3));
            var8.func_71500_a("Item NBT", new RenderItem$3(this, var3));
            var8.func_71500_a("Item Foil", new RenderItem$4(this, var3));
            throw new ReportedException(var7);
         }

         if (var3.func_77962_s()) {
            GL11.glDepthFunc(514);
            GL11.glDisable(2896);
            GL11.glDepthMask(false);
            var2.func_110577_a(field_110798_h);
            GL11.glEnable(3008);
            GL11.glEnable(3042);
            GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
            this.func_77018_a(var4 * 431278612 + var5 * 32178161, var4 - 2, var5 - 2, 20, 20);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
            GL11.glDepthMask(true);
            GL11.glEnable(2896);
            GL11.glDepthFunc(515);
         }

         this.field_77023_b -= 50.0F;
      }
   }

   private void func_77018_a(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < 2; ++var6) {
         OpenGlHelper.func_148821_a(772, 1, 0, 0);
         float var7 = 0.00390625F;
         float var8 = 0.00390625F;
         float var9 = (float)(Minecraft.func_71386_F() % (long)(3000 + var6 * 1873)) / (3000.0F + (float)(var6 * 1873)) * 256.0F;
         float var10 = 0.0F;
         Tessellator var11 = Tessellator.field_78398_a;
         float var12 = 4.0F;
         if (var6 == 1) {
            var12 = -1.0F;
         }

         var11.func_78382_b();
         var11.func_78374_a(
            (double)(var2 + 0),
            (double)(var3 + var5),
            (double)this.field_77023_b,
            (double)((var9 + (float)var5 * var12) * var7),
            (double)((var10 + (float)var5) * var8)
         );
         var11.func_78374_a(
            (double)(var2 + var4),
            (double)(var3 + var5),
            (double)this.field_77023_b,
            (double)((var9 + (float)var4 + (float)var5 * var12) * var7),
            (double)((var10 + (float)var5) * var8)
         );
         var11.func_78374_a(
            (double)(var2 + var4), (double)(var3 + 0), (double)this.field_77023_b, (double)((var9 + (float)var4) * var7), (double)((var10 + 0.0F) * var8)
         );
         var11.func_78374_a(
            (double)(var2 + 0), (double)(var3 + 0), (double)this.field_77023_b, (double)((var9 + 0.0F) * var7), (double)((var10 + 0.0F) * var8)
         );
         var11.func_78381_a();
      }
   }

   public void func_77021_b(FontRenderer var1, TextureManager var2, ItemStack var3, int var4, int var5) {
      this.func_94148_a(var1, var2, var3, var4, var5, null);
   }

   public void func_94148_a(FontRenderer var1, TextureManager var2, ItemStack var3, int var4, int var5, String var6) {
      if (var3 != null) {
         if (var3.field_77994_a > 1 || var6 != null) {
            String var7 = var6 == null ? String.valueOf(var3.field_77994_a) : var6;
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            GL11.glDisable(3042);
            var1.func_78261_a(var7, var4 + 19 - 2 - var1.func_78256_a(var7), var5 + 6 + 3, 16777215);
            GL11.glEnable(2896);
            GL11.glEnable(2929);
         }

         if (var3.func_77951_h()) {
            int var12 = (int)Math.round(13.0 - (double)var3.func_77952_i() * 13.0 / (double)var3.func_77958_k());
            int var8 = (int)Math.round(255.0 - (double)var3.func_77952_i() * 255.0 / (double)var3.func_77958_k());
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GL11.glDisable(3008);
            GL11.glDisable(3042);
            Tessellator var9 = Tessellator.field_78398_a;
            int var10 = 255 - var8 << 16 | var8 << 8;
            int var11 = (255 - var8) / 4 << 16 | 16128;
            this.func_77017_a(var9, var4 + 2, var5 + 13, 13, 2, 0);
            this.func_77017_a(var9, var4 + 2, var5 + 13, 12, 1, var11);
            this.func_77017_a(var9, var4 + 2, var5 + 13, var12, 1, var10);
            GL11.glEnable(3042);
            GL11.glEnable(3008);
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glEnable(2929);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private void func_77017_a(Tessellator var1, int var2, int var3, int var4, int var5, int var6) {
      var1.func_78382_b();
      var1.func_78378_d(var6);
      var1.func_78377_a((double)(var2 + 0), (double)(var3 + 0), 0.0);
      var1.func_78377_a((double)(var2 + 0), (double)(var3 + var5), 0.0);
      var1.func_78377_a((double)(var2 + var4), (double)(var3 + var5), 0.0);
      var1.func_78377_a((double)(var2 + var4), (double)(var3 + 0), 0.0);
      var1.func_78381_a();
   }

   public void func_94149_a(int var1, int var2, IIcon var3, int var4, int var5) {
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78382_b();
      var6.func_78374_a((double)(var1 + 0), (double)(var2 + var5), (double)this.field_77023_b, (double)var3.func_94209_e(), (double)var3.func_94210_h());
      var6.func_78374_a((double)(var1 + var4), (double)(var2 + var5), (double)this.field_77023_b, (double)var3.func_94212_f(), (double)var3.func_94210_h());
      var6.func_78374_a((double)(var1 + var4), (double)(var2 + 0), (double)this.field_77023_b, (double)var3.func_94212_f(), (double)var3.func_94206_g());
      var6.func_78374_a((double)(var1 + 0), (double)(var2 + 0), (double)this.field_77023_b, (double)var3.func_94209_e(), (double)var3.func_94206_g());
      var6.func_78381_a();
   }
}
