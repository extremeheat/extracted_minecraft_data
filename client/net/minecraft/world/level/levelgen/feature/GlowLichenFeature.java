package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;

public class GlowLichenFeature extends Feature<GlowLichenConfiguration> {
   public GlowLichenFeature(Codec<GlowLichenConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<GlowLichenConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      Random var4 = var1.random();
      GlowLichenConfiguration var5 = (GlowLichenConfiguration)var1.config();
      if (!isAirOrWater(var2.getBlockState(var3))) {
         return false;
      } else {
         List var6 = getShuffledDirections(var5, var4);
         if (placeGlowLichenIfPossible(var2, var3, var2.getBlockState(var3), var5, var4, var6)) {
            return true;
         } else {
            BlockPos.MutableBlockPos var7 = var3.mutable();
            Iterator var8 = var6.iterator();

            while(var8.hasNext()) {
               Direction var9 = (Direction)var8.next();
               var7.set(var3);
               List var10 = getShuffledDirectionsExcept(var5, var4, var9.getOpposite());

               for(int var11 = 0; var11 < var5.searchRange; ++var11) {
                  var7.setWithOffset(var3, (Direction)var9);
                  BlockState var12 = var2.getBlockState(var7);
                  if (!isAirOrWater(var12) && !var12.is(Blocks.GLOW_LICHEN)) {
                     break;
                  }

                  if (placeGlowLichenIfPossible(var2, var7, var12, var5, var4, var10)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean placeGlowLichenIfPossible(WorldGenLevel var0, BlockPos var1, BlockState var2, GlowLichenConfiguration var3, Random var4, List<Direction> var5) {
      BlockPos.MutableBlockPos var6 = var1.mutable();
      Iterator var7 = var5.iterator();

      Direction var8;
      BlockState var9;
      do {
         if (!var7.hasNext()) {
            return false;
         }

         var8 = (Direction)var7.next();
         var9 = var0.getBlockState(var6.setWithOffset(var1, (Direction)var8));
      } while(!var3.canBePlacedOn.contains(var9.getBlock()));

      GlowLichenBlock var10 = (GlowLichenBlock)Blocks.GLOW_LICHEN;
      BlockState var11 = var10.getStateForPlacement(var2, var0, var1, var8);
      if (var11 == null) {
         return false;
      } else {
         var0.setBlock(var1, var11, 3);
         var0.getChunk(var1).markPosForPostprocessing(var1);
         if (var4.nextFloat() < var3.chanceOfSpreading) {
            var10.spreadFromFaceTowardRandomDirection(var11, var0, var1, var8, var4, true);
         }

         return true;
      }
   }

   public static List<Direction> getShuffledDirections(GlowLichenConfiguration var0, Random var1) {
      ArrayList var2 = Lists.newArrayList(var0.validDirections);
      Collections.shuffle(var2, var1);
      return var2;
   }

   public static List<Direction> getShuffledDirectionsExcept(GlowLichenConfiguration var0, Random var1, Direction var2) {
      List var3 = (List)var0.validDirections.stream().filter((var1x) -> {
         return var1x != var2;
      }).collect(Collectors.toList());
      Collections.shuffle(var3, var1);
      return var3;
   }

   private static boolean isAirOrWater(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER);
   }
}
