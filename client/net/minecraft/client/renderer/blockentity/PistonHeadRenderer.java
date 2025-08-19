package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;

public class PistonHeadRenderer implements BlockEntityRenderer<PistonMovingBlockEntity> {
   public PistonHeadRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void submit(PistonMovingBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Level var9 = var1.getLevel();
      if (var9 != null) {
         BlockPos var10 = var1.getBlockPos().relative(var1.getMovementDirection().getOpposite());
         BlockState var11 = var1.getMovedState();
         if (!var11.isAir()) {
            ModelBlockRenderer.enableCaching();
            var3.pushPose();
            var3.translate(var1.getXOff(var2), var1.getYOff(var2), var1.getZOff(var2));
            Holder var12 = var9.getBiome(var10);
            if (var11.is(Blocks.PISTON_HEAD) && var1.getProgress(var2) <= 4.0F) {
               var11 = (BlockState)var11.setValue(PistonHeadBlock.SHORT, var1.getProgress(var2) <= 0.5F);
               submitMovingBlock(var3, var8, var10, var11, var12, var9);
            } else if (var1.isSourcePiston() && !var1.isExtending()) {
               PistonType var13 = var11.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
               BlockState var14 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, var13)).setValue(PistonHeadBlock.FACING, (Direction)var11.getValue(PistonBaseBlock.FACING));
               var14 = (BlockState)var14.setValue(PistonHeadBlock.SHORT, var1.getProgress(var2) >= 0.5F);
               submitMovingBlock(var3, var8, var10, var14, var12, var9);
               BlockPos var15 = var10.relative(var1.getMovementDirection());
               var3.popPose();
               var3.pushPose();
               var11 = (BlockState)var11.setValue(PistonBaseBlock.EXTENDED, true);
               submitMovingBlock(var3, var8, var15, var11, var12, var9);
            } else {
               submitMovingBlock(var3, var8, var10, var11, var12, var9);
            }

            var3.popPose();
            ModelBlockRenderer.clearCache();
         }
      }
   }

   private static void submitMovingBlock(PoseStack var0, SubmitNodeCollector var1, BlockPos var2, BlockState var3, Holder<Biome> var4, Level var5) {
      MovingBlockRenderState var6 = new MovingBlockRenderState();
      var6.randomSeedPos = var2;
      var6.blockPos = var2;
      var6.blockState = var3;
      var6.biome = var4;
      var6.level = var5;
      var1.submitMovingBlock(var0, var6);
   }

   public int getViewDistance() {
      return 68;
   }
}
