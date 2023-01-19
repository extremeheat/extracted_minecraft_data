package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4;
   private final Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration, ?>>> feature;

   protected FungusBlock(BlockBehaviour.Properties var1, Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration, ?>>> var2) {
      super(var1);
      this.feature = var2;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.NYLIUM) || var1.is(Blocks.MYCELIUM) || var1.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(var1, var2, var3);
   }

   @Override
   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      Block var5 = ((ConfiguredFeature)this.feature.get().value()).config().validBaseState.getBlock();
      BlockState var6 = var1.getBlockState(var2.below());
      return var6.is(var5);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return (double)var2.nextFloat() < 0.4;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.feature.get().value().place(var1, var1.getChunkSource().getGenerator(), var2, var3);
   }
}
