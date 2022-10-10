package net.minecraft.client.renderer.entity;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorldReaderBase;

public abstract class Render<T extends Entity> {
   private static final ResourceLocation field_110778_a = new ResourceLocation("textures/misc/shadow.png");
   protected final RenderManager field_76990_c;
   protected float field_76989_e;
   protected float field_76987_f = 1.0F;
   protected boolean field_188301_f;

   protected Render(RenderManager var1) {
      super();
      this.field_76990_c = var1;
   }

   public void func_188297_a(boolean var1) {
      this.field_188301_f = var1;
   }

   public boolean func_177071_a(T var1, ICamera var2, double var3, double var5, double var7) {
      AxisAlignedBB var9 = var1.func_184177_bl().func_186662_g(0.5D);
      if (var9.func_181656_b() || var9.func_72320_b() == 0.0D) {
         var9 = new AxisAlignedBB(var1.field_70165_t - 2.0D, var1.field_70163_u - 2.0D, var1.field_70161_v - 2.0D, var1.field_70165_t + 2.0D, var1.field_70163_u + 2.0D, var1.field_70161_v + 2.0D);
      }

      return var1.func_145770_h(var3, var5, var7) && (var1.field_70158_ak || var2.func_78546_a(var9));
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      if (!this.field_188301_f) {
         this.func_177067_a(var1, var2, var4, var6);
      }

   }

   protected int func_188298_c(T var1) {
      ScorePlayerTeam var2 = (ScorePlayerTeam)var1.func_96124_cp();
      return var2 != null && var2.func_178775_l().func_211163_e() != null ? var2.func_178775_l().func_211163_e() : 16777215;
   }

   protected void func_177067_a(T var1, double var2, double var4, double var6) {
      if (this.func_177070_b(var1)) {
         this.func_147906_a(var1, var1.func_145748_c_().func_150254_d(), var2, var4, var6, 64);
      }
   }

   protected boolean func_177070_b(T var1) {
      return var1.func_94059_bO() && var1.func_145818_k_();
   }

   protected void func_188296_a(T var1, double var2, double var4, double var6, String var8, double var9) {
      this.func_147906_a(var1, var8, var2, var4, var6, 64);
   }

   @Nullable
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
      TextureAtlasSprite var10 = var9.func_195424_a(ModelBakery.field_207763_a);
      TextureAtlasSprite var11 = var9.func_195424_a(ModelBakery.field_207764_b);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      float var12 = var1.field_70130_N * 1.4F;
      GlStateManager.func_179152_a(var12, var12, var12);
      Tessellator var13 = Tessellator.func_178181_a();
      BufferBuilder var14 = var13.func_178180_c();
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

         var14.func_181662_b((double)(var15 - 0.0F), (double)(0.0F - var18), (double)var19).func_187315_a((double)var24, (double)var25).func_181675_d();
         var14.func_181662_b((double)(-var15 - 0.0F), (double)(0.0F - var18), (double)var19).func_187315_a((double)var22, (double)var25).func_181675_d();
         var14.func_181662_b((double)(-var15 - 0.0F), (double)(1.4F - var18), (double)var19).func_187315_a((double)var22, (double)var23).func_181675_d();
         var14.func_181662_b((double)(var15 - 0.0F), (double)(1.4F - var18), (double)var19).func_187315_a((double)var24, (double)var23).func_181675_d();
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
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.field_76990_c.field_78724_e.func_110577_a(field_110778_a);
      IWorldReaderBase var10 = this.func_76982_b();
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
      BufferBuilder var31 = var30.func_178180_c();
      var31.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      Iterator var32 = BlockPos.func_177975_b(new BlockPos(var18, var20, var22), new BlockPos(var19, var21, var23)).iterator();

      while(var32.hasNext()) {
         BlockPos var33 = (BlockPos)var32.next();
         IBlockState var34 = var10.func_180495_p(var33.func_177977_b());
         if (var34.func_185901_i() != EnumBlockRenderType.INVISIBLE && var10.func_201696_r(var33) > 3) {
            this.func_188299_a(var34, var2, var4, var6, var33, var8, var11, var24, var26, var28);
         }
      }

      var30.func_78381_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
   }

   private IWorldReaderBase func_76982_b() {
      return this.field_76990_c.field_78722_g;
   }

   private void func_188299_a(IBlockState var1, double var2, double var4, double var6, BlockPos var8, float var9, float var10, double var11, double var13, double var15) {
      if (var1.func_185917_h()) {
         VoxelShape var17 = var1.func_196954_c(this.func_76982_b(), var8.func_177977_b());
         if (!var17.func_197766_b()) {
            Tessellator var18 = Tessellator.func_178181_a();
            BufferBuilder var19 = var18.func_178180_c();
            double var20 = ((double)var9 - (var4 - ((double)var8.func_177956_o() + var13)) / 2.0D) * 0.5D * (double)this.func_76982_b().func_205052_D(var8);
            if (var20 >= 0.0D) {
               if (var20 > 1.0D) {
                  var20 = 1.0D;
               }

               AxisAlignedBB var22 = var17.func_197752_a();
               double var23 = (double)var8.func_177958_n() + var22.field_72340_a + var11;
               double var25 = (double)var8.func_177958_n() + var22.field_72336_d + var11;
               double var27 = (double)var8.func_177956_o() + var22.field_72338_b + var13 + 0.015625D;
               double var29 = (double)var8.func_177952_p() + var22.field_72339_c + var15;
               double var31 = (double)var8.func_177952_p() + var22.field_72334_f + var15;
               float var33 = (float)((var2 - var23) / 2.0D / (double)var10 + 0.5D);
               float var34 = (float)((var2 - var25) / 2.0D / (double)var10 + 0.5D);
               float var35 = (float)((var6 - var29) / 2.0D / (double)var10 + 0.5D);
               float var36 = (float)((var6 - var31) / 2.0D / (double)var10 + 0.5D);
               var19.func_181662_b(var23, var27, var29).func_187315_a((double)var33, (double)var35).func_181666_a(1.0F, 1.0F, 1.0F, (float)var20).func_181675_d();
               var19.func_181662_b(var23, var27, var31).func_187315_a((double)var33, (double)var36).func_181666_a(1.0F, 1.0F, 1.0F, (float)var20).func_181675_d();
               var19.func_181662_b(var25, var27, var31).func_187315_a((double)var34, (double)var36).func_181666_a(1.0F, 1.0F, 1.0F, (float)var20).func_181675_d();
               var19.func_181662_b(var25, var27, var29).func_187315_a((double)var34, (double)var35).func_181666_a(1.0F, 1.0F, 1.0F, (float)var20).func_181675_d();
            }
         }
      }
   }

   public static void func_76978_a(AxisAlignedBB var0, double var1, double var3, double var5) {
      GlStateManager.func_179090_x();
      Tessellator var7 = Tessellator.func_178181_a();
      BufferBuilder var8 = var7.func_178180_c();
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
         boolean var12 = var1.func_70093_af();
         float var13 = this.field_76990_c.field_78735_i;
         float var14 = this.field_76990_c.field_78732_j;
         boolean var15 = this.field_76990_c.field_78733_k.field_74320_O == 2;
         float var16 = var1.field_70131_O + 0.5F - (var12 ? 0.25F : 0.0F);
         int var17 = "deadmau5".equals(var2) ? -10 : 0;
         GameRenderer.func_189692_a(this.func_76983_a(), var2, (float)var3, (float)var5 + var16, (float)var7, var17, var13, var14, var15, var12);
      }
   }

   public RenderManager func_177068_d() {
      return this.field_76990_c;
   }

   public boolean func_188295_H_() {
      return false;
   }

   public void func_188300_b(T var1, double var2, double var4, double var6, float var8, float var9) {
   }
}
