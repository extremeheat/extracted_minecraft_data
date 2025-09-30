package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
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

public class PistonHeadRenderer implements BlockEntityRenderer<PistonMovingBlockEntity, PistonHeadRenderState> {
   public PistonHeadRenderer() {
      super();
   }

   public PistonHeadRenderState createRenderState() {
      return new PistonHeadRenderState();
   }

   public void extractRenderState(PistonMovingBlockEntity var1, PistonHeadRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.xOffset = var1.getXOff(var3);
      var2.yOffset = var1.getYOff(var3);
      var2.zOffset = var1.getZOff(var3);
      var2.block = null;
      var2.base = null;
      BlockState var6 = var1.getMovedState();
      Level var7 = var1.getLevel();
      if (var7 != null && !var6.isAir()) {
         BlockPos var8 = var1.getBlockPos().relative(var1.getMovementDirection().getOpposite());
         Holder var9 = var7.getBiome(var8);
         if (var6.is(Blocks.PISTON_HEAD) && var1.getProgress(var3) <= 4.0F) {
            var6 = (BlockState)var6.setValue(PistonHeadBlock.SHORT, var1.getProgress(var3) <= 0.5F);
            var2.block = createMovingBlock(var8, var6, var9, var7);
         } else if (var1.isSourcePiston() && !var1.isExtending()) {
            PistonType var10 = var6.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState var11 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, var10)).setValue(PistonHeadBlock.FACING, (Direction)var6.getValue(PistonBaseBlock.FACING));
            var11 = (BlockState)var11.setValue(PistonHeadBlock.SHORT, var1.getProgress(var3) >= 0.5F);
            var2.block = createMovingBlock(var8, var11, var9, var7);
            BlockPos var12 = var8.relative(var1.getMovementDirection());
            var6 = (BlockState)var6.setValue(PistonBaseBlock.EXTENDED, true);
            var2.base = createMovingBlock(var12, var6, var9, var7);
         } else {
            var2.block = createMovingBlock(var8, var6, var9, var7);
         }
      }

   }

   public void submit(PistonHeadRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.block != null) {
         var2.pushPose();
         var2.translate(var1.xOffset, var1.yOffset, var1.zOffset);
         var3.submitMovingBlock(var2, var1.block);
         var2.popPose();
         if (var1.base != null) {
            var3.submitMovingBlock(var2, var1.base);
         }

      }
   }

   private static MovingBlockRenderState createMovingBlock(BlockPos var0, BlockState var1, Holder<Biome> var2, Level var3) {
      MovingBlockRenderState var4 = new MovingBlockRenderState();
      var4.randomSeedPos = var0;
      var4.blockPos = var0;
      var4.blockState = var1;
      var4.biome = var2;
      var4.level = var3;
      return var4;
   }

   public int getViewDistance() {
      return 68;
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
