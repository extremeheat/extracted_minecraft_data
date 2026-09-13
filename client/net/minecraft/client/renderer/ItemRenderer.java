package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
   private static final ResourceLocation field_110930_b = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final ResourceLocation field_110931_c = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation field_110929_d = new ResourceLocation("textures/misc/underwater.png");
   private Minecraft field_78455_a;
   private ItemStack field_78453_b;
   private float field_78454_c;
   private float field_78451_d;
   private RenderBlocks field_147720_h = new RenderBlocks();
   private int field_78450_g = -1;

   public ItemRenderer(Minecraft var1) {
      super();
      this.field_78455_a = var1;
   }

   public void func_78443_a(EntityLivingBase var1, ItemStack var2, int var3) {
      GL11.glPushMatrix();
      TextureManager var4 = this.field_78455_a.func_110434_K();
      Item var5 = var2.func_77973_b();
      Block var6 = Block.func_149634_a(var5);
      if (var2 != null && var6 != null && var6.func_149701_w() != 0) {
         GL11.glEnable(3042);
         GL11.glEnable(2884);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
      }

      if (var2.func_94608_d() == 0 && var5 instanceof ItemBlock && RenderBlocks.func_147739_a(var6.func_149645_b())) {
         var4.func_110577_a(var4.func_130087_a(0));
         if (var2 != null && var6 != null && var6.func_149701_w() != 0) {
            GL11.glDepthMask(false);
            this.field_147720_h.func_147800_a(var6, var2.func_77960_j(), 1.0F);
            GL11.glDepthMask(true);
         } else {
            this.field_147720_h.func_147800_a(var6, var2.func_77960_j(), 1.0F);
         }
      } else {
         IIcon var7 = var1.func_70620_b(var2, var3);
         if (var7 == null) {
            GL11.glPopMatrix();
            return;
         }

         var4.func_110577_a(var4.func_130087_a(var2.func_94608_d()));
         TextureUtil.func_152777_a(false, false, 1.0F);
         Tessellator var8 = Tessellator.field_78398_a;
         float var9 = var7.func_94209_e();
         float var10 = var7.func_94212_f();
         float var11 = var7.func_94206_g();
         float var12 = var7.func_94210_h();
         float var13 = 0.0F;
         float var14 = 0.3F;
         GL11.glEnable(32826);
         GL11.glTranslatef(-var13, -var14, 0.0F);
         float var15 = 1.5F;
         GL11.glScalef(var15, var15, var15);
         GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
         func_78439_a(var8, var10, var11, var9, var12, var7.func_94211_a(), var7.func_94216_b(), 0.0625F);
         if (var2.func_77962_s() && var3 == 0) {
            GL11.glDepthFunc(514);
            GL11.glDisable(2896);
            var4.func_110577_a(field_110930_b);
            GL11.glEnable(3042);
            OpenGlHelper.func_148821_a(768, 1, 1, 0);
            float var16 = 0.76F;
            GL11.glColor4f(0.5F * var16, 0.25F * var16, 0.8F * var16, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            float var17 = 0.125F;
            GL11.glScalef(var17, var17, var17);
            float var18 = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(var18, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            func_78439_a(var8, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(var17, var17, var17);
            var18 = (float)(Minecraft.func_71386_F() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-var18, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            func_78439_a(var8, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glDepthFunc(515);
         }

         GL11.glDisable(32826);
         var4.func_110577_a(var4.func_130087_a(var2.func_94608_d()));
         TextureUtil.func_147945_b();
      }

      if (var2 != null && var6 != null && var6.func_149701_w() != 0) {
         GL11.glDisable(3042);
      }

      GL11.glPopMatrix();
   }

   public static void func_78439_a(Tessellator var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7) {
      var0.func_78382_b();
      var0.func_78375_b(0.0F, 0.0F, 1.0F);
      var0.func_78374_a(0.0, 0.0, 0.0, (double)var1, (double)var4);
      var0.func_78374_a(1.0, 0.0, 0.0, (double)var3, (double)var4);
      var0.func_78374_a(1.0, 1.0, 0.0, (double)var3, (double)var2);
      var0.func_78374_a(0.0, 1.0, 0.0, (double)var1, (double)var2);
      var0.func_78381_a();
      var0.func_78382_b();
      var0.func_78375_b(0.0F, 0.0F, -1.0F);
      var0.func_78374_a(0.0, 1.0, (double)(0.0F - var7), (double)var1, (double)var2);
      var0.func_78374_a(1.0, 1.0, (double)(0.0F - var7), (double)var3, (double)var2);
      var0.func_78374_a(1.0, 0.0, (double)(0.0F - var7), (double)var3, (double)var4);
      var0.func_78374_a(0.0, 0.0, (double)(0.0F - var7), (double)var1, (double)var4);
      var0.func_78381_a();
      float var8 = 0.5F * (var1 - var3) / (float)var5;
      float var9 = 0.5F * (var4 - var2) / (float)var6;
      var0.func_78382_b();
      var0.func_78375_b(-1.0F, 0.0F, 0.0F);

      for(int var10 = 0; var10 < var5; ++var10) {
         float var11 = (float)var10 / (float)var5;
         float var12 = var1 + (var3 - var1) * var11 - var8;
         var0.func_78374_a((double)var11, 0.0, (double)(0.0F - var7), (double)var12, (double)var4);
         var0.func_78374_a((double)var11, 0.0, 0.0, (double)var12, (double)var4);
         var0.func_78374_a((double)var11, 1.0, 0.0, (double)var12, (double)var2);
         var0.func_78374_a((double)var11, 1.0, (double)(0.0F - var7), (double)var12, (double)var2);
      }

      var0.func_78381_a();
      var0.func_78382_b();
      var0.func_78375_b(1.0F, 0.0F, 0.0F);

      for(int var14 = 0; var14 < var5; ++var14) {
         float var17 = (float)var14 / (float)var5;
         float var20 = var1 + (var3 - var1) * var17 - var8;
         float var13 = var17 + 1.0F / (float)var5;
         var0.func_78374_a((double)var13, 1.0, (double)(0.0F - var7), (double)var20, (double)var2);
         var0.func_78374_a((double)var13, 1.0, 0.0, (double)var20, (double)var2);
         var0.func_78374_a((double)var13, 0.0, 0.0, (double)var20, (double)var4);
         var0.func_78374_a((double)var13, 0.0, (double)(0.0F - var7), (double)var20, (double)var4);
      }

      var0.func_78381_a();
      var0.func_78382_b();
      var0.func_78375_b(0.0F, 1.0F, 0.0F);

      for(int var15 = 0; var15 < var6; ++var15) {
         float var18 = (float)var15 / (float)var6;
         float var21 = var4 + (var2 - var4) * var18 - var9;
         float var23 = var18 + 1.0F / (float)var6;
         var0.func_78374_a(0.0, (double)var23, 0.0, (double)var1, (double)var21);
         var0.func_78374_a(1.0, (double)var23, 0.0, (double)var3, (double)var21);
         var0.func_78374_a(1.0, (double)var23, (double)(0.0F - var7), (double)var3, (double)var21);
         var0.func_78374_a(0.0, (double)var23, (double)(0.0F - var7), (double)var1, (double)var21);
      }

      var0.func_78381_a();
      var0.func_78382_b();
      var0.func_78375_b(0.0F, -1.0F, 0.0F);

      for(int var16 = 0; var16 < var6; ++var16) {
         float var19 = (float)var16 / (float)var6;
         float var22 = var4 + (var2 - var4) * var19 - var9;
         var0.func_78374_a(1.0, (double)var19, 0.0, (double)var3, (double)var22);
         var0.func_78374_a(0.0, (double)var19, 0.0, (double)var1, (double)var22);
         var0.func_78374_a(0.0, (double)var19, (double)(0.0F - var7), (double)var1, (double)var22);
         var0.func_78374_a(1.0, (double)var19, (double)(0.0F - var7), (double)var3, (double)var22);
      }

      var0.func_78381_a();
   }

   public void func_78440_a(float var1) {
      float var2 = this.field_78451_d + (this.field_78454_c - this.field_78451_d) * var1;
      EntityClientPlayerMP var3 = this.field_78455_a.field_71439_g;
      float var4 = var3.field_70127_C + (var3.field_70125_A - var3.field_70127_C) * var1;
      GL11.glPushMatrix();
      GL11.glRotatef(var4, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(var3.field_70126_B + (var3.field_70177_z - var3.field_70126_B) * var1, 0.0F, 1.0F, 0.0F);
      RenderHelper.func_74519_b();
      GL11.glPopMatrix();
      EntityPlayerSP var5 = var3;
      float var6 = var5.field_71164_i + (var5.field_71155_g - var5.field_71164_i) * var1;
      float var7 = var5.field_71163_h + (var5.field_71154_f - var5.field_71163_h) * var1;
      GL11.glRotatef((var3.field_70125_A - var6) * 0.1F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef((var3.field_70177_z - var7) * 0.1F, 0.0F, 1.0F, 0.0F);
      ItemStack var8 = this.field_78453_b;
      if (var8 != null && var8.func_77973_b() instanceof ItemCloth) {
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
      }

      int var9 = this.field_78455_a
         .field_71441_e
         .func_72802_i(
            MathHelper.func_76128_c(var3.field_70165_t), MathHelper.func_76128_c(var3.field_70163_u), MathHelper.func_76128_c(var3.field_70161_v), 0
         );
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var10 / 1.0F, (float)var11 / 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (var8 != null) {
         int var12 = var8.func_77973_b().func_82790_a(var8, 0);
         float var13 = (float)(var12 >> 16 & 0xFF) / 255.0F;
         float var14 = (float)(var12 >> 8 & 0xFF) / 255.0F;
         float var15 = (float)(var12 & 0xFF) / 255.0F;
         GL11.glColor4f(var13, var14, var15, 1.0F);
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (var8 != null && var8.func_77973_b() == Items.field_151098_aY) {
         GL11.glPushMatrix();
         float var24 = 0.8F;
         float var30 = var3.func_70678_g(var1);
         float var38 = MathHelper.func_76126_a(var30 * 3.1415927F);
         float var46 = MathHelper.func_76126_a(MathHelper.func_76129_c(var30) * 3.1415927F);
         GL11.glTranslatef(-var46 * 0.4F, MathHelper.func_76126_a(MathHelper.func_76129_c(var30) * 3.1415927F * 2.0F) * 0.2F, -var38 * 0.2F);
         var30 = 1.0F - var4 / 45.0F + 0.1F;
         if (var30 < 0.0F) {
            var30 = 0.0F;
         }

         if (var30 > 1.0F) {
            var30 = 1.0F;
         }

         var30 = -MathHelper.func_76134_b(var30 * 3.1415927F) * 0.5F + 0.5F;
         GL11.glTranslatef(0.0F, 0.0F * var24 - (1.0F - var2) * 1.2F - var30 * 0.5F + 0.04F, -0.9F * var24);
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(var30 * -85.0F, 0.0F, 0.0F, 1.0F);
         GL11.glEnable(32826);
         this.field_78455_a.func_110434_K().func_110577_a(var3.func_110306_p());

         for(int var39 = 0; var39 < 2; ++var39) {
            int var47 = var39 * 2 - 1;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)var47);
            GL11.glRotatef((float)(-45 * var47), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef((float)(-65 * var47), 0.0F, 1.0F, 0.0F);
            Render var54 = RenderManager.field_78727_a.func_78713_a(this.field_78455_a.field_71439_g);
            RenderPlayer var59 = (RenderPlayer)var54;
            float var63 = 1.0F;
            GL11.glScalef(var63, var63, var63);
            var59.func_82441_a(this.field_78455_a.field_71439_g);
            GL11.glPopMatrix();
         }

         var38 = var3.func_70678_g(var1);
         var46 = MathHelper.func_76126_a(var38 * var38 * 3.1415927F);
         float var55 = MathHelper.func_76126_a(MathHelper.func_76129_c(var38) * 3.1415927F);
         GL11.glRotatef(-var46 * 20.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var55 * 20.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(-var55 * 80.0F, 1.0F, 0.0F, 0.0F);
         float var60 = 0.38F;
         GL11.glScalef(var60, var60, var60);
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
         float var64 = 0.015625F;
         GL11.glScalef(var64, var64, var64);
         this.field_78455_a.func_110434_K().func_110577_a(field_110931_c);
         Tessellator var67 = Tessellator.field_78398_a;
         GL11.glNormal3f(0.0F, 0.0F, -1.0F);
         var67.func_78382_b();
         byte var69 = 7;
         var67.func_78374_a((double)(0 - var69), (double)(128 + var69), 0.0, 0.0, 1.0);
         var67.func_78374_a((double)(128 + var69), (double)(128 + var69), 0.0, 1.0, 1.0);
         var67.func_78374_a((double)(128 + var69), (double)(0 - var69), 0.0, 1.0, 0.0);
         var67.func_78374_a((double)(0 - var69), (double)(0 - var69), 0.0, 0.0, 0.0);
         var67.func_78381_a();
         MapData var21 = Items.field_151098_aY.func_77873_a(var8, this.field_78455_a.field_71441_e);
         if (var21 != null) {
            this.field_78455_a.field_71460_t.func_147701_i().func_148250_a(var21, false);
         }

         GL11.glPopMatrix();
      } else if (var8 != null) {
         GL11.glPushMatrix();
         float var22 = 0.8F;
         if (var3.func_71052_bv() > 0) {
            EnumAction var26 = var8.func_77975_n();
            if (var26 == EnumAction.eat || var26 == EnumAction.drink) {
               float var34 = (float)var3.func_71052_bv() - var1 + 1.0F;
               float var42 = 1.0F - var34 / (float)var8.func_77988_m();
               float var16 = 1.0F - var42;
               var16 = var16 * var16 * var16;
               var16 = var16 * var16 * var16;
               var16 = var16 * var16 * var16;
               float var17 = 1.0F - var16;
               GL11.glTranslatef(
                  0.0F, MathHelper.func_76135_e(MathHelper.func_76134_b(var34 / 4.0F * 3.1415927F) * 0.1F) * (float)((double)var42 > 0.2 ? 1 : 0), 0.0F
               );
               GL11.glTranslatef(var17 * 0.6F, -var17 * 0.5F, 0.0F);
               GL11.glRotatef(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
               GL11.glRotatef(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
            }
         } else {
            float var25 = var3.func_70678_g(var1);
            float var33 = MathHelper.func_76126_a(var25 * 3.1415927F);
            float var41 = MathHelper.func_76126_a(MathHelper.func_76129_c(var25) * 3.1415927F);
            GL11.glTranslatef(-var41 * 0.4F, MathHelper.func_76126_a(MathHelper.func_76129_c(var25) * 3.1415927F * 2.0F) * 0.2F, -var33 * 0.2F);
         }

         GL11.glTranslatef(0.7F * var22, -0.65F * var22 - (1.0F - var2) * 0.6F, -0.9F * var22);
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glEnable(32826);
         float var27 = var3.func_70678_g(var1);
         float var35 = MathHelper.func_76126_a(var27 * var27 * 3.1415927F);
         float var43 = MathHelper.func_76126_a(MathHelper.func_76129_c(var27) * 3.1415927F);
         GL11.glRotatef(-var35 * 20.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var43 * 20.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(-var43 * 80.0F, 1.0F, 0.0F, 0.0F);
         float var52 = 0.4F;
         GL11.glScalef(var52, var52, var52);
         if (var3.func_71052_bv() > 0) {
            EnumAction var56 = var8.func_77975_n();
            if (var56 == EnumAction.block) {
               GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
               GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
               GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
            } else if (var56 == EnumAction.bow) {
               GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
               GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
               GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
               float var18 = (float)var8.func_77988_m() - ((float)var3.func_71052_bv() - var1 + 1.0F);
               float var19 = var18 / 20.0F;
               var19 = (var19 * var19 + var19 * 2.0F) / 3.0F;
               if (var19 > 1.0F) {
                  var19 = 1.0F;
               }

               if (var19 > 0.1F) {
                  GL11.glTranslatef(0.0F, MathHelper.func_76126_a((var18 - 0.1F) * 1.3F) * 0.01F * (var19 - 0.1F), 0.0F);
               }

               GL11.glTranslatef(0.0F, 0.0F, var19 * 0.1F);
               GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
               GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
               GL11.glTranslatef(0.0F, 0.5F, 0.0F);
               float var20 = 1.0F + var19 * 0.2F;
               GL11.glScalef(1.0F, 1.0F, var20);
               GL11.glTranslatef(0.0F, -0.5F, 0.0F);
               GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            }
         }

         if (var8.func_77973_b().func_77629_n_()) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var8.func_77973_b().func_77623_v()) {
            this.func_78443_a(var3, var8, 0);
            int var57 = var8.func_77973_b().func_82790_a(var8, 1);
            float var61 = (float)(var57 >> 16 & 0xFF) / 255.0F;
            float var66 = (float)(var57 >> 8 & 0xFF) / 255.0F;
            float var68 = (float)(var57 & 0xFF) / 255.0F;
            GL11.glColor4f(1.0F * var61, 1.0F * var66, 1.0F * var68, 1.0F);
            this.func_78443_a(var3, var8, 1);
         } else {
            this.func_78443_a(var3, var8, 0);
         }

         GL11.glPopMatrix();
      } else if (!var3.func_82150_aj()) {
         GL11.glPushMatrix();
         float var23 = 0.8F;
         float var28 = var3.func_70678_g(var1);
         float var36 = MathHelper.func_76126_a(var28 * 3.1415927F);
         float var44 = MathHelper.func_76126_a(MathHelper.func_76129_c(var28) * 3.1415927F);
         GL11.glTranslatef(-var44 * 0.3F, MathHelper.func_76126_a(MathHelper.func_76129_c(var28) * 3.1415927F * 2.0F) * 0.4F, -var36 * 0.4F);
         GL11.glTranslatef(0.8F * var23, -0.75F * var23 - (1.0F - var2) * 0.6F, -0.9F * var23);
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glEnable(32826);
         var28 = var3.func_70678_g(var1);
         var36 = MathHelper.func_76126_a(var28 * var28 * 3.1415927F);
         var44 = MathHelper.func_76126_a(MathHelper.func_76129_c(var28) * 3.1415927F);
         GL11.glRotatef(var44 * 70.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var36 * 20.0F, 0.0F, 0.0F, 1.0F);
         this.field_78455_a.func_110434_K().func_110577_a(var3.func_110306_p());
         GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
         GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
         GL11.glScalef(1.0F, 1.0F, 1.0F);
         GL11.glTranslatef(5.6F, 0.0F, 0.0F);
         Render var53 = RenderManager.field_78727_a.func_78713_a(this.field_78455_a.field_71439_g);
         RenderPlayer var58 = (RenderPlayer)var53;
         float var62 = 1.0F;
         GL11.glScalef(var62, var62, var62);
         var58.func_82441_a(this.field_78455_a.field_71439_g);
         GL11.glPopMatrix();
      }

      if (var8 != null && var8.func_77973_b() instanceof ItemCloth) {
         GL11.glDisable(3042);
      }

      GL11.glDisable(32826);
      RenderHelper.func_74518_a();
   }

   public void func_78447_b(float var1) {
      GL11.glDisable(3008);
      if (this.field_78455_a.field_71439_g.func_70027_ad()) {
         this.func_78442_d(var1);
      }

      if (this.field_78455_a.field_71439_g.func_70094_T()) {
         int var2 = MathHelper.func_76128_c(this.field_78455_a.field_71439_g.field_70165_t);
         int var3 = MathHelper.func_76128_c(this.field_78455_a.field_71439_g.field_70163_u);
         int var4 = MathHelper.func_76128_c(this.field_78455_a.field_71439_g.field_70161_v);
         Block var5 = this.field_78455_a.field_71441_e.func_147439_a(var2, var3, var4);
         if (this.field_78455_a.field_71441_e.func_147439_a(var2, var3, var4).func_149721_r()) {
            this.func_78446_a(var1, var5.func_149733_h(2));
         } else {
            for(int var6 = 0; var6 < 8; ++var6) {
               float var7 = ((float)((var6 >> 0) % 2) - 0.5F) * this.field_78455_a.field_71439_g.field_70130_N * 0.9F;
               float var8 = ((float)((var6 >> 1) % 2) - 0.5F) * this.field_78455_a.field_71439_g.field_70131_O * 0.2F;
               float var9 = ((float)((var6 >> 2) % 2) - 0.5F) * this.field_78455_a.field_71439_g.field_70130_N * 0.9F;
               int var10 = MathHelper.func_76141_d((float)var2 + var7);
               int var11 = MathHelper.func_76141_d((float)var3 + var8);
               int var12 = MathHelper.func_76141_d((float)var4 + var9);
               if (this.field_78455_a.field_71441_e.func_147439_a(var10, var11, var12).func_149721_r()) {
                  var5 = this.field_78455_a.field_71441_e.func_147439_a(var10, var11, var12);
               }
            }
         }

         if (var5.func_149688_o() != Material.field_151579_a) {
            this.func_78446_a(var1, var5.func_149733_h(2));
         }
      }

      if (this.field_78455_a.field_71439_g.func_70055_a(Material.field_151586_h)) {
         this.func_78448_c(var1);
      }

      GL11.glEnable(3008);
   }

   private void func_78446_a(float var1, IIcon var2) {
      this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      Tessellator var3 = Tessellator.field_78398_a;
      float var4 = 0.1F;
      GL11.glColor4f(var4, var4, var4, 0.5F);
      GL11.glPushMatrix();
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = var2.func_94209_e();
      float var11 = var2.func_94212_f();
      float var12 = var2.func_94206_g();
      float var13 = var2.func_94210_h();
      var3.func_78382_b();
      var3.func_78374_a((double)var5, (double)var7, (double)var9, (double)var11, (double)var13);
      var3.func_78374_a((double)var6, (double)var7, (double)var9, (double)var10, (double)var13);
      var3.func_78374_a((double)var6, (double)var8, (double)var9, (double)var10, (double)var12);
      var3.func_78374_a((double)var5, (double)var8, (double)var9, (double)var11, (double)var12);
      var3.func_78381_a();
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_78448_c(float var1) {
      this.field_78455_a.func_110434_K().func_110577_a(field_110929_d);
      Tessellator var2 = Tessellator.field_78398_a;
      float var3 = this.field_78455_a.field_71439_g.func_70013_c(var1);
      GL11.glColor4f(var3, var3, var3, 0.5F);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glPushMatrix();
      float var4 = 4.0F;
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = -this.field_78455_a.field_71439_g.field_70177_z / 64.0F;
      float var11 = this.field_78455_a.field_71439_g.field_70125_A / 64.0F;
      var2.func_78382_b();
      var2.func_78374_a((double)var5, (double)var7, (double)var9, (double)(var4 + var10), (double)(var4 + var11));
      var2.func_78374_a((double)var6, (double)var7, (double)var9, (double)(0.0F + var10), (double)(var4 + var11));
      var2.func_78374_a((double)var6, (double)var8, (double)var9, (double)(0.0F + var10), (double)(0.0F + var11));
      var2.func_78374_a((double)var5, (double)var8, (double)var9, (double)(var4 + var10), (double)(0.0F + var11));
      var2.func_78381_a();
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
   }

   private void func_78442_d(float var1) {
      Tessellator var2 = Tessellator.field_78398_a;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      float var3 = 1.0F;

      for(int var4 = 0; var4 < 2; ++var4) {
         GL11.glPushMatrix();
         IIcon var5 = Blocks.field_150480_ab.func_149840_c(1);
         this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
         float var6 = var5.func_94209_e();
         float var7 = var5.func_94212_f();
         float var8 = var5.func_94206_g();
         float var9 = var5.func_94210_h();
         float var10 = (0.0F - var3) / 2.0F;
         float var11 = var10 + var3;
         float var12 = 0.0F - var3 / 2.0F;
         float var13 = var12 + var3;
         float var14 = -0.5F;
         GL11.glTranslatef((float)(-(var4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GL11.glRotatef((float)(var4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         var2.func_78382_b();
         var2.func_78374_a((double)var10, (double)var12, (double)var14, (double)var7, (double)var9);
         var2.func_78374_a((double)var11, (double)var12, (double)var14, (double)var6, (double)var9);
         var2.func_78374_a((double)var11, (double)var13, (double)var14, (double)var6, (double)var8);
         var2.func_78374_a((double)var10, (double)var13, (double)var14, (double)var7, (double)var8);
         var2.func_78381_a();
         GL11.glPopMatrix();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
   }

   public void func_78441_a() {
      this.field_78451_d = this.field_78454_c;
      EntityClientPlayerMP var1 = this.field_78455_a.field_71439_g;
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      boolean var3 = this.field_78450_g == var1.field_71071_by.field_70461_c && var2 == this.field_78453_b;
      if (this.field_78453_b == null && var2 == null) {
         var3 = true;
      }

      if (var2 != null
         && this.field_78453_b != null
         && var2 != this.field_78453_b
         && var2.func_77973_b() == this.field_78453_b.func_77973_b()
         && var2.func_77960_j() == this.field_78453_b.func_77960_j()) {
         this.field_78453_b = var2;
         var3 = true;
      }

      float var4 = 0.4F;
      float var5 = var3 ? 1.0F : 0.0F;
      float var6 = var5 - this.field_78454_c;
      if (var6 < -var4) {
         var6 = -var4;
      }

      if (var6 > var4) {
         var6 = var4;
      }

      this.field_78454_c += var6;
      if (this.field_78454_c < 0.1F) {
         this.field_78453_b = var2;
         this.field_78450_g = var1.field_71071_by.field_70461_c;
      }
   }

   public void func_78444_b() {
      this.field_78454_c = 0.0F;
   }

   public void func_78445_c() {
      this.field_78454_c = 0.0F;
   }
}
