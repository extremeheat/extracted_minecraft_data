package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherWorldCarver extends CaveWorldCarver {
   public NetherWorldCarver(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1, 128);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
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

   protected boolean carveBlock(ChunkAccess var1, Function<BlockPos, Biome> var2, BitSet var3, Random var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, BlockPos.MutableBlockPos var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, MutableBoolean var16) {
      int var17 = var13 | var15 << 4 | var14 << 8;
      if (var3.get(var17)) {
         return false;
      } else {
         var3.set(var17);
         var5.set(var11, var14, var12);
         if (this.canReplaceBlock(var1.getBlockState(var5))) {
            BlockState var18;
            if (var14 <= 31) {
               var18 = LAVA.createLegacyBlock();
            } else {
               var18 = CAVE_AIR;
            }

            var1.setBlockState(var5, var18, false);
            return true;
         } else {
            return false;
         }
      }
   }
}
