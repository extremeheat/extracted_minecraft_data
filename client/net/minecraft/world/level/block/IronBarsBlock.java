package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronBarsBlock extends CrossCollisionBlock {
   public static final MapCodec<IronBarsBlock> CODEC = simpleCodec(IronBarsBlock::new);

   @Override
   public MapCodec<? extends IronBarsBlock> codec() {
      return CODEC;
   }

   protected IronBarsBlock(BlockBehaviour.Properties var1) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.south();
      BlockPos var7 = var3.west();
      BlockPos var8 = var3.east();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      return this.defaultBlockState()
         .setValue(NORTH, Boolean.valueOf(this.attachsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH))))
         .setValue(SOUTH, Boolean.valueOf(this.attachsTo(var10, var10.isFaceSturdy(var2, var6, Direction.NORTH))))
         .setValue(WEST, Boolean.valueOf(this.attachsTo(var11, var11.isFaceSturdy(var2, var7, Direction.EAST))))
         .setValue(EAST, Boolean.valueOf(this.attachsTo(var12, var12.isFaceSturdy(var2, var8, Direction.WEST))))
         .setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5.getAxis().isHorizontal()
         ? var1.setValue(PROPERTY_BY_DIRECTION.get(var5), Boolean.valueOf(this.attachsTo(var7, var7.isFaceSturdy(var2, var6, var5.getOpposite()))))
         : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   @Override
   protected boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      if (var2.is(this)) {
         if (!var3.getAxis().isHorizontal()) {
            return true;
         }

         if (var1.getValue(PROPERTY_BY_DIRECTION.get(var3)) && var2.getValue(PROPERTY_BY_DIRECTION.get(var3.getOpposite()))) {
            return true;
         }
      }

      return super.skipRendering(var1, var2, var3);
   }

   public final boolean attachsTo(BlockState var1, boolean var2) {
      return !isExceptionForConnection(var1) && var2 || var1.getBlock() instanceof IronBarsBlock || var1.is(BlockTags.WALLS);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
