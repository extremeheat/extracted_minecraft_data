package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity, FallingBlockRenderState> {
   private final BlockRenderDispatcher dispatcher;

   public FallingBlockRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.dispatcher = var1.getBlockRenderDispatcher();
   }

   public boolean shouldRender(FallingBlockEntity var1, Frustum var2, double var3, double var5, double var7) {
      if (!super.shouldRender(var1, var2, var3, var5, var7)) {
         return false;
      } else {
         return var1.getBlockState() != var1.level().getBlockState(var1.blockPosition());
      }
   }

   public void render(FallingBlockRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      BlockState var5 = var1.blockState;
      if (var5.getRenderShape() == RenderShape.MODEL) {
         var2.pushPose();
         var2.translate(-0.5, 0.0, -0.5);
         this.dispatcher.getModelRenderer().tesselateBlock(var1, this.dispatcher.getBlockModel(var5), var5, var1.blockPos, var2, var3.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(var5)), false, RandomSource.create(), var5.getSeed(var1.startBlockPos), OverlayTexture.NO_OVERLAY);
         var2.popPose();
         super.render(var1, var2, var3, var4);
      }
   }

   public FallingBlockRenderState createRenderState() {
      return new FallingBlockRenderState();
   }

   public void extractRenderState(FallingBlockEntity var1, FallingBlockRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      BlockPos var4 = BlockPos.containing(var1.getX(), var1.getBoundingBox().maxY, var1.getZ());
      var2.startBlockPos = var1.getStartPos();
      var2.blockPos = var4;
      var2.blockState = var1.getBlockState();
      var2.biome = var1.level().getBiome(var4);
      var2.level = var1.level();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
