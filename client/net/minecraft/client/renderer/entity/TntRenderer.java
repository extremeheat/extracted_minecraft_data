package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;

public class TntRenderer extends EntityRenderer<PrimedTnt, TntRenderState> {
   private final BlockRenderDispatcher blockRenderer;

   public TntRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   public void render(TntRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.5F, 0.0F);
      float var5 = var1.fuseRemainingInTicks;
      if (var1.fuseRemainingInTicks < 10.0F) {
         float var6 = 1.0F - var1.fuseRemainingInTicks / 10.0F;
         var6 = Mth.clamp(var6, 0.0F, 1.0F);
         var6 *= var6;
         var6 *= var6;
         float var7 = 1.0F + var6 * 0.3F;
         var2.scale(var7, var7, var7);
      }

      var2.mulPose(Axis.YP.rotationDegrees(-90.0F));
      var2.translate(-0.5F, -0.5F, 0.5F);
      var2.mulPose(Axis.YP.rotationDegrees(90.0F));
      if (var1.blockState != null) {
         TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, var1.blockState, var2, var3, var4, (int)var5 / 5 % 2 == 0);
      }

      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ResourceLocation getTextureLocation(TntRenderState var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public TntRenderState createRenderState() {
      return new TntRenderState();
   }

   public void extractRenderState(PrimedTnt var1, TntRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.fuseRemainingInTicks = (float)var1.getFuse() - var3 + 1.0F;
      var2.blockState = var1.getBlockState();
   }
}
