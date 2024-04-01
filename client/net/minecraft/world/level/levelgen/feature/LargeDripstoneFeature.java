package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.phys.Vec3;

public class LargeDripstoneFeature extends Feature<LargeDripstoneConfiguration> {
   public LargeDripstoneFeature(Codec<LargeDripstoneConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<LargeDripstoneConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      LargeDripstoneConfiguration var4 = (LargeDripstoneConfiguration)var1.config();
      RandomSource var5 = var1.random();
      PointedDripstoneBlock var6 = var4.block;
      if (!DripstoneUtils.isEmptyOrWater(var2, var3)) {
         return false;
      } else {
         Optional var7 = Column.scan(
            var2, var3, var4.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, var0 -> DripstoneUtils.isDripstoneBaseOrLava(var0)
         );
         if (var7.isEmpty()) {
            return false;
         } else if (var7.get() instanceof Column.Range || var6 == Blocks.POTATO_BUD && ((Column)var7.get()).getFloor().isPresent()) {
            Column var8 = (Column)var7.get();
            double var9 = var8 instanceof Column.Range var11 ? (double)var11.height() : (double)Mth.randomBetween(var5, 10.0F, 20.0F);
            if (var9 < 4.0) {
               return false;
            } else {
               int var19 = (int)(var9 * (double)var4.maxColumnRadiusToCaveHeightRatio);
               int var12 = Mth.clamp(var19, var4.columnRadius.getMinValue(), var4.columnRadius.getMaxValue());
               int var13 = Mth.randomBetweenInclusive(var5, var4.columnRadius.getMinValue(), var12);
               Optional var14;
               if (var8.getCeiling().isPresent()) {
                  var14 = Optional.of(makeDripstone(var3.atY(var8.getCeiling().getAsInt() - 1), false, var5, var13, var4.stalactiteBluntness, var4.heightScale));
               } else {
                  var14 = Optional.empty();
               }

               LargeDripstoneFeature.LargeDripstone var15 = makeDripstone(
                  var3.atY(var8.getFloor().getAsInt() + 1), true, var5, var13, var4.stalagmiteBluntness, var4.heightScale
               );
               LargeDripstoneFeature.WindOffsetter var16;
               if (var14.isPresent() && ((LargeDripstoneFeature.LargeDripstone)var14.get()).isSuitableForWind(var4) && var15.isSuitableForWind(var4)) {
                  var16 = new LargeDripstoneFeature.WindOffsetter(var3.getY(), var5, var4.windSpeed);
               } else {
                  var16 = LargeDripstoneFeature.WindOffsetter.noWind();
               }

               boolean var17 = var14.isPresent()
                  && ((LargeDripstoneFeature.LargeDripstone)var14.get()).moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(var2, var16);
               boolean var18 = var15.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(var2, var16);
               if (var14.isPresent() && var17) {
                  ((LargeDripstoneFeature.LargeDripstone)var14.get()).placeBlocks(var6, var2, var5, var16);
               }

               if (var18) {
                  var15.placeBlocks(var6, var2, var5, var16);
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   private static LargeDripstoneFeature.LargeDripstone makeDripstone(
      BlockPos var0, boolean var1, RandomSource var2, int var3, FloatProvider var4, FloatProvider var5
   ) {
      return new LargeDripstoneFeature.LargeDripstone(var0, var1, var3, (double)var4.sample(var2), (double)var5.sample(var2));
   }

   private void placeDebugMarkers(WorldGenLevel var1, BlockPos var2, Column.Range var3, LargeDripstoneFeature.WindOffsetter var4) {
      var1.setBlock(var4.offset(var2.atY(var3.ceiling() - 1)), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
      var1.setBlock(var4.offset(var2.atY(var3.floor() + 1)), Blocks.GOLD_BLOCK.defaultBlockState(), 2);

      for(BlockPos.MutableBlockPos var5 = var2.atY(var3.floor() + 2).mutable(); var5.getY() < var3.ceiling() - 1; var5.move(Direction.UP)) {
         BlockPos var6 = var4.offset(var5);
         if (DripstoneUtils.isEmptyOrWater(var1, var6) || var1.getBlockState(var6).is(Blocks.DRIPSTONE_BLOCK)) {
            var1.setBlock(var6, Blocks.CREEPER_HEAD.defaultBlockState(), 2);
         }
      }
   }

   static final class LargeDripstone {
      private BlockPos root;
      private final boolean pointingUp;
      private int radius;
      private final double bluntness;
      private final double scale;

      LargeDripstone(BlockPos var1, boolean var2, int var3, double var4, double var6) {
         super();
         this.root = var1;
         this.pointingUp = var2;
         this.radius = var3;
         this.bluntness = var4;
         this.scale = var6;
      }

      private int getHeight() {
         return this.getHeightAtRadius(0.0F);
      }

      private int getMinY() {
         return this.pointingUp ? this.root.getY() : this.root.getY() - this.getHeight();
      }

      private int getMaxY() {
         return !this.pointingUp ? this.root.getY() : this.root.getY() + this.getHeight();
      }

      boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel var1, LargeDripstoneFeature.WindOffsetter var2) {
         while(this.radius > 1) {
            BlockPos.MutableBlockPos var3 = this.root.mutable();
            int var4 = Math.min(10, this.getHeight());

            for(int var5 = 0; var5 < var4; ++var5) {
               if (var1.getBlockState(var3).is(Blocks.LAVA)) {
                  return false;
               }

               if (DripstoneUtils.isCircleMostlyEmbeddedInStone(var1, var2.offset(var3), this.radius)) {
                  this.root = var3;
                  return true;
               }

               var3.move(this.pointingUp ? Direction.DOWN : Direction.UP);
            }

            this.radius /= 2;
         }

         return false;
      }

      private int getHeightAtRadius(float var1) {
         return (int)DripstoneUtils.getDripstoneHeight((double)var1, (double)this.radius, this.scale, this.bluntness);
      }

      void placeBlocks(PointedDripstoneBlock var1, WorldGenLevel var2, RandomSource var3, LargeDripstoneFeature.WindOffsetter var4) {
         for(int var5 = -this.radius; var5 <= this.radius; ++var5) {
            for(int var6 = -this.radius; var6 <= this.radius; ++var6) {
               float var7 = Mth.sqrt((float)(var5 * var5 + var6 * var6));
               if (!(var7 > (float)this.radius)) {
                  int var8 = this.getHeightAtRadius(var7);
                  if (var8 > 0) {
                     if ((double)var3.nextFloat() < 0.2) {
                        var8 = (int)((float)var8 * Mth.randomBetween(var3, 0.8F, 1.0F));
                     }

                     BlockPos.MutableBlockPos var9 = this.root.offset(var5, 0, var6).mutable();
                     boolean var10 = false;
                     int var11 = this.pointingUp && var1 == Blocks.POINTED_DRIPSTONE
                        ? var2.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var9.getX(), var9.getZ())
                        : 2147483647;

                     for(int var12 = 0; var12 < var8 && var9.getY() < var11; ++var12) {
                        Object var13 = var4.offset(var9);
                        if (Math.abs(((BlockPos)var13).getZ() - this.root.getZ()) > 16 || Math.abs(((BlockPos)var13).getX() - this.root.getX()) > 16) {
                           var13 = var9;
                        }

                        if (DripstoneUtils.isEmptyOrWaterOrLava(var2, (BlockPos)var13)) {
                           var10 = true;
                           Block var14 = var1.getLargeBlock();
                           var2.setBlock((BlockPos)var13, var14.defaultBlockState(), 2);
                        } else if (var10 && var2.getBlockState((BlockPos)var13).is(BlockTags.BASE_STONE_OVERWORLD)) {
                           break;
                        }

                        var9.move(this.pointingUp ? Direction.UP : Direction.DOWN);
                     }
                  }
               }
            }
         }
      }

      boolean isSuitableForWind(LargeDripstoneConfiguration var1) {
         return this.radius >= var1.minRadiusForWind && this.bluntness >= (double)var1.minBluntnessForWind;
      }
   }

   static final class WindOffsetter {
      private final int originY;
      @Nullable
      private final Vec3 windSpeed;

      WindOffsetter(int var1, RandomSource var2, FloatProvider var3) {
         super();
         this.originY = var1;
         float var4 = var3.sample(var2);
         float var5 = Mth.randomBetween(var2, 0.0F, 3.1415927F);
         this.windSpeed = new Vec3((double)(Mth.cos(var5) * var4), 0.0, (double)(Mth.sin(var5) * var4));
      }

      private WindOffsetter() {
         super();
         this.originY = 0;
         this.windSpeed = null;
      }

      static LargeDripstoneFeature.WindOffsetter noWind() {
         return new LargeDripstoneFeature.WindOffsetter();
      }

      BlockPos offset(BlockPos var1) {
         if (this.windSpeed == null) {
            return var1;
         } else {
            int var2 = this.originY - var1.getY();
            Vec3 var3 = this.windSpeed.scale((double)var2);
            return var1.offset(Mth.floor(var3.x), 0, Mth.floor(var3.z));
         }
      }
   }
}
