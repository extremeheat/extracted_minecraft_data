package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer extends MinecartRenderer<MinecartTNT> {
   private final BlockRenderDispatcher blockRenderer;

   public TntMinecartRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.TNT_MINECART);
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   protected void renderMinecartContents(MinecartTNT var1, float var2, BlockState var3, PoseStack var4, MultiBufferSource var5, int var6) {
      int var7 = var1.getFuse();
      if (var7 > -1 && (float)var7 - var2 + 1.0F < 10.0F) {
         float var8 = 1.0F - ((float)var7 - var2 + 1.0F) / 10.0F;
         var8 = Mth.clamp(var8, 0.0F, 1.0F);
         var8 *= var8;
         var8 *= var8;
         float var9 = 1.0F + var8 * 0.3F;
         var4.scale(var9, var9, var9);
      }

      renderWhiteSolidBlock(this.blockRenderer, var3, var4, var5, var6, var7 > -1 && var7 / 5 % 2 == 0);
   }

   public static void renderWhiteSolidBlock(BlockRenderDispatcher var0, BlockState var1, PoseStack var2, MultiBufferSource var3, int var4, boolean var5) {
      int var6;
      if (var5) {
         var6 = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
      } else {
         var6 = OverlayTexture.NO_OVERLAY;
      }

      var0.renderSingleBlock(var1, var2, var3, var4, var6);
   }
}
