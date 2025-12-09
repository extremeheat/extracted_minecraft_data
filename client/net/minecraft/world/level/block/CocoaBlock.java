package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CocoaBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
   public static final MapCodec<CocoaBlock> CODEC = simpleCodec(CocoaBlock::new);
   public static final int MAX_AGE = 2;
   public static final IntegerProperty AGE;
   private static final List<Map<Direction, VoxelShape>> SHAPES;

   public MapCodec<CocoaBlock> codec() {
      return CODEC;
   }

   public CocoaBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AGE, 0));
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(AGE) < 2;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.random.nextInt(5) == 0) {
         int var5 = (Integer)var1.getValue(AGE);
         if (var5 < 2) {
            var2.setBlock(var3, (BlockState)var1.setValue(AGE, var5 + 1), 2);
         }
      }

   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.relative((Direction)var1.getValue(FACING)));
      return var4.is(BlockTags.JUNGLE_LOGS);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)((Map)SHAPES.get((Integer)var1.getValue(AGE))).get(var1.getValue(FACING));
   }

   public @Nullable BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();

      for(Direction var8 : var1.getNearestLookingDirections()) {
         if (var8.getAxis().isHorizontal()) {
            var2 = (BlockState)var2.setValue(FACING, var8);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5 == var1.getValue(FACING) && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return (Integer)var3.getValue(AGE) < 2;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlock(var3, (BlockState)var4.setValue(AGE, (Integer)var4.getValue(AGE) + 1), 2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, AGE);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      AGE = BlockStateProperties.AGE_2;
      SHAPES = IntStream.rangeClosed(0, 2).mapToObj((var0) -> Shapes.rotateHorizontal(Block.column((double)(4 + var0 * 2), (double)(7 - var0 * 2), 12.0).move(0.0, 0.0, (double)(var0 - 5) / 16.0).optimize())).toList();
   }
}
