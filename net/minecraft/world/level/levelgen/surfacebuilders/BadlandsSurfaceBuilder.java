package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class BadlandsSurfaceBuilder extends SurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;
   private static final BlockState YELLOW_TERRACOTTA;
   private static final BlockState BROWN_TERRACOTTA;
   private static final BlockState RED_TERRACOTTA;
   private static final BlockState LIGHT_GRAY_TERRACOTTA;
   protected BlockState[] clayBands;
   protected long seed;
   protected PerlinSimplexNoise pillarNoise;
   protected PerlinSimplexNoise pillarRoofNoise;
   protected PerlinSimplexNoise clayBandsOffsetNoise;

   public BadlandsSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var4 & 15;
      int var16 = var5 & 15;
      BlockState var17 = WHITE_TERRACOTTA;
      BlockState var18 = var3.getSurfaceBuilderConfig().getUnderMaterial();
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var20 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var21 = -1;
      boolean var22 = false;
      int var23 = 0;
      BlockPos.MutableBlockPos var24 = new BlockPos.MutableBlockPos();

      for(int var25 = var6; var25 >= 0; --var25) {
         if (var23 < 15) {
            var24.set(var15, var25, var16);
            BlockState var26 = var2.getBlockState(var24);
            if (var26.isAir()) {
               var21 = -1;
            } else if (var26.getBlock() == var9.getBlock()) {
               if (var21 == -1) {
                  var22 = false;
                  if (var19 <= 0) {
                     var17 = Blocks.AIR.defaultBlockState();
                     var18 = var9;
                  } else if (var25 >= var11 - 4 && var25 <= var11 + 1) {
                     var17 = WHITE_TERRACOTTA;
                     var18 = var3.getSurfaceBuilderConfig().getUnderMaterial();
                  }

                  if (var25 < var11 && (var17 == null || var17.isAir())) {
                     var17 = var10;
                  }

                  var21 = var19 + Math.max(0, var25 - var11);
                  if (var25 >= var11 - 1) {
                     if (var25 > var11 + 3 + var19) {
                        BlockState var27;
                        if (var25 >= 64 && var25 <= 127) {
                           if (var20) {
                              var27 = TERRACOTTA;
                           } else {
                              var27 = this.getBand(var4, var25, var5);
                           }
                        } else {
                           var27 = ORANGE_TERRACOTTA;
                        }

                        var2.setBlockState(var24, var27, false);
                     } else {
                        var2.setBlockState(var24, var3.getSurfaceBuilderConfig().getTopMaterial(), false);
                        var22 = true;
                     }
                  } else {
                     var2.setBlockState(var24, var18, false);
                     Block var28 = var18.getBlock();
                     if (var28 == Blocks.WHITE_TERRACOTTA || var28 == Blocks.ORANGE_TERRACOTTA || var28 == Blocks.MAGENTA_TERRACOTTA || var28 == Blocks.LIGHT_BLUE_TERRACOTTA || var28 == Blocks.YELLOW_TERRACOTTA || var28 == Blocks.LIME_TERRACOTTA || var28 == Blocks.PINK_TERRACOTTA || var28 == Blocks.GRAY_TERRACOTTA || var28 == Blocks.LIGHT_GRAY_TERRACOTTA || var28 == Blocks.CYAN_TERRACOTTA || var28 == Blocks.PURPLE_TERRACOTTA || var28 == Blocks.BLUE_TERRACOTTA || var28 == Blocks.BROWN_TERRACOTTA || var28 == Blocks.GREEN_TERRACOTTA || var28 == Blocks.RED_TERRACOTTA || var28 == Blocks.BLACK_TERRACOTTA) {
                        var2.setBlockState(var24, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (var21 > 0) {
                  --var21;
                  if (var22) {
                     var2.setBlockState(var24, ORANGE_TERRACOTTA, false);
                  } else {
                     var2.setBlockState(var24, this.getBand(var4, var25, var5), false);
                  }
               }

               ++var23;
            }
         }
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.clayBands == null) {
         this.generateBands(var1);
      }

      if (this.seed != var1 || this.pillarNoise == null || this.pillarRoofNoise == null) {
         WorldgenRandom var3 = new WorldgenRandom(var1);
         this.pillarNoise = new PerlinSimplexNoise(var3, 3, 0);
         this.pillarRoofNoise = new PerlinSimplexNoise(var3, 0, 0);
      }

      this.seed = var1;
   }

   protected void generateBands(long var1) {
      this.clayBands = new BlockState[64];
      Arrays.fill(this.clayBands, TERRACOTTA);
      WorldgenRandom var3 = new WorldgenRandom(var1);
      this.clayBandsOffsetNoise = new PerlinSimplexNoise(var3, 0, 0);

      int var4;
      for(var4 = 0; var4 < 64; ++var4) {
         var4 += var3.nextInt(5) + 1;
         if (var4 < 64) {
            this.clayBands[var4] = ORANGE_TERRACOTTA;
         }
      }

      var4 = var3.nextInt(4) + 2;

      int var5;
      int var6;
      int var7;
      int var8;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3.nextInt(3) + 1;
         var7 = var3.nextInt(64);

         for(var8 = 0; var7 + var8 < 64 && var8 < var6; ++var8) {
            this.clayBands[var7 + var8] = YELLOW_TERRACOTTA;
         }
      }

      var5 = var3.nextInt(4) + 2;

      int var9;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var3.nextInt(3) + 2;
         var8 = var3.nextInt(64);

         for(var9 = 0; var8 + var9 < 64 && var9 < var7; ++var9) {
            this.clayBands[var8 + var9] = BROWN_TERRACOTTA;
         }
      }

      var6 = var3.nextInt(4) + 2;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var3.nextInt(3) + 1;
         var9 = var3.nextInt(64);

         for(int var10 = 0; var9 + var10 < 64 && var10 < var8; ++var10) {
            this.clayBands[var9 + var10] = RED_TERRACOTTA;
         }
      }

      var7 = var3.nextInt(3) + 3;
      var8 = 0;

      for(var9 = 0; var9 < var7; ++var9) {
         boolean var12 = true;
         var8 += var3.nextInt(16) + 4;

         for(int var11 = 0; var8 + var11 < 64 && var11 < 1; ++var11) {
            this.clayBands[var8 + var11] = WHITE_TERRACOTTA;
            if (var8 + var11 > 1 && var3.nextBoolean()) {
               this.clayBands[var8 + var11 - 1] = LIGHT_GRAY_TERRACOTTA;
            }

            if (var8 + var11 < 63 && var3.nextBoolean()) {
               this.clayBands[var8 + var11 + 1] = LIGHT_GRAY_TERRACOTTA;
            }
         }
      }

   }

   protected BlockState getBand(int var1, int var2, int var3) {
      int var4 = (int)Math.round(this.clayBandsOffsetNoise.getValue((double)var1 / 512.0D, (double)var3 / 512.0D, false) * 2.0D);
      return this.clayBands[(var2 + var4 + 64) % 64];
   }

   static {
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
      ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
      TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
      YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
      BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
      RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
      LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
   }
}
