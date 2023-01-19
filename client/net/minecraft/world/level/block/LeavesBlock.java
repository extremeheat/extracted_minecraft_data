package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final int TICK_DELAY = 1;

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
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   public boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(DISTANCE) == 7 && !var1.getValue(PERSISTENT);
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.decaying(var1)) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }
   }

   protected boolean decaying(BlockState var1) {
      return !var1.getValue(PERSISTENT) && var1.getValue(DISTANCE) == 7;
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, updateDistance(var1, var2, var3), 3);
   }

   @Override
   public int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      int var7 = getDistanceAt(var3) + 1;
      if (var7 != 1 || var1.getValue(DISTANCE) != var7) {
         var4.scheduleTick(var5, this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(Direction var8 : Direction.values()) {
         var4.setWithOffset(var2, var8);
         var3 = Math.min(var3, getDistanceAt(var1.getBlockState(var4)) + 1);
         if (var3 == 1) {
            break;
         }
      }

      return var0.setValue(DISTANCE, Integer.valueOf(var3));
   }

   private static int getDistanceAt(BlockState var0) {
      if (var0.is(BlockTags.LOGS)) {
         return 0;
      } else {
         return var0.getBlock() instanceof LeavesBlock ? var0.getValue(DISTANCE) : 7;
      }
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.isRainingAt(var3.above())) {
         if (var4.nextInt(15) == 1) {
            BlockPos var5 = var3.below();
            BlockState var6 = var2.getBlockState(var5);
            if (!var6.canOcclude() || !var6.isFaceSturdy(var2, var5, Direction.UP)) {
               double var7 = (double)var3.getX() + var4.nextDouble();
               double var9 = (double)var3.getY() - 0.05;
               double var11 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.DRIPPING_WATER, var7, var9, var11, 0.0, 0.0, 0.0);
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
