package net.minecraft.client.renderer.blockentity.state;

import java.util.Objects;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockEntityRenderState {
   public BlockPos blockPos;
   public BlockState blockState;
   public BlockEntityType<?> blockEntityType;
   public int lightCoords;
   public ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress;

   public BlockEntityRenderState() {
      super();
      this.blockPos = BlockPos.ZERO;
      this.blockState = Blocks.AIR.defaultBlockState();
      this.blockEntityType = BlockEntityType.TEST_BLOCK;
   }

   public static void extractBase(BlockEntity var0, BlockEntityRenderState var1, ModelFeatureRenderer.@Nullable CrumblingOverlay var2) {
      var1.blockPos = var0.getBlockPos();
      var1.blockState = var0.getBlockState();
      var1.blockEntityType = var0.getType();
      var1.lightCoords = var0.getLevel() != null ? LevelRenderer.getLightColor(var0.getLevel(), var0.getBlockPos()) : 15728880;
      var1.breakProgress = var2;
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("BlockEntityRenderState", this.getClass().getCanonicalName());
      var1.setDetail("Position", this.blockPos);
      BlockState var10002 = this.blockState;
      Objects.requireNonNull(var10002);
      var1.setDetail("Block state", var10002::toString);
   }
}
