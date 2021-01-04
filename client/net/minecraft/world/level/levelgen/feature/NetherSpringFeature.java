package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.material.Fluids;

public class NetherSpringFeature extends Feature<HellSpringConfiguration> {
   private static final BlockState NETHERRACK;

   public NetherSpringFeature(Function<Dynamic<?>, ? extends HellSpringConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, HellSpringConfiguration var5) {
      if (var1.getBlockState(var4.above()) != NETHERRACK) {
         return false;
      } else if (!var1.getBlockState(var4).isAir() && var1.getBlockState(var4) != NETHERRACK) {
         return false;
      } else {
         int var6 = 0;
         if (var1.getBlockState(var4.west()) == NETHERRACK) {
            ++var6;
         }

         if (var1.getBlockState(var4.east()) == NETHERRACK) {
            ++var6;
         }

         if (var1.getBlockState(var4.north()) == NETHERRACK) {
            ++var6;
         }

         if (var1.getBlockState(var4.south()) == NETHERRACK) {
            ++var6;
         }

         if (var1.getBlockState(var4.below()) == NETHERRACK) {
            ++var6;
         }

         int var7 = 0;
         if (var1.isEmptyBlock(var4.west())) {
            ++var7;
         }

         if (var1.isEmptyBlock(var4.east())) {
            ++var7;
         }

         if (var1.isEmptyBlock(var4.north())) {
            ++var7;
         }

         if (var1.isEmptyBlock(var4.south())) {
            ++var7;
         }

         if (var1.isEmptyBlock(var4.below())) {
            ++var7;
         }

         if (!var5.insideRock && var6 == 4 && var7 == 1 || var6 == 5) {
            var1.setBlock(var4, Blocks.LAVA.defaultBlockState(), 2);
            var1.getLiquidTicks().scheduleTick(var4, Fluids.LAVA, 0);
         }

         return true;
      }
   }

   static {
      NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
   }
}
