package net.minecraft.world.level.levelgen;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public abstract class NoiseBasedChunkGenerator extends ChunkGenerator {
   private static final float[] BEARD_KERNEL = (float[])Util.make(new float[13824], (var0) -> {
      for(int var1 = 0; var1 < 24; ++var1) {
         for(int var2 = 0; var2 < 24; ++var2) {
            for(int var3 = 0; var3 < 24; ++var3) {
               var0[var1 * 24 * 24 + var2 * 24 + var3] = (float)computeContribution(var2 - 12, var3 - 12, var1 - 12);
            }
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
   protected final BlockState defaultBlock;
   protected final BlockState defaultFluid;

   public NoiseBasedChunkGenerator(LevelAccessor var1, BiomeSource var2, int var3, int var4, int var5, ChunkGeneratorSettings var6, boolean var7) {
      super(var1, var2, var6);
      this.chunkHeight = var4;
      this.chunkWidth = var3;
      this.defaultBlock = var6.getDefaultBlock();
      this.defaultFluid = var6.getDefaultFluid();
      this.chunkCountX = 16 / this.chunkWidth;
      this.chunkCountY = var5 / this.chunkHeight;
      this.chunkCountZ = 16 / this.chunkWidth;
      this.random = new WorldgenRandom(this.seed);
      this.minLimitPerlinNoise = new PerlinNoise(this.random, 15, 0);
      this.maxLimitPerlinNoise = new PerlinNoise(this.random, 15, 0);
      this.mainPerlinNoise = new PerlinNoise(this.random, 7, 0);
      this.surfaceNoise = (SurfaceNoise)(var7 ? new PerlinSimplexNoise(this.random, 3, 0) : new PerlinNoise(this.random, 3, 0));
   }

   private double sampleAndClampNoise(int var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      double var12 = 0.0D;
      double var14 = 0.0D;
      double var16 = 0.0D;
      double var18 = 1.0D;

      for(int var20 = 0; var20 < 16; ++var20) {
         double var21 = PerlinNoise.wrap((double)var1 * var4 * var18);
         double var23 = PerlinNoise.wrap((double)var2 * var6 * var18);
         double var25 = PerlinNoise.wrap((double)var3 * var4 * var18);
         double var27 = var6 * var18;
         ImprovedNoise var29 = this.minLimitPerlinNoise.getOctaveNoise(var20);
         if (var29 != null) {
            var12 += var29.noise(var21, var23, var25, var27, (double)var2 * var27) / var18;
         }

         ImprovedNoise var30 = this.maxLimitPerlinNoise.getOctaveNoise(var20);
         if (var30 != null) {
            var14 += var30.noise(var21, var23, var25, var27, (double)var2 * var27) / var18;
         }

         if (var20 < 8) {
            ImprovedNoise var31 = this.mainPerlinNoise.getOctaveNoise(var20);
            if (var31 != null) {
               var16 += var31.noise(PerlinNoise.wrap((double)var1 * var8 * var18), PerlinNoise.wrap((double)var2 * var10 * var18), PerlinNoise.wrap((double)var3 * var8 * var18), var10 * var18, (double)var2 * var10 * var18) / var18;
            }
         }

         var18 /= 2.0D;
      }

      return Mth.clampedLerp(var12 / 512.0D, var14 / 512.0D, (var16 / 10.0D + 1.0D) / 2.0D);
   }

   protected double[] makeAndFillNoiseColumn(int var1, int var2) {
      double[] var3 = new double[this.chunkCountY + 1];
      this.fillNoiseColumn(var3, var1, var2);
      return var3;
   }

   protected void fillNoiseColumn(double[] var1, int var2, int var3, double var4, double var6, double var8, double var10, int var12, int var13) {
      double[] var14 = this.getDepthAndScale(var2, var3);
      double var15 = var14[0];
      double var17 = var14[1];
      double var19 = this.getTopSlideStart();
      double var21 = this.getBottomSlideStart();

      for(int var23 = 0; var23 < this.getNoiseSizeY(); ++var23) {
         double var24 = this.sampleAndClampNoise(var2, var23, var3, var4, var6, var8, var10);
         var24 -= this.getYOffset(var15, var17, var23);
         if ((double)var23 > var19) {
            var24 = Mth.clampedLerp(var24, (double)var13, ((double)var23 - var19) / (double)var12);
         } else if ((double)var23 < var21) {
            var24 = Mth.clampedLerp(var24, -30.0D, (var21 - (double)var23) / (var21 - 1.0D));
         }

         var1[var23] = var24;
      }

   }

   protected abstract double[] getDepthAndScale(int var1, int var2);

   protected abstract double getYOffset(double var1, double var3, int var5);

   protected double getTopSlideStart() {
      return (double)(this.getNoiseSizeY() - 4);
   }

   protected double getBottomSlideStart() {
      return 0.0D;
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      int var4 = Math.floorDiv(var1, this.chunkWidth);
      int var5 = Math.floorDiv(var2, this.chunkWidth);
      int var6 = Math.floorMod(var1, this.chunkWidth);
      int var7 = Math.floorMod(var2, this.chunkWidth);
      double var8 = (double)var6 / (double)this.chunkWidth;
      double var10 = (double)var7 / (double)this.chunkWidth;
      double[][] var12 = new double[][]{this.makeAndFillNoiseColumn(var4, var5), this.makeAndFillNoiseColumn(var4, var5 + 1), this.makeAndFillNoiseColumn(var4 + 1, var5), this.makeAndFillNoiseColumn(var4 + 1, var5 + 1)};
      int var13 = this.getSeaLevel();

      for(int var14 = this.chunkCountY - 1; var14 >= 0; --var14) {
         double var15 = var12[0][var14];
         double var17 = var12[1][var14];
         double var19 = var12[2][var14];
         double var21 = var12[3][var14];
         double var23 = var12[0][var14 + 1];
         double var25 = var12[1][var14 + 1];
         double var27 = var12[2][var14 + 1];
         double var29 = var12[3][var14 + 1];

         for(int var31 = this.chunkHeight - 1; var31 >= 0; --var31) {
            double var32 = (double)var31 / (double)this.chunkHeight;
            double var34 = Mth.lerp3(var32, var8, var10, var15, var23, var19, var27, var17, var25, var21, var29);
            int var36 = var14 * this.chunkHeight + var31;
            if (var34 > 0.0D || var36 < var13) {
               BlockState var37;
               if (var34 > 0.0D) {
                  var37 = this.defaultBlock;
               } else {
                  var37 = this.defaultFluid;
               }

               if (var3.isOpaque().test(var37)) {
                  return var36 + 1;
               }
            }
         }
      }

      return 0;
   }

   protected abstract void fillNoiseColumn(double[] var1, int var2, int var3);

   public int getNoiseSizeY() {
      return this.chunkCountY + 1;
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
            var1.getBiome(var12.set(var8 + var13, var17, var9 + var14)).buildSurfaceAt(var6, var2, var15, var16, var17, var18, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.level.getSeed());
         }
      }

      this.setBedrock(var2, var6);
   }

   protected void setBedrock(ChunkAccess var1, Random var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = var1.getPos().getMinBlockX();
      int var5 = var1.getPos().getMinBlockZ();
      ChunkGeneratorSettings var6 = this.getSettings();
      int var7 = var6.getBedrockFloorPosition();
      int var8 = var6.getBedrockRoofPosition();
      Iterator var9 = BlockPos.betweenClosed(var4, 0, var5, var4 + 15, 0, var5 + 15).iterator();

      while(true) {
         BlockPos var10;
         int var11;
         do {
            if (!var9.hasNext()) {
               return;
            }

            var10 = (BlockPos)var9.next();
            if (var8 > 0) {
               for(var11 = var8; var11 >= var8 - 4; --var11) {
                  if (var11 >= var8 - var2.nextInt(5)) {
                     var1.setBlockState(var3.set(var10.getX(), var11, var10.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                  }
               }
            }
         } while(var7 >= 256);

         for(var11 = var7 + 4; var11 >= var7; --var11) {
            if (var11 <= var7 + var2.nextInt(5)) {
               var1.setBlockState(var3.set(var10.getX(), var11, var10.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
            }
         }
      }
   }

   public void fillFromNoise(LevelAccessor var1, ChunkAccess var2) {
      int var3 = this.getSeaLevel();
      ObjectArrayList var4 = new ObjectArrayList(10);
      ObjectArrayList var5 = new ObjectArrayList(32);
      ChunkPos var6 = var2.getPos();
      int var7 = var6.x;
      int var8 = var6.z;
      int var9 = var7 << 4;
      int var10 = var8 << 4;
      Iterator var11 = Feature.NOISE_AFFECTING_FEATURES.iterator();

      label178:
      while(var11.hasNext()) {
         StructureFeature var12 = (StructureFeature)var11.next();
         String var13 = var12.getFeatureName();
         LongIterator var14 = var2.getReferencesForFeature(var13).iterator();

         label176:
         while(true) {
            StructureStart var19;
            do {
               do {
                  if (!var14.hasNext()) {
                     continue label178;
                  }

                  long var15 = var14.nextLong();
                  ChunkPos var17 = new ChunkPos(var15);
                  ChunkAccess var18 = var1.getChunk(var17.x, var17.z);
                  var19 = var18.getStartForFeature(var13);
               } while(var19 == null);
            } while(!var19.isValid());

            Iterator var20 = var19.getPieces().iterator();

            while(true) {
               StructurePiece var21;
               do {
                  do {
                     if (!var20.hasNext()) {
                        continue label176;
                     }

                     var21 = (StructurePiece)var20.next();
                  } while(!var21.isCloseToChunk(var6, 12));
               } while(!(var21 instanceof PoolElementStructurePiece));

               PoolElementStructurePiece var22 = (PoolElementStructurePiece)var21;
               StructureTemplatePool.Projection var23 = var22.getElement().getProjection();
               if (var23 == StructureTemplatePool.Projection.RIGID) {
                  var4.add(var22);
               }

               Iterator var24 = var22.getJunctions().iterator();

               while(var24.hasNext()) {
                  JigsawJunction var25 = (JigsawJunction)var24.next();
                  int var26 = var25.getSourceX();
                  int var27 = var25.getSourceZ();
                  if (var26 > var9 - 12 && var27 > var10 - 12 && var26 < var9 + 15 + 12 && var27 < var10 + 15 + 12) {
                     var5.add(var25);
                  }
               }
            }
         }
      }

      double[][][] var75 = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];

      for(int var76 = 0; var76 < this.chunkCountZ + 1; ++var76) {
         var75[0][var76] = new double[this.chunkCountY + 1];
         this.fillNoiseColumn(var75[0][var76], var7 * this.chunkCountX, var8 * this.chunkCountZ + var76);
         var75[1][var76] = new double[this.chunkCountY + 1];
      }

      ProtoChunk var77 = (ProtoChunk)var2;
      Heightmap var78 = var77.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var79 = var77.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      BlockPos.MutableBlockPos var80 = new BlockPos.MutableBlockPos();
      ObjectListIterator var16 = var4.iterator();
      ObjectListIterator var81 = var5.iterator();

      for(int var82 = 0; var82 < this.chunkCountX; ++var82) {
         int var83;
         for(var83 = 0; var83 < this.chunkCountZ + 1; ++var83) {
            this.fillNoiseColumn(var75[1][var83], var7 * this.chunkCountX + var82 + 1, var8 * this.chunkCountZ + var83);
         }

         for(var83 = 0; var83 < this.chunkCountZ; ++var83) {
            LevelChunkSection var84 = var77.getOrCreateSection(15);
            var84.acquire();

            for(int var86 = this.chunkCountY - 1; var86 >= 0; --var86) {
               double var87 = var75[0][var83][var86];
               double var88 = var75[0][var83 + 1][var86];
               double var89 = var75[1][var83][var86];
               double var28 = var75[1][var83 + 1][var86];
               double var30 = var75[0][var83][var86 + 1];
               double var32 = var75[0][var83 + 1][var86 + 1];
               double var34 = var75[1][var83][var86 + 1];
               double var36 = var75[1][var83 + 1][var86 + 1];

               for(int var38 = this.chunkHeight - 1; var38 >= 0; --var38) {
                  int var39 = var86 * this.chunkHeight + var38;
                  int var40 = var39 & 15;
                  int var41 = var39 >> 4;
                  if (var84.bottomBlockY() >> 4 != var41) {
                     var84.release();
                     var84 = var77.getOrCreateSection(var41);
                     var84.acquire();
                  }

                  double var42 = (double)var38 / (double)this.chunkHeight;
                  double var44 = Mth.lerp(var42, var87, var30);
                  double var46 = Mth.lerp(var42, var89, var34);
                  double var48 = Mth.lerp(var42, var88, var32);
                  double var50 = Mth.lerp(var42, var28, var36);

                  for(int var52 = 0; var52 < this.chunkWidth; ++var52) {
                     int var53 = var9 + var82 * this.chunkWidth + var52;
                     int var54 = var53 & 15;
                     double var55 = (double)var52 / (double)this.chunkWidth;
                     double var57 = Mth.lerp(var55, var44, var46);
                     double var59 = Mth.lerp(var55, var48, var50);

                     for(int var61 = 0; var61 < this.chunkWidth; ++var61) {
                        int var62 = var10 + var83 * this.chunkWidth + var61;
                        int var63 = var62 & 15;
                        double var64 = (double)var61 / (double)this.chunkWidth;
                        double var66 = Mth.lerp(var64, var57, var59);
                        double var68 = Mth.clamp(var66 / 200.0D, -1.0D, 1.0D);

                        int var72;
                        int var73;
                        int var74;
                        for(var68 = var68 / 2.0D - var68 * var68 * var68 / 24.0D; var16.hasNext(); var68 += getContribution(var72, var73, var74) * 0.8D) {
                           PoolElementStructurePiece var70 = (PoolElementStructurePiece)var16.next();
                           BoundingBox var71 = var70.getBoundingBox();
                           var72 = Math.max(0, Math.max(var71.x0 - var53, var53 - var71.x1));
                           var73 = var39 - (var71.y0 + var70.getGroundLevelDelta());
                           var74 = Math.max(0, Math.max(var71.z0 - var62, var62 - var71.z1));
                        }

                        var16.back(var4.size());

                        while(var81.hasNext()) {
                           JigsawJunction var90 = (JigsawJunction)var81.next();
                           int var92 = var53 - var90.getSourceX();
                           var72 = var39 - var90.getSourceGroundY();
                           var73 = var62 - var90.getSourceZ();
                           var68 += getContribution(var92, var72, var73) * 0.4D;
                        }

                        var81.back(var5.size());
                        BlockState var91;
                        if (var68 > 0.0D) {
                           var91 = this.defaultBlock;
                        } else if (var39 < var3) {
                           var91 = this.defaultFluid;
                        } else {
                           var91 = AIR;
                        }

                        if (var91 != AIR) {
                           if (var91.getLightEmission() != 0) {
                              var80.set(var53, var39, var62);
                              var77.addLight(var80);
                           }

                           var84.setBlockState(var54, var40, var63, var91, false);
                           var78.update(var54, var39, var63, var91);
                           var79.update(var54, var39, var63, var91);
                        }
                     }
                  }
               }
            }

            var84.release();
         }

         double[][] var85 = var75[0];
         var75[0] = var75[1];
         var75[1] = var85;
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

   static {
      AIR = Blocks.AIR.defaultBlockState();
   }
}
