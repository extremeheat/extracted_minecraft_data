package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;

public abstract class BiomeSource implements BiomeResolver {
   public static final Codec<BiomeSource> CODEC;
   private final Supplier<Set<Holder<Biome>>> possibleBiomes = Suppliers.memoize(() -> {
      return (Set)this.collectPossibleBiomes().distinct().collect(ImmutableSet.toImmutableSet());
   });

   protected BiomeSource() {
      super();
   }

   protected abstract MapCodec<? extends BiomeSource> codec();

   protected abstract Stream<Holder<Biome>> collectPossibleBiomes();

   public Set<Holder<Biome>> possibleBiomes() {
      return (Set)this.possibleBiomes.get();
   }

   public Set<Holder<Biome>> getBiomesWithin(int var1, int var2, int var3, int var4, Climate.Sampler var5) {
      int var6 = QuartPos.fromBlock(var1 - var4);
      int var7 = QuartPos.fromBlock(var2 - var4);
      int var8 = QuartPos.fromBlock(var3 - var4);
      int var9 = QuartPos.fromBlock(var1 + var4);
      int var10 = QuartPos.fromBlock(var2 + var4);
      int var11 = QuartPos.fromBlock(var3 + var4);
      int var12 = var9 - var6 + 1;
      int var13 = var10 - var7 + 1;
      int var14 = var11 - var8 + 1;
      HashSet var15 = Sets.newHashSet();

      for(int var16 = 0; var16 < var14; ++var16) {
         for(int var17 = 0; var17 < var12; ++var17) {
            for(int var18 = 0; var18 < var13; ++var18) {
               int var19 = var6 + var17;
               int var20 = var7 + var18;
               int var21 = var8 + var16;
               var15.add(this.getNoiseBiome(var19, var20, var21, var5));
            }
         }
      }

      return var15;
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int var1, int var2, int var3, int var4, Predicate<Holder<Biome>> var5, RandomSource var6, Climate.Sampler var7) {
      return this.findBiomeHorizontal(var1, var2, var3, var4, 1, var5, var6, false, var7);
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(BlockPos var1, int var2, int var3, int var4, Predicate<Holder<Biome>> var5, Climate.Sampler var6, LevelReader var7) {
      Set var8 = (Set)this.possibleBiomes().stream().filter(var5).collect(Collectors.toUnmodifiableSet());
      if (var8.isEmpty()) {
         return null;
      } else {
         int var9 = Math.floorDiv(var2, var3);
         int[] var10 = Mth.outFromOrigin(var1.getY(), var7.getMinBuildHeight() + 1, var7.getMaxBuildHeight(), var4).toArray();
         Iterator var11 = BlockPos.spiralAround(BlockPos.ZERO, var9, Direction.EAST, Direction.SOUTH).iterator();

         while(var11.hasNext()) {
            BlockPos.MutableBlockPos var12 = (BlockPos.MutableBlockPos)var11.next();
            int var13 = var1.getX() + var12.getX() * var3;
            int var14 = var1.getZ() + var12.getZ() * var3;
            int var15 = QuartPos.fromBlock(var13);
            int var16 = QuartPos.fromBlock(var14);
            int[] var17 = var10;
            int var18 = var10.length;

            for(int var19 = 0; var19 < var18; ++var19) {
               int var20 = var17[var19];
               int var21 = QuartPos.fromBlock(var20);
               Holder var22 = this.getNoiseBiome(var15, var21, var16, var6);
               if (var8.contains(var22)) {
                  return Pair.of(new BlockPos(var13, var20, var14), var22);
               }
            }
         }

         return null;
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int var1, int var2, int var3, int var4, int var5, Predicate<Holder<Biome>> var6, RandomSource var7, boolean var8, Climate.Sampler var9) {
      int var10 = QuartPos.fromBlock(var1);
      int var11 = QuartPos.fromBlock(var3);
      int var12 = QuartPos.fromBlock(var4);
      int var13 = QuartPos.fromBlock(var2);
      Pair var14 = null;
      int var15 = 0;
      int var16 = var8 ? 0 : var12;

      for(int var17 = var16; var17 <= var12; var17 += var5) {
         for(int var18 = SharedConstants.debugGenerateSquareTerrainWithoutNoise ? 0 : -var17; var18 <= var17; var18 += var5) {
            boolean var19 = Math.abs(var18) == var17;

            for(int var20 = -var17; var20 <= var17; var20 += var5) {
               if (var8) {
                  boolean var21 = Math.abs(var20) == var17;
                  if (!var21 && !var19) {
                     continue;
                  }
               }

               int var25 = var10 + var20;
               int var22 = var11 + var18;
               Holder var23 = this.getNoiseBiome(var25, var13, var22, var9);
               if (var6.test(var23)) {
                  if (var14 == null || var7.nextInt(var15 + 1) == 0) {
                     BlockPos var24 = new BlockPos(QuartPos.toBlock(var25), var2, QuartPos.toBlock(var22));
                     if (var8) {
                        return Pair.of(var24, var23);
                     }

                     var14 = Pair.of(var24, var23);
                  }

                  ++var15;
               }
            }
         }
      }

      return var14;
   }

   public abstract Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4);

   public void addDebugInfo(List<String> var1, BlockPos var2, Climate.Sampler var3) {
   }

   static {
      CODEC = BuiltInRegistries.BIOME_SOURCE.byNameCodec().dispatchStable(BiomeSource::codec, Function.identity());
   }
}
