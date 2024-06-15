package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {
   static Aquifer create(NoiseChunk var0, ChunkPos var1, NoiseRouter var2, PositionalRandomFactory var3, int var4, int var5, Aquifer.FluidPicker var6) {
      return new Aquifer.NoiseBasedAquifer(var0, var1, var2, var3, var4, var5, var6);
   }

   static Aquifer createDisabled(final Aquifer.FluidPicker var0) {
      return new Aquifer() {
         @Nullable
         @Override
         public BlockState computeSubstance(DensityFunction.FunctionContext var1, double var2) {
            return var2 > 0.0 ? null : var0.computeFluid(var1.blockX(), var1.blockY(), var1.blockZ()).at(var1.blockY());
         }

         @Override
         public boolean shouldScheduleFluidUpdate() {
            return false;
         }
      };
   }

   @Nullable
   BlockState computeSubstance(DensityFunction.FunctionContext var1, double var2);

   boolean shouldScheduleFluidUpdate();

   public interface FluidPicker {
      Aquifer.FluidStatus computeFluid(int var1, int var2, int var3);
   }

   public static final class FluidStatus {
      final int fluidLevel;
      final BlockState fluidType;

      public FluidStatus(int var1, BlockState var2) {
         super();
         this.fluidLevel = var1;
         this.fluidType = var2;
      }

      public BlockState at(int var1) {
         return var1 < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
      }
   }

   public static class NoiseBasedAquifer implements Aquifer {
      private static final int X_RANGE = 10;
      private static final int Y_RANGE = 9;
      private static final int Z_RANGE = 10;
      private static final int X_SEPARATION = 6;
      private static final int Y_SEPARATION = 3;
      private static final int Z_SEPARATION = 6;
      private static final int X_SPACING = 16;
      private static final int Y_SPACING = 12;
      private static final int Z_SPACING = 16;
      private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
      private static final double FLOWING_UPDATE_SIMULARITY = similarity(Mth.square(10), Mth.square(12));
      private final NoiseChunk noiseChunk;
      private final DensityFunction barrierNoise;
      private final DensityFunction fluidLevelFloodednessNoise;
      private final DensityFunction fluidLevelSpreadNoise;
      private final DensityFunction lavaNoise;
      private final PositionalRandomFactory positionalRandomFactory;
      private final Aquifer.FluidStatus[] aquiferCache;
      private final long[] aquiferLocationCache;
      private final Aquifer.FluidPicker globalFluidPicker;
      private final DensityFunction erosion;
      private final DensityFunction depth;
      private boolean shouldScheduleFluidUpdate;
      private final int minGridX;
      private final int minGridY;
      private final int minGridZ;
      private final int gridSizeX;
      private final int gridSizeZ;
      private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{
         {0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
      };

      NoiseBasedAquifer(NoiseChunk var1, ChunkPos var2, NoiseRouter var3, PositionalRandomFactory var4, int var5, int var6, Aquifer.FluidPicker var7) {
         super();
         this.noiseChunk = var1;
         this.barrierNoise = var3.barrierNoise();
         this.fluidLevelFloodednessNoise = var3.fluidLevelFloodednessNoise();
         this.fluidLevelSpreadNoise = var3.fluidLevelSpreadNoise();
         this.lavaNoise = var3.lavaNoise();
         this.erosion = var3.erosion();
         this.depth = var3.depth();
         this.positionalRandomFactory = var4;
         this.minGridX = this.gridX(var2.getMinBlockX()) - 1;
         this.globalFluidPicker = var7;
         int var8 = this.gridX(var2.getMaxBlockX()) + 1;
         this.gridSizeX = var8 - this.minGridX + 1;
         this.minGridY = this.gridY(var5) - 1;
         int var9 = this.gridY(var5 + var6) + 1;
         int var10 = var9 - this.minGridY + 1;
         this.minGridZ = this.gridZ(var2.getMinBlockZ()) - 1;
         int var11 = this.gridZ(var2.getMaxBlockZ()) + 1;
         this.gridSizeZ = var11 - this.minGridZ + 1;
         int var12 = this.gridSizeX * var10 * this.gridSizeZ;
         this.aquiferCache = new Aquifer.FluidStatus[var12];
         this.aquiferLocationCache = new long[var12];
         Arrays.fill(this.aquiferLocationCache, 9223372036854775807L);
      }

      private int getIndex(int var1, int var2, int var3) {
         int var4 = var1 - this.minGridX;
         int var5 = var2 - this.minGridY;
         int var6 = var3 - this.minGridZ;
         return (var5 * this.gridSizeZ + var6) * this.gridSizeX + var4;
      }

      @Nullable
      @Override
      public BlockState computeSubstance(DensityFunction.FunctionContext var1, double var2) {
         int var4 = var1.blockX();
         int var5 = var1.blockY();
         int var6 = var1.blockZ();
         if (var2 > 0.0) {
            this.shouldScheduleFluidUpdate = false;
            return null;
         } else {
            Aquifer.FluidStatus var7 = this.globalFluidPicker.computeFluid(var4, var5, var6);
            if (var7.at(var5).is(Blocks.LAVA)) {
               this.shouldScheduleFluidUpdate = false;
               return Blocks.LAVA.defaultBlockState();
            } else {
               int var8 = Math.floorDiv(var4 - 5, 16);
               int var9 = Math.floorDiv(var5 + 1, 12);
               int var10 = Math.floorDiv(var6 - 5, 16);
               int var11 = 2147483647;
               int var12 = 2147483647;
               int var13 = 2147483647;
               long var14 = 0L;
               long var16 = 0L;
               long var18 = 0L;

               for (int var20 = 0; var20 <= 1; var20++) {
                  for (int var21 = -1; var21 <= 1; var21++) {
                     for (int var22 = 0; var22 <= 1; var22++) {
                        int var23 = var8 + var20;
                        int var24 = var9 + var21;
                        int var25 = var10 + var22;
                        int var26 = this.getIndex(var23, var24, var25);
                        long var29 = this.aquiferLocationCache[var26];
                        long var27;
                        if (var29 != 9223372036854775807L) {
                           var27 = var29;
                        } else {
                           RandomSource var31 = this.positionalRandomFactory.at(var23, var24, var25);
                           var27 = BlockPos.asLong(var23 * 16 + var31.nextInt(10), var24 * 12 + var31.nextInt(9), var25 * 16 + var31.nextInt(10));
                           this.aquiferLocationCache[var26] = var27;
                        }

                        int var43 = BlockPos.getX(var27) - var4;
                        int var32 = BlockPos.getY(var27) - var5;
                        int var33 = BlockPos.getZ(var27) - var6;
                        int var34 = var43 * var43 + var32 * var32 + var33 * var33;
                        if (var11 >= var34) {
                           var18 = var16;
                           var16 = var14;
                           var14 = var27;
                           var13 = var12;
                           var12 = var11;
                           var11 = var34;
                        } else if (var12 >= var34) {
                           var18 = var16;
                           var16 = var27;
                           var13 = var12;
                           var12 = var34;
                        } else if (var13 >= var34) {
                           var18 = var27;
                           var13 = var34;
                        }
                     }
                  }
               }

               Aquifer.FluidStatus var36 = this.getAquiferStatus(var14);
               double var37 = similarity(var11, var12);
               BlockState var38 = var36.at(var5);
               if (var37 <= 0.0) {
                  this.shouldScheduleFluidUpdate = var37 >= FLOWING_UPDATE_SIMULARITY;
                  return var38;
               } else if (var38.is(Blocks.WATER) && this.globalFluidPicker.computeFluid(var4, var5 - 1, var6).at(var5 - 1).is(Blocks.LAVA)) {
                  this.shouldScheduleFluidUpdate = true;
                  return var38;
               } else {
                  MutableDouble var39 = new MutableDouble(0.0 / 0.0);
                  Aquifer.FluidStatus var40 = this.getAquiferStatus(var16);
                  double var41 = var37 * this.calculatePressure(var1, var39, var36, var40);
                  if (var2 + var41 > 0.0) {
                     this.shouldScheduleFluidUpdate = false;
                     return null;
                  } else {
                     Aquifer.FluidStatus var42 = this.getAquiferStatus(var18);
                     double var30 = similarity(var11, var13);
                     if (var30 > 0.0) {
                        double var44 = var37 * var30 * this.calculatePressure(var1, var39, var36, var42);
                        if (var2 + var44 > 0.0) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     double var45 = similarity(var12, var13);
                     if (var45 > 0.0) {
                        double var46 = var37 * var45 * this.calculatePressure(var1, var39, var40, var42);
                        if (var2 + var46 > 0.0) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     this.shouldScheduleFluidUpdate = true;
                     return var38;
                  }
               }
            }
         }
      }

      @Override
      public boolean shouldScheduleFluidUpdate() {
         return this.shouldScheduleFluidUpdate;
      }

      private static double similarity(int var0, int var1) {
         double var2 = 25.0;
         return 1.0 - (double)Math.abs(var1 - var0) / 25.0;
      }

      private double calculatePressure(DensityFunction.FunctionContext var1, MutableDouble var2, Aquifer.FluidStatus var3, Aquifer.FluidStatus var4) {
         int var5 = var1.blockY();
         BlockState var6 = var3.at(var5);
         BlockState var7 = var4.at(var5);
         if ((!var6.is(Blocks.LAVA) || !var7.is(Blocks.WATER)) && (!var6.is(Blocks.WATER) || !var7.is(Blocks.LAVA))) {
            int var8 = Math.abs(var3.fluidLevel - var4.fluidLevel);
            if (var8 == 0) {
               return 0.0;
            } else {
               double var9 = 0.5 * (double)(var3.fluidLevel + var4.fluidLevel);
               double var11 = (double)var5 + 0.5 - var9;
               double var13 = (double)var8 / 2.0;
               double var15 = 0.0;
               double var17 = 2.5;
               double var19 = 1.5;
               double var21 = 3.0;
               double var23 = 10.0;
               double var25 = 3.0;
               double var27 = var13 - Math.abs(var11);
               double var29;
               if (var11 > 0.0) {
                  double var31 = 0.0 + var27;
                  if (var31 > 0.0) {
                     var29 = var31 / 1.5;
                  } else {
                     var29 = var31 / 2.5;
                  }
               } else {
                  double var39 = 3.0 + var27;
                  if (var39 > 0.0) {
                     var29 = var39 / 3.0;
                  } else {
                     var29 = var39 / 10.0;
                  }
               }

               double var40 = 2.0;
               double var33;
               if (!(var29 < -2.0) && !(var29 > 2.0)) {
                  double var35 = var2.getValue();
                  if (Double.isNaN(var35)) {
                     double var37 = this.barrierNoise.compute(var1);
                     var2.setValue(var37);
                     var33 = var37;
                  } else {
                     var33 = var35;
                  }
               } else {
                  var33 = 0.0;
               }

               return 2.0 * (var33 + var29);
            }
         } else {
            return 2.0;
         }
      }

      private int gridX(int var1) {
         return Math.floorDiv(var1, 16);
      }

      private int gridY(int var1) {
         return Math.floorDiv(var1, 12);
      }

      private int gridZ(int var1) {
         return Math.floorDiv(var1, 16);
      }

      private Aquifer.FluidStatus getAquiferStatus(long var1) {
         int var3 = BlockPos.getX(var1);
         int var4 = BlockPos.getY(var1);
         int var5 = BlockPos.getZ(var1);
         int var6 = this.gridX(var3);
         int var7 = this.gridY(var4);
         int var8 = this.gridZ(var5);
         int var9 = this.getIndex(var6, var7, var8);
         Aquifer.FluidStatus var10 = this.aquiferCache[var9];
         if (var10 != null) {
            return var10;
         } else {
            Aquifer.FluidStatus var11 = this.computeFluid(var3, var4, var5);
            this.aquiferCache[var9] = var11;
            return var11;
         }
      }

      private Aquifer.FluidStatus computeFluid(int var1, int var2, int var3) {
         Aquifer.FluidStatus var4 = this.globalFluidPicker.computeFluid(var1, var2, var3);
         int var5 = 2147483647;
         int var6 = var2 + 12;
         int var7 = var2 - 12;
         boolean var8 = false;

         for (int[] var12 : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
            int var13 = var1 + SectionPos.sectionToBlockCoord(var12[0]);
            int var14 = var3 + SectionPos.sectionToBlockCoord(var12[1]);
            int var15 = this.noiseChunk.preliminarySurfaceLevel(var13, var14);
            int var16 = var15 + 8;
            boolean var17 = var12[0] == 0 && var12[1] == 0;
            if (var17 && var7 > var16) {
               return var4;
            }

            boolean var18 = var6 > var16;
            if (var18 || var17) {
               Aquifer.FluidStatus var19 = this.globalFluidPicker.computeFluid(var13, var16, var14);
               if (!var19.at(var16).isAir()) {
                  if (var17) {
                     var8 = true;
                  }

                  if (var18) {
                     return var19;
                  }
               }
            }

            var5 = Math.min(var5, var15);
         }

         int var20 = this.computeSurfaceLevel(var1, var2, var3, var4, var5, var8);
         return new Aquifer.FluidStatus(var20, this.computeFluidType(var1, var2, var3, var4, var20));
      }

      private int computeSurfaceLevel(int var1, int var2, int var3, Aquifer.FluidStatus var4, int var5, boolean var6) {
         DensityFunction.SinglePointContext var7 = new DensityFunction.SinglePointContext(var1, var2, var3);
         double var8;
         double var10;
         if (OverworldBiomeBuilder.isDeepDarkRegion(this.erosion, this.depth, var7)) {
            var8 = -1.0;
            var10 = -1.0;
         } else {
            int var12 = var5 + 8 - var2;
            byte var13 = 64;
            double var14 = var6 ? Mth.clampedMap((double)var12, 0.0, 64.0, 1.0, 0.0) : 0.0;
            double var16 = Mth.clamp(this.fluidLevelFloodednessNoise.compute(var7), -1.0, 1.0);
            double var18 = Mth.map(var14, 1.0, 0.0, -0.3, 0.8);
            double var20 = Mth.map(var14, 1.0, 0.0, -0.8, 0.4);
            var8 = var16 - var20;
            var10 = var16 - var18;
         }

         int var22;
         if (var10 > 0.0) {
            var22 = var4.fluidLevel;
         } else if (var8 > 0.0) {
            var22 = this.computeRandomizedFluidSurfaceLevel(var1, var2, var3, var5);
         } else {
            var22 = DimensionType.WAY_BELOW_MIN_Y;
         }

         return var22;
      }

      private int computeRandomizedFluidSurfaceLevel(int var1, int var2, int var3, int var4) {
         byte var5 = 16;
         byte var6 = 40;
         int var7 = Math.floorDiv(var1, 16);
         int var8 = Math.floorDiv(var2, 40);
         int var9 = Math.floorDiv(var3, 16);
         int var10 = var8 * 40 + 20;
         byte var11 = 10;
         double var12 = this.fluidLevelSpreadNoise.compute(new DensityFunction.SinglePointContext(var7, var8, var9)) * 10.0;
         int var14 = Mth.quantize(var12, 3);
         int var15 = var10 + var14;
         return Math.min(var4, var15);
      }

      private BlockState computeFluidType(int var1, int var2, int var3, Aquifer.FluidStatus var4, int var5) {
         BlockState var6 = var4.fluidType;
         if (var5 <= -10 && var5 != DimensionType.WAY_BELOW_MIN_Y && var4.fluidType != Blocks.LAVA.defaultBlockState()) {
            byte var7 = 64;
            byte var8 = 40;
            int var9 = Math.floorDiv(var1, 64);
            int var10 = Math.floorDiv(var2, 40);
            int var11 = Math.floorDiv(var3, 64);
            double var12 = this.lavaNoise.compute(new DensityFunction.SinglePointContext(var9, var10, var11));
            if (Math.abs(var12) > 0.3) {
               var6 = Blocks.LAVA.defaultBlockState();
            }
         }

         return var6;
      }
   }
}
