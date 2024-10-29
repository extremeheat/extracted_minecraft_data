package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.entity.state.MinecartTntRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer extends AbstractMinecartRenderer<MinecartTNT, MinecartTntRenderState> {
   private final BlockRenderDispatcher blockRenderer;

   public TntMinecartRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.TNT_MINECART);
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   protected void renderMinecartContents(MinecartTntRenderState var1, BlockState var2, PoseStack var3, MultiBufferSource var4, int var5) {
      float var6 = var1.fuseRemainingInTicks;
      if (var6 > -1.0F && var6 < 10.0F) {
         float var7 = 1.0F - var6 / 10.0F;
         var7 = Mth.clamp(var7, 0.0F, 1.0F);
         var7 *= var7;
         var7 *= var7;
         float var8 = 1.0F + var7 * 0.3F;
         var3.scale(var8, var8, var8);
      }

      renderWhiteSolidBlock(this.blockRenderer, var2, var3, var4, var5, var6 > -1.0F && (int)var6 / 5 % 2 == 0);
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

   public MinecartTntRenderState createRenderState() {
      return new MinecartTntRenderState();
   }

   public void extractRenderState(MinecartTNT var1, MinecartTntRenderState var2, float var3) {
      super.extractRenderState((AbstractMinecart)var1, (MinecartRenderState)var2, var3);
      var2.fuseRemainingInTicks = var1.getFuse() > -1 ? (float)var1.getFuse() - var3 + 1.0F : -1.0F;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
