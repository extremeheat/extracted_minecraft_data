package net.minecraft.client.renderer.entity;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity> {
   private static final ResourceLocation field_110778_a = new ResourceLocation("textures/misc/shadow.png");
   protected final RenderManager field_76990_c;
   protected float field_76989_e;
   protected float field_76987_f = 1.0F;

   protected Render(RenderManager var1) {
      super();
      this.field_76990_c = var1;
   }

   public boolean func_177071_a(T var1, ICamera var2, double var3, double var5, double var7) {
      AxisAlignedBB var9 = var1.func_174813_aQ();
      if (var9.func_181656_b() || var9.func_72320_b() == 0.0D) {
         var9 = new AxisAlignedBB(var1.field_70165_t - 2.0D, var1.field_70163_u - 2.0D, var1.field_70161_v - 2.0D, var1.field_70165_t + 2.0D, var1.field_70163_u + 2.0D, var1.field_70161_v + 2.0D);
      }

      return var1.func_145770_h(var3, var5, var7) && (var1.field_70158_ak || var2.func_78546_a(var9));
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_177067_a(var1, var2, var4, var6);
   }

   protected void func_177067_a(T var1, double var2, double var4, double var6) {
      if (this.func_177070_b(var1)) {
         this.func_147906_a(var1, var1.func_145748_c_().func_150254_d(), var2, var4, var6, 64);
      }
   }

   protected boolean func_177070_b(T var1) {
      return var1.func_94059_bO() && var1.func_145818_k_();
   }

   protected void func_177069_a(T var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      this.func_147906_a(var1, var8, var2, var4, var6, 64);
   }

   protected abstract ResourceLocation func_110775_a(T var1);

   protected boolean func_180548_c(T var1) {
      ResourceLocation var2 = this.func_110775_a(var1);
      if (var2 == null) {
         return false;
      } else {
         this.func_110776_a(var2);
         return true;
      }
   }

   public void func_110776_a(ResourceLocation var1) {
      this.field_76990_c.field_78724_e.func_110577_a(var1);
   }

   private void func_76977_a(Entity var1, double var2, double var4, double var6, float var8) {
      GlStateManager.func_179140_f();
      TextureMap var9 = Minecraft.func_71410_x().func_147117_R();
      TextureAtlasSprite var10 = var9.func_110572_b("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite var11 = var9.func_110572_b("minecraft:blocks/fire_layer_1");
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      float var12 = var1.field_70130_N * 1.4F;
      GlStateManager.func_179152_a(var12, var12, var12);
      Tessellator var13 = Tessellator.func_178181_a();
      WorldRenderer var14 = var13.func_178180_c();
      float var15 = 0.5F;
      float var16 = 0.0F;
      float var17 = var1.field_70131_O / var12;
      float var18 = (float)(var1.field_70163_u - var1.func_174813_aQ().field_72338_b);
      GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.0F, -0.3F + (float)((int)var17) * 0.02F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var19 = 0.0F;
      int var20 = 0;
      var14.func_181668_a(7, DefaultVertexFormats.field_181707_g);

      while(var17 > 0.0F) {
         TextureAtlasSprite var21 = var20 % 2 == 0 ? var10 : var11;
         this.func_110776_a(TextureMap.field_110575_b);
         float var22 = var21.func_94209_e();
         float var23 = var21.func_94206_g();
         float var24 = var21.func_94212_f();
         float var25 = var21.func_94210_h();
         if (var20 / 2 % 2 == 0) {
            float var26 = var24;
            var24 = var22;
            var22 = var26;
         }

         var14.func_181662_b((double)(var15 - var16), (double)(0.0F - var18), (double)var19).func_181673_a((double)var24, (double)var25).func_181675_d();
         var14.func_181662_b((double)(-var15 - var16), (double)(0.0F - var18), (double)var19).func_181673_a((double)var22, (double)var25).func_181675_d();
         var14.func_181662_b((double)(-var15 - var16), (double)(1.4F - var18), (double)var19).func_181673_a((double)var22, (double)var23).func_181675_d();
         var14.func_181662_b((double)(var15 - var16), (double)(1.4F - var18), (double)var19).func_181673_a((double)var24, (double)var23).func_181675_d();
         var17 -= 0.45F;
         var18 -= 0.45F;
         var15 *= 0.9F;
         var19 += 0.03F;
         ++var20;
      }

      var13.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179145_e();
   }

   private void func_76975_c(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179112_b(770, 771);
      this.field_76990_c.field_78724_e.func_110577_a(field_110778_a);
      World var10 = this.func_76982_b();
      GlStateManager.func_179132_a(false);
      float var11 = this.field_76989_e;
      if (var1 instanceof EntityLiving) {
         EntityLiving var12 = (EntityLiving)var1;
         var11 *= var12.func_70603_bj();
         if (var12.func_70631_g_()) {
            var11 *= 0.5F;
         }
      }

      double var35 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var9;
      double var14 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var9;
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
      Tessellator var30 = Tessellator.func_178181_a();
      WorldRenderer var31 = var30.func_178180_c();
      var31.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      Iterator var32 = BlockPos.func_177975_b(new BlockPos(var18, var20, var22), new BlockPos(var19, var21, var23)).iterator();

      while(var32.hasNext()) {
         BlockPos var33 = (BlockPos)var32.next();
         Block var34 = var10.func_180495_p(var33.func_177977_b()).func_177230_c();
         if (var34.func_149645_b() != -1 && var10.func_175671_l(var33) > 3) {
            this.func_180549_a(var34, var2, var4, var6, var33, var8, var11, var24, var26, var28);
         }
      }

      var30.func_78381_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
   }

   private World func_76982_b() {
      return this.field_76990_c.field_78722_g;
   }

   private void func_180549_a(Block var1, double var2, double var4, double var6, BlockPos var8, float var9, float var10, double var11, double var13, double var15) {
      if (var1.func_149686_d()) {
         Tessellator var17 = Tessellator.func_178181_a();
         WorldRenderer var18 = var17.func_178180_c();
         double var19 = ((double)var9 - (var4 - ((double)var8.func_177956_o() + var13)) / 2.0D) * 0.5D * (double)this.func_76982_b().func_175724_o(var8);
         if (var19 >= 0.0D) {
            if (var19 > 1.0D) {
               var19 = 1.0D;
            }

            double var21 = (double)var8.func_177958_n() + var1.func_149704_x() + var11;
            double var23 = (double)var8.func_177958_n() + var1.func_149753_y() + var11;
            double var25 = (double)var8.func_177956_o() + var1.func_149665_z() + var13 + 0.015625D;
            double var27 = (double)var8.func_177952_p() + var1.func_149706_B() + var15;
            double var29 = (double)var8.func_177952_p() + var1.func_149693_C() + var15;
            float var31 = (float)((var2 - var21) / 2.0D / (double)var10 + 0.5D);
            float var32 = (float)((var2 - var23) / 2.0D / (double)var10 + 0.5D);
            float var33 = (float)((var6 - var27) / 2.0D / (double)var10 + 0.5D);
            float var34 = (float)((var6 - var29) / 2.0D / (double)var10 + 0.5D);
            var18.func_181662_b(var21, var25, var27).func_181673_a((double)var31, (double)var33).func_181666_a(1.0F, 1.0F, 1.0F, (float)var19).func_181675_d();
            var18.func_181662_b(var21, var25, var29).func_181673_a((double)var31, (double)var34).func_181666_a(1.0F, 1.0F, 1.0F, (float)var19).func_181675_d();
            var18.func_181662_b(var23, var25, var29).func_181673_a((double)var32, (double)var34).func_181666_a(1.0F, 1.0F, 1.0F, (float)var19).func_181675_d();
            var18.func_181662_b(var23, var25, var27).func_181673_a((double)var32, (double)var33).func_181666_a(1.0F, 1.0F, 1.0F, (float)var19).func_181675_d();
         }
      }
   }

   public static void func_76978_a(AxisAlignedBB var0, double var1, double var3, double var5) {
      GlStateManager.func_179090_x();
      Tessellator var7 = Tessellator.func_178181_a();
      WorldRenderer var8 = var7.func_178180_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      var8.func_178969_c(var1, var3, var5);
      var8.func_181668_a(7, DefaultVertexFormats.field_181708_h);
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      var8.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      var7.func_78381_a();
      var8.func_178969_c(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179098_w();
   }

   public void func_76979_b(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      if (this.field_76990_c.field_78733_k != null) {
         if (this.field_76990_c.field_78733_k.field_181151_V && this.field_76989_e > 0.0F && !var1.func_82150_aj() && this.field_76990_c.func_178627_a()) {
            double var10 = this.field_76990_c.func_78714_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
            float var12 = (float)((1.0D - var10 / 256.0D) * (double)this.field_76987_f);
            if (var12 > 0.0F) {
               this.func_76975_c(var1, var2, var4, var6, var12, var9);
            }
         }

         if (var1.func_90999_ad() && (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v())) {
            this.func_76977_a(var1, var2, var4, var6, var9);
         }

      }
   }

   public FontRenderer func_76983_a() {
      return this.field_76990_c.func_78716_a();
   }

   protected void func_147906_a(T var1, String var2, double var3, double var5, double var7, int var9) {
      double var10 = var1.func_70068_e(this.field_76990_c.field_78734_h);
      if (var10 <= (double)(var9 * var9)) {
         FontRenderer var12 = this.func_76983_a();
         float var13 = 1.6F;
         float var14 = 0.016666668F * var13;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var3 + 0.0F, (float)var5 + var1.field_70131_O + 0.5F, (float)var7);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(-var14, -var14, var14);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         Tessellator var15 = Tessellator.func_178181_a();
         WorldRenderer var16 = var15.func_178180_c();
         byte var17 = 0;
         if (var2.equals("deadmau5")) {
            var17 = -10;
         }

         int var18 = var12.func_78256_a(var2) / 2;
         GlStateManager.func_179090_x();
         var16.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         var16.func_181662_b((double)(-var18 - 1), (double)(-1 + var17), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         var16.func_181662_b((double)(-var18 - 1), (double)(8 + var17), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         var16.func_181662_b((double)(var18 + 1), (double)(8 + var17), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         var16.func_181662_b((double)(var18 + 1), (double)(-1 + var17), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         var15.func_78381_a();
         GlStateManager.func_179098_w();
         var12.func_78276_b(var2, -var12.func_78256_a(var2) / 2, var17, 553648127);
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         var12.func_78276_b(var2, -var12.func_78256_a(var2) / 2, var17, -1);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }
   }

   public RenderManager func_177068_d() {
      return this.field_76990_c;
   }
}
