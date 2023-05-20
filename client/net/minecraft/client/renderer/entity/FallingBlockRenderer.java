package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
   private final BlockRenderDispatcher dispatcher;

   public FallingBlockRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.dispatcher = var1.getBlockRenderDispatcher();
   }

   public void render(FallingBlockEntity var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      BlockState var7 = var1.getBlockState();
      if (var7.getRenderShape() == RenderShape.MODEL) {
         Level var8 = var1.getLevel();
         if (var7 != var8.getBlockState(var1.blockPosition()) && var7.getRenderShape() != RenderShape.INVISIBLE) {
            var4.pushPose();
            BlockPos var9 = BlockPos.containing(var1.getX(), var1.getBoundingBox().maxY, var1.getZ());
            var4.translate(-0.5, 0.0, -0.5);
            this.dispatcher
               .getModelRenderer()
               .tesselateBlock(
                  var8,
                  this.dispatcher.getBlockModel(var7),
                  var7,
                  var9,
                  var4,
                  var5.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(var7)),
                  false,
                  RandomSource.create(),
                  var7.getSeed(var1.getStartPos()),
                  OverlayTexture.NO_OVERLAY
               );
            var4.popPose();
            super.render(var1, var2, var3, var4, var5, var6);
         }
      }
   }

   public ResourceLocation getTextureLocation(FallingBlockEntity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
