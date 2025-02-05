package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallDryGrassBlock extends DryVegetationBlock implements BonemealableBlock {
   public static final MapCodec<TallDryGrassBlock> CODEC = simpleCodec(TallDryGrassBlock::new);
   private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 16.0);

   public MapCodec<TallDryGrassBlock> codec() {
      return CODEC;
   }

   protected TallDryGrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return BonemealableBlock.hasSpreadableNeighbourPos(var1, var2, Blocks.SHORT_DRY_GRASS.defaultBlockState());
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BonemealableBlock.findSpreadableNeighbourPos(var1, var3, Blocks.SHORT_DRY_GRASS.defaultBlockState()).ifPresent((var1x) -> var1.setBlockAndUpdate(var1x, Blocks.SHORT_DRY_GRASS.defaultBlockState()));
   }
}
