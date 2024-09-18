package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LeavesBlock> CODEC = simpleCodec(LeavesBlock::new);
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final int TICK_DELAY = 1;

   @Override
   public MapCodec<? extends LeavesBlock> codec() {
      return CODEC;
   }

   public LeavesBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(DISTANCE, Integer.valueOf(7))
            .setValue(PERSISTENT, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   protected boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(DISTANCE) == 7 && !var1.getValue(PERSISTENT);
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.decaying(var1)) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }
   }

   protected boolean decaying(BlockState var1) {
      return !var1.getValue(PERSISTENT) && var1.getValue(DISTANCE) == 7;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, updateDistance(var1, var2, var3), 3);
   }

   @Override
   protected int getLightBlock(BlockState var1) {
      return 1;
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      int var9 = getDistanceAt(var7) + 1;
      if (var9 != 1 || var1.getValue(DISTANCE) != var9) {
         var3.scheduleTick(var4, this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for (Direction var8 : Direction.values()) {
         var4.setWithOffset(var2, var8);
         var3 = Math.min(var3, getDistanceAt(var1.getBlockState(var4)) + 1);
         if (var3 == 1) {
            break;
         }
      }

      return var0.setValue(DISTANCE, Integer.valueOf(var3));
   }

   private static int getDistanceAt(BlockState var0) {
      return getOptionalDistanceAt(var0).orElse(7);
   }

   public static OptionalInt getOptionalDistanceAt(BlockState var0) {
      if (var0.is(BlockTags.LOGS)) {
         return OptionalInt.of(0);
      } else {
         return var0.hasProperty(DISTANCE) ? OptionalInt.of(var0.getValue(DISTANCE)) : OptionalInt.empty();
      }
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.isRainingAt(var3.above())) {
         if (var4.nextInt(15) == 1) {
            BlockPos var5 = var3.below();
            BlockState var6 = var2.getBlockState(var5);
            if (!var6.canOcclude() || !var6.isFaceSturdy(var2, var5, Direction.UP)) {
               ParticleUtils.spawnParticleBelow(var2, var3, var4, ParticleTypes.DRIPPING_WATER);
            }
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, PERSISTENT, WATERLOGGED);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockState var3 = this.defaultBlockState()
         .setValue(PERSISTENT, Boolean.valueOf(true))
         .setValue(WATERLOGGED, Boolean.valueOf(var2.getType() == Fluids.WATER));
      return updateDistance(var3, var1.getLevel(), var1.getClickedPos());
   }
}
