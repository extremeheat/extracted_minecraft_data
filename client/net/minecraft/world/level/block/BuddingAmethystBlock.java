package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BuddingAmethystBlock extends AmethystBlock {
   public static final MapCodec<BuddingAmethystBlock> CODEC = simpleCodec(BuddingAmethystBlock::new);
   public static final int GROWTH_CHANCE = 5;
   private static final Direction[] DIRECTIONS = Direction.values();

   public MapCodec<BuddingAmethystBlock> codec() {
      return CODEC;
   }

   public BuddingAmethystBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(5) == 0) {
         Direction var5 = DIRECTIONS[var4.nextInt(DIRECTIONS.length)];
         BlockPos var6 = var3.relative(var5);
         BlockState var7 = var2.getBlockState(var6);
         Block var8 = null;
         if (canClusterGrowAtState(var7)) {
            var8 = Blocks.SMALL_AMETHYST_BUD;
         } else if (var7.is(Blocks.SMALL_AMETHYST_BUD) && var7.getValue(AmethystClusterBlock.FACING) == var5) {
            var8 = Blocks.MEDIUM_AMETHYST_BUD;
         } else if (var7.is(Blocks.MEDIUM_AMETHYST_BUD) && var7.getValue(AmethystClusterBlock.FACING) == var5) {
            var8 = Blocks.LARGE_AMETHYST_BUD;
         } else if (var7.is(Blocks.LARGE_AMETHYST_BUD) && var7.getValue(AmethystClusterBlock.FACING) == var5) {
            var8 = Blocks.AMETHYST_CLUSTER;
         }

         if (var8 != null) {
            BlockState var9 = (BlockState)((BlockState)var8.defaultBlockState().setValue(AmethystClusterBlock.FACING, var5)).setValue(AmethystClusterBlock.WATERLOGGED, var7.getFluidState().getType() == Fluids.WATER);
            var2.setBlockAndUpdate(var6, var9);
         }

      }
   }

   public static boolean canClusterGrowAtState(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER) && var0.getFluidState().getAmount() == 8;
   }
}
