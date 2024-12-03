package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseCoralPlantTypeBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape AABB;

   protected BaseCoralPlantTypeBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
   }

   protected abstract MapCodec<? extends BaseCoralPlantTypeBlock> codec();

   protected void tryScheduleDieTick(BlockState var1, BlockGetter var2, ScheduledTickAccess var3, RandomSource var4, BlockPos var5) {
      if (!scanForWater(var1, var2, var5)) {
         var3.scheduleTick(var5, (Block)this, 60 + var4.nextInt(40));
      }

   }

   protected static boolean scanForWater(BlockState var0, BlockGetter var1, BlockPos var2) {
      if ((Boolean)var0.getValue(WATERLOGGED)) {
         return true;
      } else {
         for(Direction var6 : Direction.values()) {
            if (var1.getFluidState(var2.relative(var6)).is(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var2.is(FluidTags.WATER) && var2.getAmount() == 8);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABB;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5 == Direction.DOWN && !this.canSurvive(var1, var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return var2.getBlockState(var4).isFaceSturdy(var2, var4, Direction.UP);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      AABB = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
   }
}
