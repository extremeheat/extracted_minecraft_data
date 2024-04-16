package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PinkPetalsBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<PinkPetalsBlock> CODEC = simpleCodec(PinkPetalsBlock::new);
   public static final int MIN_FLOWERS = 1;
   public static final int MAX_FLOWERS = 4;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
   private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES = Util.memoize(
      (var0, var1) -> {
         VoxelShape[] var2 = new VoxelShape[]{
            Block.box(8.0, 0.0, 8.0, 16.0, 3.0, 16.0),
            Block.box(8.0, 0.0, 0.0, 16.0, 3.0, 8.0),
            Block.box(0.0, 0.0, 0.0, 8.0, 3.0, 8.0),
            Block.box(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)
         };
         VoxelShape var3 = Shapes.empty();

         for (int var4 = 0; var4 < var1; var4++) {
            int var5 = Math.floorMod(var4 - var0.get2DDataValue(), 4);
            var3 = Shapes.or(var3, var2[var5]);
         }

         return var3.singleEncompassing();
      }
   );

   @Override
   public MapCodec<PinkPetalsBlock> codec() {
      return CODEC;
   }

   protected PinkPetalsBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, Integer.valueOf(1)));
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
   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().is(this.asItem()) && var1.getValue(AMOUNT) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_PROPERTIES.apply(var1.getValue(FACING), var1.getValue(AMOUNT));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      return var2.is(this)
         ? var2.setValue(AMOUNT, Integer.valueOf(Math.min(4, var2.getValue(AMOUNT) + 1)))
         : this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, AMOUNT);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = var4.getValue(AMOUNT);
      if (var5 < 4) {
         var1.setBlock(var3, var4.setValue(AMOUNT, Integer.valueOf(var5 + 1)), 2);
      } else {
         popResource(var1, var3, new ItemStack(this));
      }
   }
}
