package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public final class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((var0x) -> {
         return var0x.biomeSource;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((var0x) -> {
         return var0x.seed;
      }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((var0x) -> {
         return var0x.settings;
      })).apply(var0, var0.stable(NoiseBasedChunkGenerator::new));
   });
   private static final float[] BEARD_KERNEL = (float[])Util.make(new float[13824], (var0) -> {
      for(int var1 = 0; var1 < 24; ++var1) {
         for(int var2 = 0; var2 < 24; ++var2) {
            for(int var3 = 0; var3 < 24; ++var3) {
               var0[var1 * 24 * 24 + var2 * 24 + var3] = (float)computeContribution(var2 - 12, var3 - 12, var1 - 12);
            }
         }
      }

   });
   private static final float[] BIOME_WEIGHTS = (float[])Util.make(new float[25], (var0) -> {
      for(int var1 = -2; var1 <= 2; ++var1) {
         for(int var2 = -2; var2 <= 2; ++var2) {
            float var3 = 10.0F / Mth.sqrt((float)(var1 * var1 + var2 * var2) + 0.2F);
            var0[var1 + 2 + (var2 + 2) * 5] = var3;
         }
      }

   });
   private static final BlockState AIR;
   private final int chunkHeight;
   private final int chunkWidth;
   private final int chunkCountX;
   private final int chunkCountY;
   private final int chunkCountZ;
   protected final WorldgenRandom random;
   private final PerlinNoise minLimitPerlinNoise;
   private final PerlinNoise maxLimitPerlinNoise;
   private final PerlinNoise mainPerlinNoise;
   private final SurfaceNoise surfaceNoise;
   private final PerlinNoise depthNoise;
   @Nullable
   private final SimplexNoise islandNoise;
   protected final BlockState defaultBlock;
   protected final BlockState defaultFluid;
   private final long seed;
   protected final Supplier<NoiseGeneratorSettings> settings;
   private final int height;

   public NoiseBasedChunkGenerator(BiomeSource var1, long var2, Supplier<NoiseGeneratorSettings> var4) {
      this(var1, var1, var2, var4);
   }

   private NoiseBasedChunkGenerator(BiomeSource var1, BiomeSource var2, long var3, Supplier<NoiseGeneratorSettings> var5) {
      super(var1, var2, ((NoiseGeneratorSettings)var5.get()).structureSettings(), var3);
      this.seed = var3;
      NoiseGeneratorSettings var6 = (NoiseGeneratorSettings)var5.get();
      this.settings = var5;
      NoiseSettings var7 = var6.noiseSettings();
      this.height = var7.height();
      this.chunkHeight = var7.noiseSizeVertical() * 4;
      this.chunkWidth = var7.noiseSizeHorizontal() * 4;
      this.defaultBlock = var6.getDefaultBlock();
      this.defaultFluid = var6.getDefaultFluid();
      this.chunkCountX = 16 / this.chunkWidth;
      this.chunkCountY = var7.height() / this.chunkHeight;
      this.chunkCountZ = 16 / this.chunkWidth;
      this.random = new WorldgenRandom(var3);
      this.minLimitPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
      this.maxLimitPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
      this.mainPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-7, 0));
      this.surfaceNoise = (SurfaceNoise)(var7.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(this.random, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(this.random, IntStream.rangeClosed(-3, 0)));
      this.random.consumeCount(2620);
      this.depthNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
      if (var7.islandNoiseOverride()) {
         WorldgenRandom var8 = new WorldgenRandom(var3);
         var8.consumeCount(17292);
         this.islandNoise = new SimplexNoise(var8);
      } else {
         this.islandNoise = null;
      }

   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long var1) {
      return new NoiseBasedChunkGenerator(this.biomeSource.withSeed(var1), var1, this.settings);
   }

   public boolean stable(long var1, ResourceKey<NoiseGeneratorSettings> var3) {
      return this.seed == var1 && ((NoiseGeneratorSettings)this.settings.get()).stable(var3);
   }

   private double sampleAndClampNoise(int var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      double var12 = 0.0D;
      double var14 = 0.0D;
      double var16 = 0.0D;
      boolean var18 = true;
      double var19 = 1.0D;

      for(int var21 = 0; var21 < 16; ++var21) {
         double var22 = PerlinNoise.wrap((double)var1 * var4 * var19);
         double var24 = PerlinNoise.wrap((double)var2 * var6 * var19);
         double var26 = PerlinNoise.wrap((double)var3 * var4 * var19);
         double var28 = var6 * var19;
         ImprovedNoise var30 = this.minLimitPerlinNoise.getOctaveNoise(var21);
         if (var30 != null) {
            var12 += var30.noise(var22, var24, var26, var28, (double)var2 * var28) / var19;
         }

         ImprovedNoise var31 = this.maxLimitPerlinNoise.getOctaveNoise(var21);
         if (var31 != null) {
            var14 += var31.noise(var22, var24, var26, var28, (double)var2 * var28) / var19;
         }

         if (var21 < 8) {
            ImprovedNoise var32 = this.mainPerlinNoise.getOctaveNoise(var21);
            if (var32 != null) {
               var16 += var32.noise(PerlinNoise.wrap((double)var1 * var8 * var19), PerlinNoise.wrap((double)var2 * var10 * var19), PerlinNoise.wrap((double)var3 * var8 * var19), var10 * var19, (double)var2 * var10 * var19) / var19;
            }
         }

         var19 /= 2.0D;
      }

      return Mth.clampedLerp(var12 / 512.0D, var14 / 512.0D, (var16 / 10.0D + 1.0D) / 2.0D);
   }

   private double[] makeAndFillNoiseColumn(int var1, int var2) {
      double[] var3 = new double[this.chunkCountY + 1];
      this.fillNoiseColumn(var3, var1, var2);
      return var3;
   }

   private void fillNoiseColumn(double[] var1, int var2, int var3) {
      NoiseSettings var8 = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();
      double var4;
      double var6;
      double var52;
      double var53;
      if (this.islandNoise != null) {
         var4 = (double)(TheEndBiomeSource.getHeightValue(this.islandNoise, var2, var3) - 8.0F);
         if (var4 > 0.0D) {
            var6 = 0.25D;
         } else {
            var6 = 1.0D;
         }
      } else {
         float var9 = 0.0F;
         float var10 = 0.0F;
         float var11 = 0.0F;
         boolean var12 = true;
         int var13 = this.getSeaLevel();
         float var14 = this.biomeSource.getNoiseBiome(var2, var13, var3).getDepth();

         for(int var15 = -2; var15 <= 2; ++var15) {
            for(int var16 = -2; var16 <= 2; ++var16) {
               Biome var17 = this.biomeSource.getNoiseBiome(var2 + var15, var13, var3 + var16);
               float var18 = var17.getDepth();
               float var19 = var17.getScale();
               float var20;
               float var21;
               if (var8.isAmplified() && var18 > 0.0F) {
                  var20 = 1.0F + var18 * 2.0F;
                  var21 = 1.0F + var19 * 4.0F;
               } else {
                  var20 = var18;
                  var21 = var19;
               }

               float var22 = var18 > var14 ? 0.5F : 1.0F;
               float var23 = var22 * BIOME_WEIGHTS[var15 + 2 + (var16 + 2) * 5] / (var20 + 2.0F);
               var9 += var21 * var23;
               var10 += var20 * var23;
               var11 += var23;
            }
         }

         float var49 = var10 / var11;
         float var51 = var9 / var11;
         var52 = (double)(var49 * 0.5F - 0.125F);
         var53 = (double)(var51 * 0.9F + 0.1F);
         var4 = var52 * 0.265625D;
         var6 = 96.0D / var53;
      }

      double var46 = 684.412D * var8.noiseSamplingSettings().xzScale();
      double var47 = 684.412D * var8.noiseSamplingSettings().yScale();
      double var48 = var46 / var8.noiseSamplingSettings().xzFactor();
      double var50 = var47 / var8.noiseSamplingSettings().yFactor();
      var52 = (double)var8.topSlideSettings().target();
      var53 = (double)var8.topSlideSettings().size();
      double var54 = (double)var8.topSlideSettings().offset();
      double var55 = (double)var8.bottomSlideSettings().target();
      double var25 = (double)var8.bottomSlideSettings().size();
      double var27 = (double)var8.bottomSlideSettings().offset();
      double var29 = var8.randomDensityOffset() ? this.getRandomDensity(var2, var3) : 0.0D;
      double var31 = var8.densityFactor();
      double var33 = var8.densityOffset();

      for(int var35 = 0; var35 <= this.chunkCountY; ++var35) {
         double var36 = this.sampleAndClampNoise(var2, var35, var3, var46, var47, var48, var50);
         double var38 = 1.0D - (double)var35 * 2.0D / (double)this.chunkCountY + var29;
         double var40 = var38 * var31 + var33;
         double var42 = (var40 + var4) * var6;
         if (var42 > 0.0D) {
            var36 += var42 * 4.0D;
         } else {
            var36 += var42;
         }

         double var44;
         if (var53 > 0.0D) {
            var44 = ((double)(this.chunkCountY - var35) - var54) / var53;
            var36 = Mth.clampedLerp(var52, var36, var44);
         }

         if (var25 > 0.0D) {
            var44 = ((double)var35 - var27) / var25;
            var36 = Mth.clampedLerp(var55, var36, var44);
         }

         var1[var35] = var36;
      }

   }

   private double getRandomDensity(int var1, int var2) {
      double var3 = this.depthNoise.getValue((double)(var1 * 200), 10.0D, (double)(var2 * 200), 1.0D, 0.0D, true);
      double var5;
      if (var3 < 0.0D) {
         var5 = -var3 * 0.3D;
      } else {
         var5 = var3;
      }

      double var7 = var5 * 24.575625D - 2.0D;
      return var7 < 0.0D ? var7 * 0.009486607142857142D : Math.min(var7, 1.0D) * 0.006640625D;
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      return this.iterateNoiseColumn(var1, var2, (BlockState[])null, var3.isOpaque());
   }

   public BlockGetter getBaseColumn(int var1, int var2) {
      BlockState[] var3 = new BlockState[this.chunkCountY * this.chunkHeight];
      this.iterateNoiseColumn(var1, var2, var3, (Predicate)null);
      return new NoiseColumn(var3);
   }

   private int iterateNoiseColumn(int var1, int var2, @Nullable BlockState[] var3, @Nullable Predicate<BlockState> var4) {
      int var5 = Math.floorDiv(var1, this.chunkWidth);
      int var6 = Math.floorDiv(var2, this.chunkWidth);
      int var7 = Math.floorMod(var1, this.chunkWidth);
      int var8 = Math.floorMod(var2, this.chunkWidth);
      double var9 = (double)var7 / (double)this.chunkWidth;
      double var11 = (double)var8 / (double)this.chunkWidth;
      double[][] var13 = new double[][]{this.makeAndFillNoiseColumn(var5, var6), this.makeAndFillNoiseColumn(var5, var6 + 1), this.makeAndFillNoiseColumn(var5 + 1, var6), this.makeAndFillNoiseColumn(var5 + 1, var6 + 1)};

      for(int var14 = this.chunkCountY - 1; var14 >= 0; --var14) {
         double var15 = var13[0][var14];
         double var17 = var13[1][var14];
         double var19 = var13[2][var14];
         double var21 = var13[3][var14];
         double var23 = var13[0][var14 + 1];
         double var25 = var13[1][var14 + 1];
         double var27 = var13[2][var14 + 1];
         double var29 = var13[3][var14 + 1];

         for(int var31 = this.chunkHeight - 1; var31 >= 0; --var31) {
            double var32 = (double)var31 / (double)this.chunkHeight;
            double var34 = Mth.lerp3(var32, var9, var11, var15, var23, var19, var27, var17, var25, var21, var29);
            int var36 = var14 * this.chunkHeight + var31;
            BlockState var37 = this.generateBaseState(var34, var36);
            if (var3 != null) {
               var3[var36] = var37;
            }

            if (var4 != null && var4.test(var37)) {
               return var36 + 1;
            }
         }
      }

      return 0;
   }

   protected BlockState generateBaseState(double var1, int var3) {
      BlockState var4;
      if (var1 > 0.0D) {
         var4 = this.defaultBlock;
      } else if (var3 < this.getSeaLevel()) {
         var4 = this.defaultFluid;
      } else {
         var4 = AIR;
      }

      return var4;
   }

   public void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2) {
      ChunkPos var3 = var2.getPos();
      int var4 = var3.x;
      int var5 = var3.z;
      WorldgenRandom var6 = new WorldgenRandom();
      var6.setBaseChunkSeed(var4, var5);
      ChunkPos var7 = var2.getPos();
      int var8 = var7.getMinBlockX();
      int var9 = var7.getMinBlockZ();
      double var10 = 0.0625D;
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for(int var13 = 0; var13 < 16; ++var13) {
         for(int var14 = 0; var14 < 16; ++var14) {
            int var15 = var8 + var13;
            int var16 = var9 + var14;
            int var17 = var2.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var13, var14) + 1;
            double var18 = this.surfaceNoise.getSurfaceNoiseValue((double)var15 * 0.0625D, (double)var16 * 0.0625D, 0.0625D, (double)var13 * 0.0625D) * 15.0D;
            var1.getBiome(var12.set(var8 + var13, var17, var9 + var14)).buildSurfaceAt(var6, var2, var15, var16, var17, var18, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), var1.getSeed());
         }
      }

      this.setBedrock(var2, var6);
   }

   private void setBedrock(ChunkAccess var1, Random var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = var1.getPos().getMinBlockX();
      int var5 = var1.getPos().getMinBlockZ();
      NoiseGeneratorSettings var6 = (NoiseGeneratorSettings)this.settings.get();
      int var7 = var6.getBedrockFloorPosition();
      int var8 = this.height - 1 - var6.getBedrockRoofPosition();
      boolean var9 = true;
      boolean var10 = var8 + 4 >= 0 && var8 < this.height;
      boolean var11 = var7 + 4 >= 0 && var7 < this.height;
      if (var10 || var11) {
         Iterator var12 = BlockPos.betweenClosed(var4, 0, var5, var4 + 15, 0, var5 + 15).iterator();

         while(true) {
            BlockPos var13;
            int var14;
            do {
               if (!var12.hasNext()) {
                  return;
               }

               var13 = (BlockPos)var12.next();
               if (var10) {
                  for(var14 = 0; var14 < 5; ++var14) {
                     if (var14 <= var2.nextInt(5)) {
                        var1.setBlockState(var3.set(var13.getX(), var8 - var14, var13.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                     }
                  }
               }
            } while(!var11);

            for(var14 = 4; var14 >= 0; --var14) {
               if (var14 <= var2.nextInt(5)) {
                  var1.setBlockState(var3.set(var13.getX(), var7 + var14, var13.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
               }
            }
         }
      }
   }

   public void fillFromNoise(LevelAccessor var1, StructureFeatureManager var2, ChunkAccess var3) {
      ObjectArrayList var4 = new ObjectArrayList(10);
      ObjectArrayList var5 = new ObjectArrayList(32);
      ChunkPos var6 = var3.getPos();
      int var7 = var6.x;
      int var8 = var6.z;
      int var9 = var7 << 4;
      int var10 = var8 << 4;
      Iterator var11 = StructureFeature.NOISE_AFFECTING_FEATURES.iterator();

      while(var11.hasNext()) {
         StructureFeature var12 = (StructureFeature)var11.next();
         var2.startsForFeature(SectionPos.of(var6, 0), var12).forEach((var5x) -> {
            Iterator var6x = var5x.getPieces().iterator();

            while(true) {
               while(true) {
                  StructurePiece var7;
                  do {
                     if (!var6x.hasNext()) {
                        return;
                     }

                     var7 = (StructurePiece)var6x.next();
                  } while(!var7.isCloseToChunk(var6, 12));

                  if (var7 instanceof PoolElementStructurePiece) {
                     PoolElementStructurePiece var8 = (PoolElementStructurePiece)var7;
                     StructureTemplatePool.Projection var9x = var8.getElement().getProjection();
                     if (var9x == StructureTemplatePool.Projection.RIGID) {
                        var4.add(var8);
                     }

                     Iterator var10x = var8.getJunctions().iterator();

                     while(var10x.hasNext()) {
                        JigsawJunction var11 = (JigsawJunction)var10x.next();
                        int var12 = var11.getSourceX();
                        int var13 = var11.getSourceZ();
                        if (var12 > var9 - 12 && var13 > var10 - 12 && var12 < var9 + 15 + 12 && var13 < var10 + 15 + 12) {
                           var5.add(var11);
                        }
                     }
                  } else {
                     var4.add(var7);
                  }
               }
            }
         });
      }

      double[][][] var75 = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];

      for(int var76 = 0; var76 < this.chunkCountZ + 1; ++var76) {
         var75[0][var76] = new double[this.chunkCountY + 1];
         this.fillNoiseColumn(var75[0][var76], var7 * this.chunkCountX, var8 * this.chunkCountZ + var76);
         var75[1][var76] = new double[this.chunkCountY + 1];
      }

      ProtoChunk var77 = (ProtoChunk)var3;
      Heightmap var13 = var77.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var14 = var77.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();
      ObjectListIterator var16 = var4.iterator();
      ObjectListIterator var17 = var5.iterator();

      for(int var18 = 0; var18 < this.chunkCountX; ++var18) {
         int var19;
         for(var19 = 0; var19 < this.chunkCountZ + 1; ++var19) {
            this.fillNoiseColumn(var75[1][var19], var7 * this.chunkCountX + var18 + 1, var8 * this.chunkCountZ + var19);
         }

         for(var19 = 0; var19 < this.chunkCountZ; ++var19) {
            LevelChunkSection var20 = var77.getOrCreateSection(15);
            var20.acquire();

            for(int var21 = this.chunkCountY - 1; var21 >= 0; --var21) {
               double var22 = var75[0][var19][var21];
               double var24 = var75[0][var19 + 1][var21];
               double var26 = var75[1][var19][var21];
               double var28 = var75[1][var19 + 1][var21];
               double var30 = var75[0][var19][var21 + 1];
               double var32 = var75[0][var19 + 1][var21 + 1];
               double var34 = var75[1][var19][var21 + 1];
               double var36 = var75[1][var19 + 1][var21 + 1];

               for(int var38 = this.chunkHeight - 1; var38 >= 0; --var38) {
                  int var39 = var21 * this.chunkHeight + var38;
                  int var40 = var39 & 15;
                  int var41 = var39 >> 4;
                  if (var20.bottomBlockY() >> 4 != var41) {
                     var20.release();
                     var20 = var77.getOrCreateSection(var41);
                     var20.acquire();
                  }

                  double var42 = (double)var38 / (double)this.chunkHeight;
                  double var44 = Mth.lerp(var42, var22, var30);
                  double var46 = Mth.lerp(var42, var26, var34);
                  double var48 = Mth.lerp(var42, var24, var32);
                  double var50 = Mth.lerp(var42, var28, var36);

                  for(int var52 = 0; var52 < this.chunkWidth; ++var52) {
                     int var53 = var9 + var18 * this.chunkWidth + var52;
                     int var54 = var53 & 15;
                     double var55 = (double)var52 / (double)this.chunkWidth;
                     double var57 = Mth.lerp(var55, var44, var46);
                     double var59 = Mth.lerp(var55, var48, var50);

                     for(int var61 = 0; var61 < this.chunkWidth; ++var61) {
                        int var62 = var10 + var19 * this.chunkWidth + var61;
                        int var63 = var62 & 15;
                        double var64 = (double)var61 / (double)this.chunkWidth;
                        double var66 = Mth.lerp(var64, var57, var59);
                        double var68 = Mth.clamp(var66 / 200.0D, -1.0D, 1.0D);

                        int var72;
                        int var73;
                        int var74;
                        for(var68 = var68 / 2.0D - var68 * var68 * var68 / 24.0D; var16.hasNext(); var68 += getContribution(var72, var73, var74) * 0.8D) {
                           StructurePiece var70 = (StructurePiece)var16.next();
                           BoundingBox var71 = var70.getBoundingBox();
                           var72 = Math.max(0, Math.max(var71.x0 - var53, var53 - var71.x1));
                           var73 = var39 - (var71.y0 + (var70 instanceof PoolElementStructurePiece ? ((PoolElementStructurePiece)var70).getGroundLevelDelta() : 0));
                           var74 = Math.max(0, Math.max(var71.z0 - var62, var62 - var71.z1));
                        }

                        var16.back(var4.size());

                        while(var17.hasNext()) {
                           JigsawJunction var79 = (JigsawJunction)var17.next();
                           int var81 = var53 - var79.getSourceX();
                           var72 = var39 - var79.getSourceGroundY();
                           var73 = var62 - var79.getSourceZ();
                           var68 += getContribution(var81, var72, var73) * 0.4D;
                        }

                        var17.back(var5.size());
                        BlockState var80 = this.generateBaseState(var68, var39);
                        if (var80 != AIR) {
                           if (var80.getLightEmission() != 0) {
                              var15.set(var53, var39, var62);
                              var77.addLight(var15);
                           }

                           var20.setBlockState(var54, var40, var63, var80, false);
                           var13.update(var54, var39, var63, var80);
                           var14.update(var54, var39, var63, var80);
                        }
                     }
                  }
               }
            }

            var20.release();
         }

         double[][] var78 = var75[0];
         var75[0] = var75[1];
         var75[1] = var78;
      }

   }

   private static double getContribution(int var0, int var1, int var2) {
      int var3 = var0 + 12;
      int var4 = var1 + 12;
      int var5 = var2 + 12;
      if (var3 >= 0 && var3 < 24) {
         if (var4 >= 0 && var4 < 24) {
            return var5 >= 0 && var5 < 24 ? (double)BEARD_KERNEL[var5 * 24 * 24 + var3 * 24 + var4] : 0.0D;
         } else {
            return 0.0D;
         }
      } else {
         return 0.0D;
      }
   }

   private static double computeContribution(int var0, int var1, int var2) {
      double var3 = (double)(var0 * var0 + var2 * var2);
      double var5 = (double)var1 + 0.5D;
      double var7 = var5 * var5;
      double var9 = Math.pow(2.718281828459045D, -(var7 / 16.0D + var3 / 16.0D));
      double var11 = -var5 * Mth.fastInvSqrt(var7 / 2.0D + var3 / 2.0D) / 2.0D;
      return var11 * var9;
   }

   public int getGenDepth() {
      return this.height;
   }

   public int getSeaLevel() {
      return ((NoiseGeneratorSettings)this.settings.get()).seaLevel();
   }

   public List<MobSpawnSettings.SpawnerData> getMobsAt(Biome var1, StructureFeatureManager var2, MobCategory var3, BlockPos var4) {
      if (var2.getStructureAt(var4, true, StructureFeature.SWAMP_HUT).isValid()) {
         if (var3 == MobCategory.MONSTER) {
            return StructureFeature.SWAMP_HUT.getSpecialEnemies();
         }

         if (var3 == MobCategory.CREATURE) {
            return StructureFeature.SWAMP_HUT.getSpecialAnimals();
         }
      }

      if (var3 == MobCategory.MONSTER) {
         if (var2.getStructureAt(var4, false, StructureFeature.PILLAGER_OUTPOST).isValid()) {
            return StructureFeature.PILLAGER_OUTPOST.getSpecialEnemies();
         }

         if (var2.getStructureAt(var4, false, StructureFeature.OCEAN_MONUMENT).isValid()) {
            return StructureFeature.OCEAN_MONUMENT.getSpecialEnemies();
         }

         if (var2.getStructureAt(var4, true, StructureFeature.NETHER_BRIDGE).isValid()) {
            return StructureFeature.NETHER_BRIDGE.getSpecialEnemies();
         }
      }

      return super.getMobsAt(var1, var2, var3, var4);
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
      if (!((NoiseGeneratorSettings)this.settings.get()).disableMobGeneration()) {
         int var2 = var1.getCenterX();
         int var3 = var1.getCenterZ();
         Biome var4 = var1.getBiome((new ChunkPos(var2, var3)).getWorldPosition());
         WorldgenRandom var5 = new WorldgenRandom();
         var5.setDecorationSeed(var1.getSeed(), var2 << 4, var3 << 4);
         NaturalSpawner.spawnMobsForChunkGeneration(var1, var4, var2, var3, var5);
      }
   }

   static {
      AIR = Blocks.AIR.defaultBlockState();
   }
}
