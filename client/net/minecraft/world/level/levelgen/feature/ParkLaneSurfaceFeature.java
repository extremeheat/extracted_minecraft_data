package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ParkLaneSurfaceFeature extends Feature<NoneFeatureConfiguration> {
   public ParkLaneSurfaceFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      ChunkPos var4 = new ChunkPos(var2);
      var2 = var4.getBlockAt(7, 0, 7);
      var2 = var2.atY(var3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var2).getY()).below();
      if (var2.getY() >= 8 && var3.getBiome(var2).is(Biomes.ARBORETUM)) {
         Direction[] var5 = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
         boolean[] var6 = new boolean[]{false, false, false, false};
         int var7 = 0;

         for(int var8 = 0; var8 < 4; ++var8) {
            if (this.connectsTo(var3, var4, var2, var5[var8])) {
               ++var7;
               var6[var8] = !this.isGeneratedTowardsORShouldNOTGenerateFromThisChunk(var3, var2, var5[var8]);
            }
         }

         if (var7 == 0) {
            return false;
         } else {
            int var16 = var7 == 4 ? 1 : 0;
            int var9 = var3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var2).getY();

            for(int var10 = -1 - var16; var10 <= 2 + var16; ++var10) {
               for(int var11 = -1 - var16; var11 <= 2 + var16; ++var11) {
                  BlockPos var12 = var2.offset(var10, 0, var11);
                  BlockPos var13 = var3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var12).below();
                  if (!this.isUntouchedTerrain(var3, var13)) {
                     return false;
                  }
               }
            }

            for(int var17 = -1 - var16; var17 <= 2 + var16; ++var17) {
               for(int var19 = -1 - var16; var19 <= 2 + var16; ++var19) {
                  BlockPos var20 = var2.offset(var17, 0, var19);
                  this.placePathOrPlanksIfFlying(var3, var20.atY(var9 - 1), null, false);
               }
            }

            for(int var18 = 0; var18 < 4; ++var18) {
               if (var6[var18]) {
                  this.generatePath(var3, var2, var5[var18], var16 == 1);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean isUntouchedTerrain(WorldGenLevel var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return var3.is(Blocks.PEELGRASS_BLOCK)
         || var3.is(Blocks.POISON_PATH)
         || var3.is(Blocks.POTATO_PLANKS)
         || var3.is(Blocks.LANTERN)
         || var3.is(Blocks.POTATO_FENCE);
   }

   private boolean connectsTo(WorldGenLevel var1, ChunkPos var2, BlockPos var3, Direction var4) {
      BlockPos var5 = var3.relative(var4, 16);
      if (!var1.getBiome(var5).is(Biomes.ARBORETUM)) {
         return false;
      } else {
         int var6 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var3).getY();
         int var7 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var5).getY();
         if (Math.abs(var6 - var7) > 10) {
            return false;
         } else {
            long var8 = var1.getSeed();
            ChunkPos var10 = var2;
            var3.relative(Direction.NORTH);
            if (var4 == Direction.NORTH || var4 == Direction.EAST) {
               var10 = new ChunkPos(var2.x + var4.getStepX(), var2.z + var4.getStepZ());
            }

            var8 += (long)var10.hashCode();
            Random var11 = new Random(var8);
            if (var4 == Direction.NORTH || var4 == Direction.SOUTH) {
               var11.nextFloat();
            }

            boolean var12 = var11.nextFloat() < 0.7F;
            if (!var12) {
               return false;
            } else {
               for(int var13 = -1; var13 <= 2; ++var13) {
                  for(int var14 = -2; var14 < 18; ++var14) {
                     BlockPos var15 = this.getPositionAtLane(var1, var3, var4, var13, var14);
                     if (!this.isUntouchedTerrain(var1, var15)) {
                        return false;
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   private boolean isGeneratedTowardsORShouldNOTGenerateFromThisChunk(WorldGenLevel var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3, 16);
      int var5 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var2).getY();
      int var6 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var4).getY();
      BlockPos var7 = var2.relative(var3, 4);
      var7 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7).below();
      if (!var1.getBlockState(var7).is(Blocks.POISON_PATH) && !var1.getBlockState(var7).is(Blocks.POTATO_PLANKS)) {
         return var6 < var5;
      } else {
         return true;
      }
   }

   private void generatePath(WorldGenLevel var1, BlockPos var2, Direction var3, boolean var4) {
      if (this.canConformToTerrain(var1, var2, var3)) {
         for(int var13 = -1; var13 <= 2; ++var13) {
            Direction var14 = null;
            if (var13 == -1 || var13 == 2) {
               var14 = this.getFencingDirection(var3, var13 < 0);
            }

            boolean var15 = false;

            for(int var16 = 2; var16 < 14; ++var16) {
               if (var15 && (double)var1.getRandom().nextFloat() < 0.3) {
                  var15 = false;
               }

               BlockPos var17 = this.getPositionAtLane(var1, var2, var3, var13, var16);
               var15 = this.placePathOrPlanksIfFlying(var1, var17, (!var4 || var16 != 2) && var16 != 13 ? var14 : null, var15);
            }
         }
      } else {
         int var5 = var2.getY();
         int var6 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var2.relative(var3, 16)).getY() - 1;

         for(int var7 = -1; var7 <= 2; ++var7) {
            Direction var8 = null;
            if (var7 == -1 || var7 == 2) {
               var8 = this.getFencingDirection(var3, var7 < 0);
            }

            boolean var9 = false;

            for(int var10 = 2; var10 < 14; ++var10) {
               if (var9 && (double)var1.getRandom().nextFloat() < 0.3) {
                  var9 = false;
               }

               int var11 = Math.round(Mth.lerp(((float)var10 - 2.0F) / 12.0F, (float)var5, (float)var6));
               BlockPos var12 = this.getPositionAtLane(var1, var2, var3, var7, var10).atY(var11);
               var9 = this.placePathOrPlanksIfFlying(var1, var12, (!var4 || var10 != 2) && var10 != 13 ? var8 : null, var9);
            }
         }
      }
   }

   @Nullable
   private Direction getFencingDirection(Direction var1, boolean var2) {
      if (var1.getAxis() == Direction.NORTH.getAxis()) {
         return var2 ? Direction.WEST : Direction.EAST;
      } else if (var1.getAxis() == Direction.EAST.getAxis()) {
         return var2 ? Direction.NORTH : Direction.SOUTH;
      } else {
         return null;
      }
   }

   private BlockPos getPositionAtLane(WorldGenLevel var1, BlockPos var2, Direction var3, int var4, int var5) {
      Vec3i var6 = var3.getNormal();
      BlockPos var7 = var2.offset(
         (var6.getX() > 0 ? 1 : 0) + var6.getX() * var5 + Math.abs(var6.getZ()) * var4,
         0,
         (var6.getZ() > 0 ? 1 : 0) + var6.getZ() * var5 + Math.abs(var6.getX()) * var4
      );
      return var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7).below();
   }

   private boolean canConformToTerrain(WorldGenLevel var1, BlockPos var2, Direction var3) {
      int var4 = var2.getY();

      for(int var5 = -1; var5 <= 2; ++var5) {
         int var6 = var4;

         for(int var7 = 2; var7 < 14; ++var7) {
            int var8 = this.getPositionAtLane(var1, var2, var3, var5, var7).getY();
            if (Math.abs(var6 - var8) > 1) {
               return false;
            }

            var6 = var8;
         }
      }

      return true;
   }

   private boolean placePathOrPlanksIfFlying(WorldGenLevel var1, BlockPos var2, @Nullable Direction var3, boolean var4) {
      int var5 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var2).getY() - 1;
      int var6 = Math.min(var5, var2.getY() + 10);

      for(int var7 = var2.getY() + 1; var7 <= var6; ++var7) {
         var1.setBlock(var2.atY(var7), Blocks.AIR.defaultBlockState(), 3);
      }

      boolean var10 = var1.getBlockState(var2.below()).isAir();
      var1.setBlock(var2, (var10 ? Blocks.POTATO_PLANKS : Blocks.POISON_PATH).defaultBlockState(), 3);
      if (var3 != null) {
         BlockPos var8 = var2.above();
         BlockPos var9 = var8.relative(var3);
         if (var10 && var1.getBlockState(var9).isAir()) {
            var1.setBlock(var8, Blocks.POTATO_FENCE.defaultBlockState(), 3);
            var1.getChunk(var8).markPosForPostprocessing(var8);
            return true;
         }

         if (var1.getBlockState(var9).isAir() && this.isUntouchedTerrain(var1, var2.relative(var3)) && (var4 || var1.getRandom().nextFloat() < 0.6F)) {
            var1.setBlock(var9, Blocks.POTATO_FENCE.defaultBlockState(), 3);
            var1.getChunk(var9).markPosForPostprocessing(var9);
            if (var1.getRandom().nextFloat() < 0.1F) {
               var1.setBlock(var9.above(), Blocks.LANTERN.defaultBlockState(), 3);
            }

            return true;
         }
      }

      return false;
   }
}
