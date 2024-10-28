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
   public static final DirectionProperty FACING;
   public static final IntegerProperty AMOUNT;
   private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES;

   public MapCodec<PinkPetalsBlock> codec() {
      return CODEC;
   }

   protected PinkPetalsBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AMOUNT, 1));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().is(this.asItem()) && (Integer)var1.getValue(AMOUNT) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPE_BY_PROPERTIES.apply((Direction)var1.getValue(FACING), (Integer)var1.getValue(AMOUNT));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      return var2.is(this) ? (BlockState)var2.setValue(AMOUNT, Math.min(4, (Integer)var2.getValue(AMOUNT) + 1)) : (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, AMOUNT);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = (Integer)var4.getValue(AMOUNT);
      if (var5 < 4) {
         var1.setBlock(var3, (BlockState)var4.setValue(AMOUNT, var5 + 1), 2);
      } else {
         popResource(var1, var3, new ItemStack(this));
      }

   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
      SHAPE_BY_PROPERTIES = Util.memoize((var0, var1) -> {
         VoxelShape[] var2 = new VoxelShape[]{Block.box(8.0, 0.0, 8.0, 16.0, 3.0, 16.0), Block.box(8.0, 0.0, 0.0, 16.0, 3.0, 8.0), Block.box(0.0, 0.0, 0.0, 8.0, 3.0, 8.0), Block.box(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)};
         VoxelShape var3 = Shapes.empty();

         for(int var4 = 0; var4 < var1; ++var4) {
            int var5 = Math.floorMod(var4 - var0.get2DDataValue(), 4);
            var3 = Shapes.or(var3, var2[var5]);
         }

         return var3.singleEncompassing();
      });
   }
}
