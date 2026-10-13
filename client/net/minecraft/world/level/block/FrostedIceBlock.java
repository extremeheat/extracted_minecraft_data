package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class FrostedIceBlock extends CrackingIceBlock {
   private static final int NEIGHBORS_TO_AGE = 4;
   private static final int NEIGHBORS_TO_MELT = 2;
   private static final int MIN_TICK_DELAY = 20;
   private static final int MAX_TICK_DELAY = 40;

   public FrostedIceBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 60, 120));
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (this.shouldEnvironmentIncreaseCracking(state, level, pos, random) && this.slightlyMelt(state, level, pos)) {
         this.propagateMelting(level, pos, random);
      } else {
         level.scheduleTick(pos, this, this.getTickDelay(random));
      }

   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (block.defaultBlockState().is(this) && this.fewerNeigboursThan(level, pos, 2)) {
         this.melt(state, level, pos);
      }

      super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
   }

   private boolean fewerNeigboursThan(final BlockGetter level, final BlockPos pos, final int limit) {
      int result = 0;
      BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         neighborPos.setWithOffset(pos, (Direction)direction);
         if (level.getBlockState(neighborPos).is(this)) {
            ++result;
            if (result >= limit) {
               return false;
            }
         }
      }

      return true;
   }

   protected boolean shouldEnvironmentIncreaseCracking(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (random.nextInt(3) != 0 && !this.fewerNeigboursThan(level, pos, 4)) {
         return false;
      } else {
         int brightness = level.dimension() == Level.END ? level.getBrightness(LightLayer.BLOCK, pos) : level.getMaxLocalRawBrightness(pos);
         return brightness > 11 - (Integer)state.getValue(AGE) - state.getLightDampening();
      }
   }

   protected int getTickDelay(final RandomSource random) {
      return Mth.nextInt(random, 20, 40);
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return ItemStack.EMPTY;
   }
}
