package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntitySpecialRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher field_178462_c = Minecraft.func_71410_x().func_175602_ab();

   public TileEntityPistonRenderer() {
      super();
   }

   public void func_180535_a(TileEntityPiston var1, double var2, double var4, double var6, float var8, int var9) {
      BlockPos var10 = var1.func_174877_v();
      IBlockState var11 = var1.func_174927_b();
      Block var12 = var11.func_177230_c();
      if (var12.func_149688_o() != Material.field_151579_a && var1.func_145860_a(var8) < 1.0F) {
         Tessellator var13 = Tessellator.func_178181_a();
         WorldRenderer var14 = var13.func_178180_c();
         this.func_147499_a(TextureMap.field_110575_b);
         RenderHelper.func_74518_a();
         GlStateManager.func_179112_b(770, 771);
         GlStateManager.func_179147_l();
         GlStateManager.func_179129_p();
         if (Minecraft.func_71379_u()) {
            GlStateManager.func_179103_j(7425);
         } else {
            GlStateManager.func_179103_j(7424);
         }

         var14.func_181668_a(7, DefaultVertexFormats.field_176600_a);
         var14.func_178969_c((double)((float)var2 - (float)var10.func_177958_n() + var1.func_174929_b(var8)), (double)((float)var4 - (float)var10.func_177956_o() + var1.func_174928_c(var8)), (double)((float)var6 - (float)var10.func_177952_p() + var1.func_174926_d(var8)));
         World var15 = this.func_178459_a();
         if (var12 == Blocks.field_150332_K && var1.func_145860_a(var8) < 0.5F) {
            var11 = var11.func_177226_a(BlockPistonExtension.field_176327_M, true);
            this.field_178462_c.func_175019_b().func_178267_a(var15, this.field_178462_c.func_175022_a(var11, var15, var10), var11, var10, var14, true);
         } else if (var1.func_145867_d() && !var1.func_145868_b()) {
            BlockPistonExtension.EnumPistonType var16 = var12 == Blocks.field_150320_F ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
            IBlockState var17 = Blocks.field_150332_K.func_176223_P().func_177226_a(BlockPistonExtension.field_176325_b, var16).func_177226_a(BlockPistonExtension.field_176326_a, var11.func_177229_b(BlockPistonBase.field_176321_a));
            var17 = var17.func_177226_a(BlockPistonExtension.field_176327_M, var1.func_145860_a(var8) >= 0.5F);
            this.field_178462_c.func_175019_b().func_178267_a(var15, this.field_178462_c.func_175022_a(var17, var15, var10), var17, var10, var14, true);
            var14.func_178969_c((double)((float)var2 - (float)var10.func_177958_n()), (double)((float)var4 - (float)var10.func_177956_o()), (double)((float)var6 - (float)var10.func_177952_p()));
            var11.func_177226_a(BlockPistonBase.field_176320_b, true);
            this.field_178462_c.func_175019_b().func_178267_a(var15, this.field_178462_c.func_175022_a(var11, var15, var10), var11, var10, var14, true);
         } else {
            this.field_178462_c.func_175019_b().func_178267_a(var15, this.field_178462_c.func_175022_a(var11, var15, var10), var11, var10, var14, false);
         }

         var14.func_178969_c(0.0D, 0.0D, 0.0D);
         var13.func_78381_a();
         RenderHelper.func_74519_b();
      }
   }
}
