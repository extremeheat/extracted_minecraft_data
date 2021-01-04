package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer extends MinecartRenderer<MinecartTNT> {
   public TntMinecartRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   protected void renderMinecartContents(MinecartTNT var1, float var2, BlockState var3) {
      int var4 = var1.getFuse();
      if (var4 > -1 && (float)var4 - var2 + 1.0F < 10.0F) {
         float var5 = 1.0F - ((float)var4 - var2 + 1.0F) / 10.0F;
         var5 = Mth.clamp(var5, 0.0F, 1.0F);
         var5 *= var5;
         var5 *= var5;
         float var6 = 1.0F + var5 * 0.3F;
         GlStateManager.scalef(var6, var6, var6);
      }

      super.renderMinecartContents(var1, var2, var3);
      if (var4 > -1 && var4 / 5 % 2 == 0) {
         BlockRenderDispatcher var7 = Minecraft.getInstance().getBlockRenderer();
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)var4 - var2 + 1.0F) / 100.0F) * 0.8F);
         GlStateManager.pushMatrix();
         var7.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
      }

   }
}
