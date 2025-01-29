package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;

public class PistonHeadRenderer implements BlockEntityRenderer<PistonMovingBlockEntity> {
   private final BlockRenderDispatcher blockRenderer;

   public PistonHeadRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   public void render(PistonMovingBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Level var8 = var1.getLevel();
      if (var8 != null) {
         BlockPos var9 = var1.getBlockPos().relative(var1.getMovementDirection().getOpposite());
         BlockState var10 = var1.getMovedState();
         if (!var10.isAir()) {
            ModelBlockRenderer.enableCaching();
            var3.pushPose();
            var3.translate(var1.getXOff(var2), var1.getYOff(var2), var1.getZOff(var2));
            if (var10.is(Blocks.PISTON_HEAD) && var1.getProgress(var2) <= 4.0F) {
               var10 = (BlockState)var10.setValue(PistonHeadBlock.SHORT, var1.getProgress(var2) <= 0.5F);
               this.renderBlock(var9, var10, var3, var4, var8, false, var6);
            } else if (var1.isSourcePiston() && !var1.isExtending()) {
               PistonType var11 = var10.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
               BlockState var12 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, var11)).setValue(PistonHeadBlock.FACING, (Direction)var10.getValue(PistonBaseBlock.FACING));
               var12 = (BlockState)var12.setValue(PistonHeadBlock.SHORT, var1.getProgress(var2) >= 0.5F);
               this.renderBlock(var9, var12, var3, var4, var8, false, var6);
               BlockPos var13 = var9.relative(var1.getMovementDirection());
               var3.popPose();
               var3.pushPose();
               var10 = (BlockState)var10.setValue(PistonBaseBlock.EXTENDED, true);
               this.renderBlock(var13, var10, var3, var4, var8, true, var6);
            } else {
               this.renderBlock(var9, var10, var3, var4, var8, false, var6);
            }

            var3.popPose();
            ModelBlockRenderer.clearCache();
         }
      }
   }

   private void renderBlock(BlockPos var1, BlockState var2, PoseStack var3, MultiBufferSource var4, Level var5, boolean var6, int var7) {
      RenderType var8 = ItemBlockRenderTypes.getMovingBlockRenderType(var2);
      VertexConsumer var9 = var4.getBuffer(var8);
      this.blockRenderer.getModelRenderer().tesselateBlock(var5, this.blockRenderer.getBlockModel(var2), var2, var1, var3, var9, var6, RandomSource.create(), var2.getSeed(var1), var7);
   }

   public int getViewDistance() {
      return 68;
   }
}
