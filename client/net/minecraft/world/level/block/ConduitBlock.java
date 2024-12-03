package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConduitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<ConduitBlock> CODEC = simpleCodec(ConduitBlock::new);
   public static final BooleanProperty WATERLOGGED;
   private static final int SIZE = 3;
   protected static final VoxelShape SHAPE;

   public MapCodec<ConduitBlock> codec() {
      return CODEC;
   }

   public ConduitBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ConduitBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.CONDUIT, var1.isClientSide ? ConduitBlockEntity::clientTick : ConduitBlockEntity::serverTick);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var2.is(FluidTags.WATER) && var2.getAmount() == 8);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
   }
}
