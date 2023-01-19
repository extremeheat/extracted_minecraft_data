package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

public class ObserverBlock extends DirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public ObserverBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.valueOf(false)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED);
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(POWERED)) {
         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(false)), 2);
      } else {
         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(true)), 2);
         var2.scheduleTick(var3, this, 2);
      }

      this.updateNeighborsInFront(var2, var3, var1);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(FACING) == var2 && !var1.getValue(POWERED)) {
         this.startSignal(var4, var5);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   private void startSignal(LevelAccessor var1, BlockPos var2) {
      if (!var1.isClientSide() && !var1.getBlockTicks().hasScheduledTick(var2, this)) {
         var1.scheduleTick(var2, this, 2);
      }
   }

   protected void updateNeighborsInFront(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = var3.getValue(FACING);
      BlockPos var5 = var2.relative(var4.getOpposite());
      var1.neighborChanged(var5, this, var2);
      var1.updateNeighborsAtExceptFromFacing(var5, this, var4);
   }

   @Override
   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getSignal(var2, var3, var4);
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWERED) && var1.getValue(FACING) == var4 ? 15 : 0;
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (!var2.isClientSide() && var1.getValue(POWERED) && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            BlockState var6 = var1.setValue(POWERED, Boolean.valueOf(false));
            var2.setBlock(var3, var6, 18);
            this.updateNeighborsInFront(var2, var3, var6);
         }
      }
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (!var2.isClientSide && var1.getValue(POWERED) && var2.getBlockTicks().hasScheduledTick(var3, this)) {
            this.updateNeighborsInFront(var2, var3, var1.setValue(POWERED, Boolean.valueOf(false)));
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite().getOpposite());
   }
}
