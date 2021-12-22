package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4D;
   private final Supplier<ConfiguredFeature<HugeFungusConfiguration, ?>> feature;

   protected FungusBlock(BlockBehaviour.Properties var1, Supplier<ConfiguredFeature<HugeFungusConfiguration, ?>> var2) {
      super(var1);
      this.feature = var2;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.NYLIUM) || var1.is(Blocks.MYCELIUM) || var1.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(var1, var2, var3);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      Block var5 = ((HugeFungusConfiguration)((ConfiguredFeature)this.feature.get()).config).validBaseState.getBlock();
      BlockState var6 = var1.getBlockState(var2.below());
      return var6.is(var5);
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return (double)var2.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      ((ConfiguredFeature)this.feature.get()).place(var1, var1.getChunkSource().getGenerator(), var2, var3);
   }
}
