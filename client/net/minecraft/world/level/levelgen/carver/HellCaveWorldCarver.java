package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class HellCaveWorldCarver extends CaveWorldCarver {
   public HellCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> var1) {
      super(var1, 128);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK});
      this.liquids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
   }

   protected int getCaveBound() {
      return 10;
   }

   protected float getThickness(Random var1) {
      return (var1.nextFloat() * 2.0F + var1.nextFloat()) * 2.0F;
   }

   protected double getYScale() {
      return 5.0D;
   }

   protected int getCaveY(Random var1) {
      return var1.nextInt(this.genHeight);
   }

   protected boolean carveBlock(ChunkAccess var1, BitSet var2, Random var3, BlockPos.MutableBlockPos var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, AtomicBoolean var15) {
      int var16 = var12 | var14 << 4 | var13 << 8;
      if (var2.get(var16)) {
         return false;
      } else {
         var2.set(var16);
         var4.set(var10, var13, var11);
         if (this.canReplaceBlock(var1.getBlockState(var4))) {
            BlockState var17;
            if (var13 <= 31) {
               var17 = LAVA.createLegacyBlock();
            } else {
               var17 = CAVE_AIR;
            }

            var1.setBlockState(var4, var17, false);
            return true;
         } else {
            return false;
         }
      }
   }
}
