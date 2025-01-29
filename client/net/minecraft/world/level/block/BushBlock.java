package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BushBlock extends VegetationBlock implements BonemealableBlock {
   public static final MapCodec<BushBlock> CODEC = simpleCodec(BushBlock::new);
   private static final VoxelShape SHAPE = Block.cube(16.0, 12.0, 16.0);

   public MapCodec<BushBlock> codec() {
      return CODEC;
   }

   protected BushBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return this.findSpreadableNeighbourPos(var1, var3, var4).isPresent();
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.findSpreadableNeighbourPos(var1, var3, var4).ifPresent((var2x) -> var1.setBlockAndUpdate(var2x, this.defaultBlockState()));
   }

   private Optional<BlockPos> findSpreadableNeighbourPos(Level var1, BlockPos var2, BlockState var3) {
      for(Direction var5 : Direction.Plane.HORIZONTAL.shuffledCopy(var1.random)) {
         BlockPos var6 = var2.relative(var5);
         if (var1.isEmptyBlock(var6) && this.defaultBlockState().canSurvive(var1, var6)) {
            return Optional.of(var6);
         }
      }

      return Optional.empty();
   }
}
