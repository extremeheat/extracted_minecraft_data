package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AmethystClusterBlock extends AmethystBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   protected final VoxelShape northAabb;
   protected final VoxelShape southAabb;
   protected final VoxelShape eastAabb;
   protected final VoxelShape westAabb;
   protected final VoxelShape upAabb;
   protected final VoxelShape downAabb;

   public AmethystClusterBlock(int var1, int var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, Direction.UP));
      this.upAabb = Block.box((double)var2, 0.0, (double)var2, (double)(16 - var2), (double)var1, (double)(16 - var2));
      this.downAabb = Block.box((double)var2, (double)(16 - var1), (double)var2, (double)(16 - var2), 16.0, (double)(16 - var2));
      this.northAabb = Block.box((double)var2, (double)var2, (double)(16 - var1), (double)(16 - var2), (double)(16 - var2), 16.0);
      this.southAabb = Block.box((double)var2, (double)var2, 0.0, (double)(16 - var2), (double)(16 - var2), (double)var1);
      this.eastAabb = Block.box(0.0, (double)var2, (double)var2, (double)var1, (double)(16 - var2), (double)(16 - var2));
      this.westAabb = Block.box((double)(16 - var1), (double)var2, (double)var2, 16.0, (double)(16 - var2), (double)(16 - var2));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = var1.getValue(FACING);
      switch(var5) {
         case NORTH:
            return this.northAabb;
         case SOUTH:
            return this.southAabb;
         case EAST:
            return this.eastAabb;
         case WEST:
            return this.westAabb;
         case DOWN:
            return this.downAabb;
         case UP:
         default:
            return this.upAabb;
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      return var2.getBlockState(var5).isFaceSturdy(var2, var5, var4);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2 == var1.getValue(FACING).getOpposite() && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return this.defaultBlockState()
         .setValue(WATERLOGGED, Boolean.valueOf(var2.getFluidState(var3).getType() == Fluids.WATER))
         .setValue(FACING, var1.getClickedFace());
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
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED, FACING);
   }
}
