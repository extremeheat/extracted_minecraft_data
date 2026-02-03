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

   public static void extractBase(final BlockEntity blockEntity, final BlockEntityRenderState state, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      state.blockPos = blockEntity.getBlockPos();
      state.blockState = blockEntity.getBlockState();
      state.blockEntityType = blockEntity.getType();
      state.lightCoords = blockEntity.getLevel() != null ? LevelRenderer.getLightCoords(blockEntity.getLevel(), blockEntity.getBlockPos()) : 15728880;
      state.breakProgress = breakProgress;
   }

   public void fillCrashReportCategory(final CrashReportCategory category) {
      category.setDetail("BlockEntityRenderState", this.getClass().getCanonicalName());
      category.setDetail("Position", this.blockPos);
      BlockState var10002 = this.blockState;
      Objects.requireNonNull(var10002);
      category.setDetail("Block state", var10002::toString);
   }
}
