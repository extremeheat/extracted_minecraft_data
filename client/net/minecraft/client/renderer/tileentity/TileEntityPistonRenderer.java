package net.minecraft.client.renderer.tileentity;

import java.util.Random;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntityRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher field_178462_c = Minecraft.func_71410_x().func_175602_ab();

   public TileEntityPistonRenderer() {
      super();
   }

   public void func_199341_a(TileEntityPiston var1, double var2, double var4, double var6, float var8, int var9) {
      BlockPos var10 = var1.func_174877_v().func_177972_a(var1.func_195509_h().func_176734_d());
      IBlockState var11 = var1.func_200230_i();
      if (!var11.func_196958_f() && var1.func_145860_a(var8) < 1.0F) {
         Tessellator var12 = Tessellator.func_178181_a();
         BufferBuilder var13 = var12.func_178180_c();
         this.func_147499_a(TextureMap.field_110575_b);
         RenderHelper.func_74518_a();
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.func_179147_l();
         GlStateManager.func_179129_p();
         if (Minecraft.func_71379_u()) {
            GlStateManager.func_179103_j(7425);
         } else {
            GlStateManager.func_179103_j(7424);
         }

         var13.func_181668_a(7, DefaultVertexFormats.field_176600_a);
         var13.func_178969_c(var2 - (double)var10.func_177958_n() + (double)var1.func_174929_b(var8), var4 - (double)var10.func_177956_o() + (double)var1.func_174928_c(var8), var6 - (double)var10.func_177952_p() + (double)var1.func_174926_d(var8));
         World var14 = this.func_178459_a();
         if (var11.func_177230_c() == Blocks.field_150332_K && var1.func_145860_a(var8) <= 4.0F) {
            var11 = (IBlockState)var11.func_206870_a(BlockPistonExtension.field_176327_M, true);
            this.func_188186_a(var10, var11, var13, var14, false);
         } else if (var1.func_145867_d() && !var1.func_145868_b()) {
            PistonType var15 = var11.func_177230_c() == Blocks.field_150320_F ? PistonType.STICKY : PistonType.DEFAULT;
            IBlockState var16 = (IBlockState)((IBlockState)Blocks.field_150332_K.func_176223_P().func_206870_a(BlockPistonExtension.field_176325_b, var15)).func_206870_a(BlockPistonExtension.field_176387_N, var11.func_177229_b(BlockPistonBase.field_176387_N));
            var16 = (IBlockState)var16.func_206870_a(BlockPistonExtension.field_176327_M, var1.func_145860_a(var8) >= 0.5F);
            this.func_188186_a(var10, var16, var13, var14, false);
            BlockPos var17 = var10.func_177972_a(var1.func_195509_h());
            var13.func_178969_c(var2 - (double)var17.func_177958_n(), var4 - (double)var17.func_177956_o(), var6 - (double)var17.func_177952_p());
            var11 = (IBlockState)var11.func_206870_a(BlockPistonBase.field_176320_b, true);
            this.func_188186_a(var17, var11, var13, var14, true);
         } else {
            this.func_188186_a(var10, var11, var13, var14, false);
         }

         var13.func_178969_c(0.0D, 0.0D, 0.0D);
         var12.func_78381_a();
         RenderHelper.func_74519_b();
      }
   }

   private boolean func_188186_a(BlockPos var1, IBlockState var2, BufferBuilder var3, World var4, boolean var5) {
      return this.field_178462_c.func_175019_b().func_199324_a(var4, this.field_178462_c.func_184389_a(var2), var2, var1, var3, var5, new Random(), var2.func_209533_a(var1));
   }
}
