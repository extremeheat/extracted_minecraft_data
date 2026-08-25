package net.minecraft.world.level.levelgen.material;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;
import net.minecraft.world.level.levelgen.synth.Noise;
import org.jspecify.annotations.Nullable;

public class MaterialSystem {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;
   private static final BlockState YELLOW_TERRACOTTA;
   private static final BlockState BROWN_TERRACOTTA;
   private static final BlockState RED_TERRACOTTA;
   private static final BlockState LIGHT_GRAY_TERRACOTTA;
   private static final BlockState PACKED_ICE;
   private static final BlockState SNOW_BLOCK;
   private final BlockState defaultBlock;
   private final int seaLevel;
   private final DensityFunction preliminarySurfaceFunction;
   private final BlockState[] clayBands;
   private final Noise clayBandsOffsetNoise;
   private final Noise badlandsPillarNoise;
   private final Noise badlandsPillarRoofNoise;
   private final Noise badlandsSurfaceNoise;
   private final Noise icebergPillarNoise;
   private final Noise icebergPillarRoofNoise;
   private final Noise icebergSurfaceNoise;
   private final PositionalRandomFactory noiseRandom;
   private final Noise surfaceNoise;
   private final Noise surfaceSecondaryNoise;

   public MaterialSystem(final RandomState randomState, final BlockState defaultBlock, final int seaLevel, final DensityFunction preliminarySurfaceFunction, final PositionalRandomFactory noiseRandom) {
      super();
      this.defaultBlock = defaultBlock;
      this.seaLevel = seaLevel;
      this.preliminarySurfaceFunction = preliminarySurfaceFunction;
      this.noiseRandom = noiseRandom;
      this.clayBandsOffsetNoise = randomState.getOrCreateNoise(Noises.CLAY_BANDS_OFFSET);
      this.clayBands = generateBands(noiseRandom.fromHashOf(Identifier.withDefaultNamespace("clay_bands")));
      this.surfaceNoise = randomState.getOrCreateNoise(Noises.SURFACE);
      this.surfaceSecondaryNoise = randomState.getOrCreateNoise(Noises.SURFACE_SECONDARY);
      this.badlandsPillarNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR);
      this.badlandsPillarRoofNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR_ROOF);
      this.badlandsSurfaceNoise = randomState.getOrCreateNoise(Noises.BADLANDS_SURFACE);
      this.icebergPillarNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR);
      this.icebergPillarRoofNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
      this.icebergSurfaceNoise = randomState.getOrCreateNoise(Noises.ICEBERG_SURFACE);
   }

   public void buildSurface(final RandomState randomState, final BiomeManager biomeManager, final WorldGenerationContext generationContext, final ChunkAccess protoChunk, final NoiseChunk noiseChunk, final MaterialRule ruleSource, final @Nullable Set<Holder<Biome>> possibleBiomes) {
      final BlockPos.MutableBlockPos columnPos = new BlockPos.MutableBlockPos();
      final ChunkPos chunkPos = protoChunk.getPos();
      int minBlockX = chunkPos.getMinBlockX();
      int minBlockZ = chunkPos.getMinBlockZ();
      LevelHeightAccessor heightAccessor = protoChunk.getHeightAccessorForGeneration();
      final int minY = heightAccessor.getMinY();
      final int maxY = heightAccessor.getMaxY();
      BlockColumn column = new BlockColumn() {
         {
            Objects.requireNonNull(MaterialSystem.this);
         }

         public BlockState getBlock(final int blockY) {
            return protoChunk.getBlockState(columnPos.setY(blockY));
         }

         public void setBlock(final int blockY, final BlockState state) {
            if (blockY >= minY && blockY <= maxY) {
               protoChunk.setBlockState(columnPos.setY(blockY), state);
               if (!state.getFluidState().isEmpty()) {
                  protoChunk.markPosForPostProcessing(columnPos);
               }
            }

         }

         public String toString() {
            return "ChunkBlockColumn " + String.valueOf(chunkPos);
         }
      };
      int highestFilledSectionIndex = protoChunk.getHighestFilledSectionIndex();
      int maxBlockY = protoChunk.getSectionYFromSectionIndex(highestFilledSectionIndex) * 16 + 15;
      DensityVolume fullVolume = noiseChunk.volume();
      DensityVolume narrowedVolume = new DensityVolume(fullVolume.sizeX(), Math.max(maxBlockY - fullVolume.minBlockY() + 1, 1), fullVolume.sizeZ(), fullVolume.minBlockX(), fullVolume.minBlockY(), fullVolume.minBlockZ());
      DensitySamplerSet var10005 = noiseChunk.cachingSamplers();
      Objects.requireNonNull(biomeManager);
      MaterialRuleContext context = new MaterialRuleContext(this, randomState, narrowedVolume, var10005, biomeManager::getBiome, generationContext, possibleBiomes);
      RuleEvaluator rule = ruleSource.compile(context);
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int blockX = minBlockX + x;
            int blockZ = minBlockZ + z;
            int startingHeight = protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
            columnPos.setX(blockX).setZ(blockZ);
            Holder<Biome> surfaceBiome = biomeManager.getBiome(blockPos.set(blockX, startingHeight, blockZ));
            if (surfaceBiome.is(Biomes.ERODED_BADLANDS)) {
               this.erodedBadlandsExtension(column, blockX, blockZ, startingHeight, protoChunk);
            }

            int height = protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
            int gradientX = getSurfaceGradientX(protoChunk, x, z);
            int gradientZ = getSurfaceGradientZ(protoChunk, x, z);
            context.updateXZ(blockX, blockZ, gradientX, gradientZ);
            int stoneAboveDepth = 0;
            int waterHeight = -2147483648;
            int nextCeilingStoneY = 2147483647;
            int endY = protoChunk.getMinY();

            for(int y = height; y >= endY; --y) {
               BlockState old = column.getBlock(y);
               if (old.isAir()) {
                  stoneAboveDepth = 0;
                  waterHeight = -2147483648;
               } else if (!old.getFluidState().isEmpty()) {
                  if (waterHeight == -2147483648) {
                     waterHeight = y + 1;
                  }
               } else {
                  if (nextCeilingStoneY >= y) {
                     nextCeilingStoneY = DimensionType.WAY_BELOW_MIN_Y;

                     for(int lookaheadY = y - 1; lookaheadY >= endY - 1; --lookaheadY) {
                        BlockState nextState = column.getBlock(lookaheadY);
                        if (!this.isStone(nextState)) {
                           nextCeilingStoneY = lookaheadY + 1;
                           break;
                        }
                     }
                  }

                  ++stoneAboveDepth;
                  int stoneBelowDepth = y - nextCeilingStoneY + 1;
                  context.updateY(stoneAboveDepth, stoneBelowDepth, waterHeight, y);
                  if (y >= minY && y <= maxY) {
                     BlockState state = rule.tryApply(blockX, y, blockZ);
                     if (state != null) {
                        column.setBlock(y, state);
                     }
                  }
               }
            }

            if (surfaceBiome.is(Biomes.FROZEN_OCEAN) || surfaceBiome.is(Biomes.DEEP_FROZEN_OCEAN)) {
               this.frozenOceanExtension(context.getMinSurfaceLevel(), surfaceBiome.value(), column, blockPos, blockX, blockZ, startingHeight);
            }
         }
      }

   }

   private static int getSurfaceGradientX(final ChunkAccess protoChunk, final int x, final int z) {
      return protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, Math.min(x + 1, 15), z) - protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, Math.max(x - 1, 0), z);
   }

   private static int getSurfaceGradientZ(final ChunkAccess protoChunk, final int x, final int z) {
      return protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, Math.min(z + 1, 15)) - protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, Math.max(z - 1, 0));
   }

   protected int getSurfaceDepth(final int blockX, final int blockZ) {
      double noiseValue = (double)this.surfaceNoise.get((double)blockX, 0.0, (double)blockZ);
      return (int)(noiseValue * 2.75 + 3.0 + this.noiseRandom.at(blockX, 0, blockZ).nextDouble() * 0.25);
   }

   protected double getSurfaceSecondary(final int blockX, final int blockZ) {
      return (double)this.surfaceSecondaryNoise.get((double)blockX, 0.0, (double)blockZ);
   }

   private boolean isStone(final BlockState state) {
      return !state.isAir() && state.getFluidState().isEmpty();
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(final MaterialRule ruleSource, final RandomState randomState, final WorldGenerationContext worldGenerationContext, final Function<BlockPos, Holder<Biome>> biomeGetter, final ChunkAccess chunk, final DensitySamplerSet densitySamplers, final BlockPos pos, final boolean underFluid) {
      DensityVolume volume = new DensityVolume(1, 1, 1, pos.getX(), pos.getY(), pos.getZ());
      MaterialRuleContext context = new MaterialRuleContext(this, randomState, volume, densitySamplers, biomeGetter, worldGenerationContext, (Set)null);
      RuleEvaluator rule = ruleSource.compile(context);
      int blockX = pos.getX();
      int blockY = pos.getY();
      int blockZ = pos.getZ();
      int gradientX = getSurfaceGradientX(chunk, SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getZ()));
      int gradientZ = getSurfaceGradientZ(chunk, SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getZ()));
      context.updateXZ(blockX, blockZ, gradientX, gradientZ);
      context.updateY(1, 1, underFluid ? blockY + 1 : -2147483648, blockY);
      BlockState state = rule.tryApply(blockX, blockY, blockZ);
      return Optional.ofNullable(state);
   }

   private void erodedBadlandsExtension(final BlockColumn column, final int blockX, final int blockZ, final int height, final LevelHeightAccessor protoChunk) {
      double pillarNoiseScale = 0.2;
      double pillarBuffer = Math.min(Math.abs((double)this.badlandsSurfaceNoise.get((double)blockX, 0.0, (double)blockZ) * 8.25), (double)(this.badlandsPillarNoise.get((double)blockX * 0.2, 0.0, (double)blockZ * 0.2) * 15.0F));
      if (!(pillarBuffer <= 0.0)) {
         double floorNoiseSampleResolution = 0.75;
         double floorAmplitude = 1.5;
         double pillarFloor = Math.abs((double)this.badlandsPillarRoofNoise.get((double)blockX * 0.75, 0.0, (double)blockZ * 0.75) * 1.5);
         double extensionTop = 64.0 + Math.min(pillarBuffer * pillarBuffer * 2.5, Math.ceil(pillarFloor * 50.0) + 24.0);
         int startY = Mth.floor(extensionTop);
         if (height <= startY) {
            for(int y = startY; y >= protoChunk.getMinY(); --y) {
               BlockState oldState = column.getBlock(y);
               if (oldState.is(this.defaultBlock.getBlock())) {
                  break;
               }

               if (oldState.is(Blocks.WATER)) {
                  return;
               }
            }

            for(int y = startY; y >= protoChunk.getMinY() && column.getBlock(y).isAir(); --y) {
               column.setBlock(y, this.defaultBlock);
            }

         }
      }
   }

   private void frozenOceanExtension(final int minSurfaceLevel, final Biome surfaceBiome, final BlockColumn column, final BlockPos.MutableBlockPos blockPos, final int blockX, final int blockZ, final int height) {
      double pillarScale = 1.28;
      double iceberg = Math.min(Math.abs((double)this.icebergSurfaceNoise.get((double)blockX, 0.0, (double)blockZ) * 8.25), (double)(this.icebergPillarNoise.get((double)blockX * 1.28, 0.0, (double)blockZ * 1.28) * 15.0F));
      if (!(iceberg <= 1.8)) {
         double roofScale = 1.17;
         double roofAmplitude = 1.5;
         double icebergRoof = Math.abs((double)this.icebergPillarRoofNoise.get((double)blockX * 1.17, 0.0, (double)blockZ * 1.17) * 1.5);
         double top = Math.min(iceberg * iceberg * 1.2, Math.ceil(icebergRoof * 40.0) + 14.0);
         if (surfaceBiome.shouldMeltFrozenOceanIcebergSlightly(blockPos.set(blockX, this.seaLevel, blockZ), this.seaLevel)) {
            top -= 2.0;
         }

         if (!(top <= 2.0)) {
            double extensionBottom = (double)this.seaLevel - top - 7.0;
            top += (double)this.seaLevel;
            double extensionTop = top;
            RandomSource random = this.noiseRandom.at(blockX, 0, blockZ);
            int maxSnowDepth = 2 + random.nextInt(4);
            int minSnowHeight = this.seaLevel + 18 + random.nextInt(10);
            int snowDepth = 0;

            for(int y = Math.max(height, (int)top + 1); y >= minSurfaceLevel; --y) {
               if (column.getBlock(y).isAir() && y < (int)extensionTop && random.nextDouble() > 0.01 || column.getBlock(y).is(Blocks.WATER) && y > (int)extensionBottom && y < this.seaLevel && random.nextDouble() > 0.15) {
                  if (snowDepth <= maxSnowDepth && y > minSnowHeight) {
                     column.setBlock(y, SNOW_BLOCK);
                     ++snowDepth;
                  } else {
                     column.setBlock(y, PACKED_ICE);
                  }
               }
            }

         }
      }
   }

   private static BlockState[] generateBands(final RandomSource random) {
      BlockState[] clayBands = new BlockState[192];
      Arrays.fill(clayBands, TERRACOTTA);

      for(int i = 0; i < clayBands.length; ++i) {
         i += random.nextInt(5) + 1;
         if (i < clayBands.length) {
            clayBands[i] = ORANGE_TERRACOTTA;
         }
      }

      makeBands(random, clayBands, 1, YELLOW_TERRACOTTA);
      makeBands(random, clayBands, 2, BROWN_TERRACOTTA);
      makeBands(random, clayBands, 1, RED_TERRACOTTA);
      int whiteBandCount = random.nextIntBetweenInclusive(9, 15);
      int i = 0;

      for(int start = 0; i < whiteBandCount && start < clayBands.length; start += random.nextInt(16) + 4) {
         clayBands[start] = WHITE_TERRACOTTA;
         if (start - 1 > 0 && random.nextBoolean()) {
            clayBands[start - 1] = LIGHT_GRAY_TERRACOTTA;
         }

         if (start + 1 < clayBands.length && random.nextBoolean()) {
            clayBands[start + 1] = LIGHT_GRAY_TERRACOTTA;
         }

         ++i;
      }

      return clayBands;
   }

   private static void makeBands(final RandomSource random, final BlockState[] clayBands, final int baseWidth, final BlockState state) {
      int bandCount = random.nextIntBetweenInclusive(6, 15);

      for(int i = 0; i < bandCount; ++i) {
         int width = baseWidth + random.nextInt(3);
         int start = random.nextInt(clayBands.length);

         for(int p = 0; start + p < clayBands.length && p < width; ++p) {
            clayBands[start + p] = state;
         }
      }

   }

   protected BlockState getBand(final int worldX, final int y, final int worldZ) {
      int offset = Math.round(this.clayBandsOffsetNoise.get((double)worldX, 0.0, (double)worldZ) * 4.0F);
      return this.clayBands[(y + offset + this.clayBands.length) % this.clayBands.length];
   }

   public DensityFunction preliminarySurfaceFunction() {
      return this.preliminarySurfaceFunction;
   }

   static {
      WHITE_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.white()).defaultBlockState();
      ORANGE_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.orange()).defaultBlockState();
      TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
      YELLOW_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.yellow()).defaultBlockState();
      BROWN_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.brown()).defaultBlockState();
      RED_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.red()).defaultBlockState();
      LIGHT_GRAY_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.lightGray()).defaultBlockState();
      PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
      SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   }
}
