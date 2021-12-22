package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {
   static Aquifer create(NoiseChunk var0, ChunkPos var1, NormalNoise var2, NormalNoise var3, NormalNoise var4, NormalNoise var5, PositionalRandomFactory var6, int var7, int var8, Aquifer.FluidPicker var9) {
      return new Aquifer.NoiseBasedAquifer(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   static Aquifer createDisabled(final Aquifer.FluidPicker var0) {
      return new Aquifer() {
         @Nullable
         public BlockState computeSubstance(int var1, int var2, int var3, double var4, double var6) {
            return var6 > 0.0D ? null : var0.computeFluid(var1, var2, var3).method_22(var2);
         }

         public boolean shouldScheduleFluidUpdate() {
            return false;
         }
      };
   }

   @Nullable
   BlockState computeSubstance(int var1, int var2, int var3, double var4, double var6);

   boolean shouldScheduleFluidUpdate();

   public static class NoiseBasedAquifer implements Aquifer, Aquifer.FluidPicker {
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
      private final NormalNoise barrierNoise;
      private final NormalNoise fluidLevelFloodednessNoise;
      private final NormalNoise fluidLevelSpreadNoise;
      private final NormalNoise lavaNoise;
      private final PositionalRandomFactory positionalRandomFactory;
      private final Aquifer.FluidStatus[] aquiferCache;
      private final long[] aquiferLocationCache;
      private final Aquifer.FluidPicker globalFluidPicker;
      private boolean shouldScheduleFluidUpdate;
      private final int minGridX;
      private final int minGridY;
      private final int minGridZ;
      private final int gridSizeX;
      private final int gridSizeZ;
      private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

      NoiseBasedAquifer(NoiseChunk var1, ChunkPos var2, NormalNoise var3, NormalNoise var4, NormalNoise var5, NormalNoise var6, PositionalRandomFactory var7, int var8, int var9, Aquifer.FluidPicker var10) {
         super();
         this.noiseChunk = var1;
         this.barrierNoise = var3;
         this.fluidLevelFloodednessNoise = var4;
         this.fluidLevelSpreadNoise = var5;
         this.lavaNoise = var6;
         this.positionalRandomFactory = var7;
         this.minGridX = this.gridX(var2.getMinBlockX()) - 1;
         this.globalFluidPicker = var10;
         int var11 = this.gridX(var2.getMaxBlockX()) + 1;
         this.gridSizeX = var11 - this.minGridX + 1;
         this.minGridY = this.gridY(var8) - 1;
         int var12 = this.gridY(var8 + var9) + 1;
         int var13 = var12 - this.minGridY + 1;
         this.minGridZ = this.gridZ(var2.getMinBlockZ()) - 1;
         int var14 = this.gridZ(var2.getMaxBlockZ()) + 1;
         this.gridSizeZ = var14 - this.minGridZ + 1;
         int var15 = this.gridSizeX * var13 * this.gridSizeZ;
         this.aquiferCache = new Aquifer.FluidStatus[var15];
         this.aquiferLocationCache = new long[var15];
         Arrays.fill(this.aquiferLocationCache, 9223372036854775807L);
      }

      private int getIndex(int var1, int var2, int var3) {
         int var4 = var1 - this.minGridX;
         int var5 = var2 - this.minGridY;
         int var6 = var3 - this.minGridZ;
         return (var5 * this.gridSizeZ + var6) * this.gridSizeX + var4;
      }

      @Nullable
      public BlockState computeSubstance(int var1, int var2, int var3, double var4, double var6) {
         if (var4 <= -64.0D) {
            return this.globalFluidPicker.computeFluid(var1, var2, var3).method_22(var2);
         } else {
            if (var6 <= 0.0D) {
               Aquifer.FluidStatus var12 = this.globalFluidPicker.computeFluid(var1, var2, var3);
               double var8;
               BlockState var10;
               boolean var11;
               if (var12.method_22(var2).is(Blocks.LAVA)) {
                  var10 = Blocks.LAVA.defaultBlockState();
                  var8 = 0.0D;
                  var11 = false;
               } else {
                  int var13 = Math.floorDiv(var1 - 5, 16);
                  int var14 = Math.floorDiv(var2 + 1, 12);
                  int var15 = Math.floorDiv(var3 - 5, 16);
                  int var16 = 2147483647;
                  int var17 = 2147483647;
                  int var18 = 2147483647;
                  long var19 = 0L;
                  long var21 = 0L;
                  long var23 = 0L;
                  int var25 = 0;

                  while(true) {
                     if (var25 > 1) {
                        Aquifer.FluidStatus var49 = this.getAquiferStatus(var19);
                        Aquifer.FluidStatus var50 = this.getAquiferStatus(var21);
                        Aquifer.FluidStatus var51 = this.getAquiferStatus(var23);
                        double var52 = similarity(var16, var17);
                        double var53 = similarity(var16, var18);
                        double var54 = similarity(var17, var18);
                        var11 = var52 >= FLOWING_UPDATE_SIMULARITY;
                        if (var49.method_22(var2).is(Blocks.WATER) && this.globalFluidPicker.computeFluid(var1, var2 - 1, var3).method_22(var2 - 1).is(Blocks.LAVA)) {
                           var8 = 1.0D;
                        } else if (var52 > -1.0D) {
                           MutableDouble var55 = new MutableDouble(0.0D / 0.0);
                           double var35 = this.calculatePressure(var1, var2, var3, var55, var49, var50);
                           double var57 = this.calculatePressure(var1, var2, var3, var55, var49, var51);
                           double var58 = this.calculatePressure(var1, var2, var3, var55, var50, var51);
                           double var41 = Math.max(0.0D, var52);
                           double var43 = Math.max(0.0D, var53);
                           double var45 = Math.max(0.0D, var54);
                           double var47 = 2.0D * var41 * Math.max(var35, Math.max(var57 * var43, var58 * var45));
                           var8 = Math.max(0.0D, var47);
                        } else {
                           var8 = 0.0D;
                        }

                        var10 = var49.method_22(var2);
                        break;
                     }

                     for(int var26 = -1; var26 <= 1; ++var26) {
                        for(int var27 = 0; var27 <= 1; ++var27) {
                           int var28 = var13 + var25;
                           int var29 = var14 + var26;
                           int var30 = var15 + var27;
                           int var31 = this.getIndex(var28, var29, var30);
                           long var34 = this.aquiferLocationCache[var31];
                           long var32;
                           if (var34 != 9223372036854775807L) {
                              var32 = var34;
                           } else {
                              RandomSource var36 = this.positionalRandomFactory.method_6(var28, var29, var30);
                              var32 = BlockPos.asLong(var28 * 16 + var36.nextInt(10), var29 * 12 + var36.nextInt(9), var30 * 16 + var36.nextInt(10));
                              this.aquiferLocationCache[var31] = var32;
                           }

                           int var56 = BlockPos.getX(var32) - var1;
                           int var37 = BlockPos.getY(var32) - var2;
                           int var38 = BlockPos.getZ(var32) - var3;
                           int var39 = var56 * var56 + var37 * var37 + var38 * var38;
                           if (var16 >= var39) {
                              var23 = var21;
                              var21 = var19;
                              var19 = var32;
                              var18 = var17;
                              var17 = var16;
                              var16 = var39;
                           } else if (var17 >= var39) {
                              var23 = var21;
                              var21 = var32;
                              var18 = var17;
                              var17 = var39;
                           } else if (var18 >= var39) {
                              var23 = var32;
                              var18 = var39;
                           }
                        }
                     }

                     ++var25;
                  }
               }

               if (var6 + var8 <= 0.0D) {
                  this.shouldScheduleFluidUpdate = var11;
                  return var10;
               }
            }

            this.shouldScheduleFluidUpdate = false;
            return null;
         }
      }

      public boolean shouldScheduleFluidUpdate() {
         return this.shouldScheduleFluidUpdate;
      }

      private static double similarity(int var0, int var1) {
         double var2 = 25.0D;
         return 1.0D - (double)Math.abs(var1 - var0) / 25.0D;
      }

      private double calculatePressure(int var1, int var2, int var3, MutableDouble var4, Aquifer.FluidStatus var5, Aquifer.FluidStatus var6) {
         BlockState var7 = var5.method_22(var2);
         BlockState var8 = var6.method_22(var2);
         if (var7.is(Blocks.LAVA) && var8.is(Blocks.WATER) || var7.is(Blocks.WATER) && var8.is(Blocks.LAVA)) {
            return 1.0D;
         } else {
            int var9 = Math.abs(var5.fluidLevel - var6.fluidLevel);
            if (var9 == 0) {
               return 0.0D;
            } else {
               double var10 = 0.5D * (double)(var5.fluidLevel + var6.fluidLevel);
               double var12 = (double)var2 + 0.5D - var10;
               double var14 = (double)var9 / 2.0D;
               double var16 = 0.0D;
               double var18 = 2.5D;
               double var20 = 1.5D;
               double var22 = 3.0D;
               double var24 = 10.0D;
               double var26 = 3.0D;
               double var28 = var14 - Math.abs(var12);
               double var32;
               double var30;
               if (var12 > 0.0D) {
                  var32 = 0.0D + var28;
                  if (var32 > 0.0D) {
                     var30 = var32 / 1.5D;
                  } else {
                     var30 = var32 / 2.5D;
                  }
               } else {
                  var32 = 3.0D + var28;
                  if (var32 > 0.0D) {
                     var30 = var32 / 3.0D;
                  } else {
                     var30 = var32 / 10.0D;
                  }
               }

               if (!(var30 < -2.0D) && !(var30 > 2.0D)) {
                  var32 = var4.getValue();
                  if (Double.isNaN(var32)) {
                     double var34 = 0.5D;
                     double var36 = this.barrierNoise.getValue((double)var1, (double)var2 * 0.5D, (double)var3);
                     var4.setValue(var36);
                     return var36 + var30;
                  } else {
                     return var32 + var30;
                  }
               } else {
                  return var30;
               }
            }
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

      public Aquifer.FluidStatus computeFluid(int var1, int var2, int var3) {
         Aquifer.FluidStatus var4 = this.globalFluidPicker.computeFluid(var1, var2, var3);
         int var5 = 2147483647;
         int var6 = var2 + 12;
         int var7 = var2 - 12;
         boolean var8 = false;
         int[][] var9 = SURFACE_SAMPLING_OFFSETS_IN_CHUNKS;
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            int[] var12 = var9[var11];
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
               if (!var19.method_22(var16).isAir()) {
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

         int var34 = var5 + 8 - var2;
         boolean var35 = true;
         double var36 = var8 ? Mth.clampedMap((double)var34, 0.0D, 64.0D, 1.0D, 0.0D) : 0.0D;
         double var37 = 0.67D;
         double var38 = Mth.clamp(this.fluidLevelFloodednessNoise.getValue((double)var1, (double)var2 * 0.67D, (double)var3), -1.0D, 1.0D);
         double var39 = Mth.map(var36, 1.0D, 0.0D, -0.3D, 0.8D);
         if (var38 > var39) {
            return var4;
         } else {
            double var40 = Mth.map(var36, 1.0D, 0.0D, -0.8D, 0.4D);
            if (var38 <= var40) {
               return new Aquifer.FluidStatus(DimensionType.WAY_BELOW_MIN_Y, var4.fluidType);
            } else {
               boolean var21 = true;
               boolean var22 = true;
               int var23 = Math.floorDiv(var1, 16);
               int var24 = Math.floorDiv(var2, 40);
               int var25 = Math.floorDiv(var3, 16);
               int var26 = var24 * 40 + 20;
               boolean var27 = true;
               double var28 = this.fluidLevelSpreadNoise.getValue((double)var23, (double)var24 / 1.4D, (double)var25) * 10.0D;
               int var30 = Mth.quantize(var28, 3);
               int var31 = var26 + var30;
               int var32 = Math.min(var5, var31);
               BlockState var33 = this.getFluidType(var1, var2, var3, var4, var31);
               return new Aquifer.FluidStatus(var32, var33);
            }
         }
      }

      private BlockState getFluidType(int var1, int var2, int var3, Aquifer.FluidStatus var4, int var5) {
         if (var5 <= -10) {
            boolean var6 = true;
            boolean var7 = true;
            int var8 = Math.floorDiv(var1, 64);
            int var9 = Math.floorDiv(var2, 40);
            int var10 = Math.floorDiv(var3, 64);
            double var11 = this.lavaNoise.getValue((double)var8, (double)var9, (double)var10);
            if (Math.abs(var11) > 0.3D) {
               return Blocks.LAVA.defaultBlockState();
            }
         }

         return var4.fluidType;
      }
   }

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

      // $FF: renamed from: at (int) net.minecraft.world.level.block.state.BlockState
      public BlockState method_22(int var1) {
         return var1 < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
      }
   }
}
