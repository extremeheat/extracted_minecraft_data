package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SurfaceSystem {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
   private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
   private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
   private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
   private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
   private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   private static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private final BlockState defaultBlock;
   private final int seaLevel;
   private final BlockState[] clayBands;
   private final NormalNoise clayBandsOffsetNoise;
   private final NormalNoise badlandsPillarNoise;
   private final NormalNoise badlandsPillarRoofNoise;
   private final NormalNoise badlandsSurfaceNoise;
   private final NormalNoise icebergPillarNoise;
   private final NormalNoise icebergPillarRoofNoise;
   private final NormalNoise icebergSurfaceNoise;
   private final PositionalRandomFactory noiseRandom;
   private final NormalNoise surfaceNoise;
   private final NormalNoise surfaceSecondaryNoise;

   public SurfaceSystem(RandomState var1, BlockState var2, int var3, PositionalRandomFactory var4) {
      super();
      this.defaultBlock = var2;
      this.seaLevel = var3;
      this.noiseRandom = var4;
      this.clayBandsOffsetNoise = var1.getOrCreateNoise(Noises.CLAY_BANDS_OFFSET);
      this.clayBands = generateBands(var4.fromHashOf(new ResourceLocation("clay_bands")));
      this.surfaceNoise = var1.getOrCreateNoise(Noises.SURFACE);
      this.surfaceSecondaryNoise = var1.getOrCreateNoise(Noises.SURFACE_SECONDARY);
      this.badlandsPillarNoise = var1.getOrCreateNoise(Noises.BADLANDS_PILLAR);
      this.badlandsPillarRoofNoise = var1.getOrCreateNoise(Noises.BADLANDS_PILLAR_ROOF);
      this.badlandsSurfaceNoise = var1.getOrCreateNoise(Noises.BADLANDS_SURFACE);
      this.icebergPillarNoise = var1.getOrCreateNoise(Noises.ICEBERG_PILLAR);
      this.icebergPillarRoofNoise = var1.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
      this.icebergSurfaceNoise = var1.getOrCreateNoise(Noises.ICEBERG_SURFACE);
   }

   public void buildSurface(
      RandomState var1,
      BiomeManager var2,
      Registry<Biome> var3,
      boolean var4,
      WorldGenerationContext var5,
      final ChunkAccess var6,
      NoiseChunk var7,
      SurfaceRules.RuleSource var8
   ) {
      final BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      final ChunkPos var10 = var6.getPos();
      int var11 = var10.getMinBlockX();
      int var12 = var10.getMinBlockZ();
      BlockColumn var13 = new BlockColumn() {
         @Override
         public BlockState getBlock(int var1) {
            return var6.getBlockState(var9.setY(var1));
         }

         @Override
         public void setBlock(int var1, BlockState var2) {
            LevelHeightAccessor var3 = var6.getHeightAccessorForGeneration();
            if (var1 >= var3.getMinBuildHeight() && var1 < var3.getMaxBuildHeight()) {
               var6.setBlockState(var9.setY(var1), var2, false);
               if (!var2.getFluidState().isEmpty()) {
                  var6.markPosForPostprocessing(var9);
               }
            }
         }

         @Override
         public String toString() {
            return "ChunkBlockColumn " + var10;
         }
      };
      SurfaceRules.Context var14 = new SurfaceRules.Context(this, var1, var6, var7, var2::getBiome, var3, var5);
      SurfaceRules.SurfaceRule var15 = var8.apply(var14);
      BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();

      for(int var17 = 0; var17 < 16; ++var17) {
         for(int var18 = 0; var18 < 16; ++var18) {
            int var19 = var11 + var17;
            int var20 = var12 + var18;
            int var21 = var6.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var17, var18) + 1;
            var9.setX(var19).setZ(var20);
            Holder var22 = var2.getBiome(var16.set(var19, var4 ? 0 : var21, var20));
            if (var22.is(Biomes.ERODED_BADLANDS)) {
               this.erodedBadlandsExtension(var13, var19, var20, var21, var6);
            }

            int var23 = var6.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var17, var18) + 1;
            var14.updateXZ(var19, var20);
            int var24 = 0;
            int var25 = -2147483648;
            int var26 = 2147483647;
            int var27 = var6.getMinBuildHeight();

            for(int var28 = var23; var28 >= var27; --var28) {
               BlockState var29 = var13.getBlock(var28);
               if (var29.isAir()) {
                  var24 = 0;
                  var25 = -2147483648;
               } else if (!var29.getFluidState().isEmpty()) {
                  if (var25 == -2147483648) {
                     var25 = var28 + 1;
                  }
               } else {
                  if (var26 >= var28) {
                     var26 = DimensionType.WAY_BELOW_MIN_Y;

                     for(int var30 = var28 - 1; var30 >= var27 - 1; --var30) {
                        BlockState var31 = var13.getBlock(var30);
                        if (!this.isStone(var31)) {
                           var26 = var30 + 1;
                           break;
                        }
                     }
                  }

                  ++var24;
                  int var32 = var28 - var26 + 1;
                  var14.updateY(var24, var32, var25, var19, var28, var20);
                  if (var29 == this.defaultBlock) {
                     BlockState var33 = var15.tryApply(var19, var28, var20);
                     if (var33 != null) {
                        var13.setBlock(var28, var33);
                     }
                  }
               }
            }

            if (var22.is(Biomes.FROZEN_OCEAN) || var22.is(Biomes.DEEP_FROZEN_OCEAN)) {
               this.frozenOceanExtension(var14.getMinSurfaceLevel(), (Biome)var22.value(), var13, var16, var19, var20, var21);
            }
         }
      }
   }

   protected int getSurfaceDepth(int var1, int var2) {
      double var3 = this.surfaceNoise.getValue((double)var1, 0.0, (double)var2);
      return (int)(var3 * 2.75 + 3.0 + this.noiseRandom.at(var1, 0, var2).nextDouble() * 0.25);
   }

   protected double getSurfaceSecondary(int var1, int var2) {
      return this.surfaceSecondaryNoise.getValue((double)var1, 0.0, (double)var2);
   }

   private boolean isStone(BlockState var1) {
      return !var1.isAir() && var1.getFluidState().isEmpty();
   }

   @Deprecated
   public Optional<BlockState> topMaterial(
      SurfaceRules.RuleSource var1,
      CarvingContext var2,
      Function<BlockPos, Holder<Biome>> var3,
      ChunkAccess var4,
      NoiseChunk var5,
      BlockPos var6,
      boolean var7
   ) {
      SurfaceRules.Context var8 = new SurfaceRules.Context(
         this, var2.randomState(), var4, var5, var3, var2.registryAccess().registryOrThrow(Registries.BIOME), var2
      );
      SurfaceRules.SurfaceRule var9 = var1.apply(var8);
      int var10 = var6.getX();
      int var11 = var6.getY();
      int var12 = var6.getZ();
      var8.updateXZ(var10, var12);
      var8.updateY(1, 1, var7 ? var11 + 1 : -2147483648, var10, var11, var12);
      BlockState var13 = var9.tryApply(var10, var11, var12);
      return Optional.ofNullable(var13);
   }

   private void erodedBadlandsExtension(BlockColumn var1, int var2, int var3, int var4, LevelHeightAccessor var5) {
      double var6 = 0.2;
      double var8 = Math.min(
         Math.abs(this.badlandsSurfaceNoise.getValue((double)var2, 0.0, (double)var3) * 8.25),
         this.badlandsPillarNoise.getValue((double)var2 * 0.2, 0.0, (double)var3 * 0.2) * 15.0
      );
      if (!(var8 <= 0.0)) {
         double var10 = 0.75;
         double var12 = 1.5;
         double var14 = Math.abs(this.badlandsPillarRoofNoise.getValue((double)var2 * 0.75, 0.0, (double)var3 * 0.75) * 1.5);
         double var16 = 64.0 + Math.min(var8 * var8 * 2.5, Math.ceil(var14 * 50.0) + 24.0);
         int var18 = Mth.floor(var16);
         if (var4 <= var18) {
            for(int var19 = var18; var19 >= var5.getMinBuildHeight(); --var19) {
               BlockState var20 = var1.getBlock(var19);
               if (var20.is(this.defaultBlock.getBlock())) {
                  break;
               }

               if (var20.is(Blocks.WATER)) {
                  return;
               }
            }

            for(int var21 = var18; var21 >= var5.getMinBuildHeight() && var1.getBlock(var21).isAir(); --var21) {
               var1.setBlock(var21, this.defaultBlock);
            }
         }
      }
   }

   private void frozenOceanExtension(int var1, Biome var2, BlockColumn var3, BlockPos.MutableBlockPos var4, int var5, int var6, int var7) {
      double var8 = 1.28;
      double var10 = Math.min(
         Math.abs(this.icebergSurfaceNoise.getValue((double)var5, 0.0, (double)var6) * 8.25),
         this.icebergPillarNoise.getValue((double)var5 * 1.28, 0.0, (double)var6 * 1.28) * 15.0
      );
      if (!(var10 <= 1.8)) {
         double var14 = 1.17;
         double var16 = 1.5;
         double var18 = Math.abs(this.icebergPillarRoofNoise.getValue((double)var5 * 1.17, 0.0, (double)var6 * 1.17) * 1.5);
         double var20 = Math.min(var10 * var10 * 1.2, Math.ceil(var18 * 40.0) + 14.0);
         if (var2.shouldMeltFrozenOceanIcebergSlightly(var4.set(var5, 63, var6))) {
            var20 -= 2.0;
         }

         double var12;
         if (var20 > 2.0) {
            var12 = (double)this.seaLevel - var20 - 7.0;
            var20 += (double)this.seaLevel;
         } else {
            var20 = 0.0;
            var12 = 0.0;
         }

         double var22 = var20;
         RandomSource var24 = this.noiseRandom.at(var5, 0, var6);
         int var25 = 2 + var24.nextInt(4);
         int var26 = this.seaLevel + 18 + var24.nextInt(10);
         int var27 = 0;

         for(int var28 = Math.max(var7, (int)var20 + 1); var28 >= var1; --var28) {
            if (var3.getBlock(var28).isAir() && var28 < (int)var22 && var24.nextDouble() > 0.01
               || var3.getBlock(var28).is(Blocks.WATER) && var28 > (int)var12 && var28 < this.seaLevel && var12 != 0.0 && var24.nextDouble() > 0.15) {
               if (var27 <= var25 && var28 > var26) {
                  var3.setBlock(var28, SNOW_BLOCK);
                  ++var27;
               } else {
                  var3.setBlock(var28, PACKED_ICE);
               }
            }
         }
      }
   }

   private static BlockState[] generateBands(RandomSource var0) {
      BlockState[] var1 = new BlockState[192];
      Arrays.fill(var1, TERRACOTTA);

      for(int var5 = 0; var5 < var1.length; ++var5) {
         var5 += var0.nextInt(5) + 1;
         if (var5 < var1.length) {
            var1[var5] = ORANGE_TERRACOTTA;
         }
      }

      makeBands(var0, var1, 1, YELLOW_TERRACOTTA);
      makeBands(var0, var1, 2, BROWN_TERRACOTTA);
      makeBands(var0, var1, 1, RED_TERRACOTTA);
      int var6 = var0.nextIntBetweenInclusive(9, 15);
      int var3 = 0;

      for(int var4 = 0; var3 < var6 && var4 < var1.length; var4 += var0.nextInt(16) + 4) {
         var1[var4] = WHITE_TERRACOTTA;
         if (var4 - 1 > 0 && var0.nextBoolean()) {
            var1[var4 - 1] = LIGHT_GRAY_TERRACOTTA;
         }

         if (var4 + 1 < var1.length && var0.nextBoolean()) {
            var1[var4 + 1] = LIGHT_GRAY_TERRACOTTA;
         }

         ++var3;
      }

      return var1;
   }

   private static void makeBands(RandomSource var0, BlockState[] var1, int var2, BlockState var3) {
      int var4 = var0.nextIntBetweenInclusive(6, 15);

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var2 + var0.nextInt(3);
         int var7 = var0.nextInt(var1.length);

         for(int var8 = 0; var7 + var8 < var1.length && var8 < var6; ++var8) {
            var1[var7 + var8] = var3;
         }
      }
   }

   protected BlockState getBand(int var1, int var2, int var3) {
      int var4 = (int)Math.round(this.clayBandsOffsetNoise.getValue((double)var1, 0.0, (double)var3) * 4.0);
      return this.clayBands[(var2 + var4 + this.clayBands.length) % this.clayBands.length];
   }
}
