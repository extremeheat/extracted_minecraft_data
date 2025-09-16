package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity, FallingBlockRenderState> {
   public FallingBlockRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public boolean shouldRender(FallingBlockEntity var1, Frustum var2, double var3, double var5, double var7) {
      if (!super.shouldRender(var1, var2, var3, var5, var7)) {
         return false;
      } else {
         return var1.getBlockState() != var1.level().getBlockState(var1.blockPosition());
      }
   }

   public void submit(FallingBlockRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      BlockState var5 = var1.movingBlockRenderState.blockState;
      if (var5.getRenderShape() == RenderShape.MODEL) {
         var2.pushPose();
         var2.translate(-0.5, 0.0, -0.5);
         var3.submitMovingBlock(var2, var1.movingBlockRenderState);
         var2.popPose();
         super.submit(var1, var2, var3, var4);
      }
   }

   public FallingBlockRenderState createRenderState() {
      return new FallingBlockRenderState();
   }

   public void extractRenderState(FallingBlockEntity var1, FallingBlockRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      BlockPos var4 = BlockPos.containing(var1.getX(), var1.getBoundingBox().maxY, var1.getZ());
      var2.movingBlockRenderState.randomSeedPos = var1.getStartPos();
      var2.movingBlockRenderState.blockPos = var4;
      var2.movingBlockRenderState.blockState = var1.getBlockState();
      var2.movingBlockRenderState.biome = var1.level().getBiome(var4);
      var2.movingBlockRenderState.level = var1.level();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
