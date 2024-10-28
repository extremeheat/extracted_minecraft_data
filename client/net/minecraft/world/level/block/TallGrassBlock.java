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

public class TallGrassBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<TallGrassBlock> CODEC = simpleCodec(TallGrassBlock::new);
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

   public MapCodec<TallGrassBlock> codec() {
      return CODEC;
   }

   protected TallGrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return getGrownBlock(var3).defaultBlockState().canSurvive(var1, var2) && var1.isEmptyBlock(var2.above());
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      DoublePlantBlock.placeAt(var1, getGrownBlock(var4).defaultBlockState(), var3, 2);
   }

   private static DoublePlantBlock getGrownBlock(BlockState var0) {
      return (DoublePlantBlock)(var0.is(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
   }
}
