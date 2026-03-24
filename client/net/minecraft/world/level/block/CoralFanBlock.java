package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CoralFanBlock extends BaseCoralFanBlock {
   public static final MapCodec<CoralFanBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(CoralBlock.DEAD_CORAL_FIELD.forGetter((b) -> b.deadBlock), propertiesCodec()).apply(i, CoralFanBlock::new));
   private final Block deadBlock;

   public MapCodec<CoralFanBlock> codec() {
      return CODEC;
   }

   protected CoralFanBlock(final Block deadBlock, final BlockBehaviour.Properties properties) {
      super(properties);
      this.deadBlock = deadBlock;
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      this.tryScheduleDieTick(state, level, level, level.getRandom(), pos);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!scanForWater(state, level, pos)) {
         level.setBlock(pos, (BlockState)this.deadBlock.defaultBlockState().setValue(WATERLOGGED, false), 2);
      }

   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(state, level, ticks, random, pos);
         if ((Boolean)state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }
}
