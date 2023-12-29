package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;

public class TntRenderer extends EntityRenderer<PrimedTnt> {
   private final BlockRenderDispatcher blockRenderer;

   public TntRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   public void render(PrimedTnt var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.translate(0.0F, 0.5F, 0.0F);
      int var7 = var1.getFuse();
      if ((float)var7 - var3 + 1.0F < 10.0F) {
         float var8 = 1.0F - ((float)var7 - var3 + 1.0F) / 10.0F;
         var8 = Mth.clamp(var8, 0.0F, 1.0F);
         var8 *= var8;
         var8 *= var8;
         float var9 = 1.0F + var8 * 0.3F;
         var4.scale(var9, var9, var9);
      }

      var4.mulPose(Axis.YP.rotationDegrees(-90.0F));
      var4.translate(-0.5F, -0.5F, 0.5F);
      var4.mulPose(Axis.YP.rotationDegrees(90.0F));
      TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, var1.getBlockState(), var4, var5, var6, var7 / 5 % 2 == 0);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(PrimedTnt var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
