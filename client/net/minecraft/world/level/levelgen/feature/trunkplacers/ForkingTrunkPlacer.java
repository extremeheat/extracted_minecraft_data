package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ForkingTrunkPlacer extends TrunkPlacer {
   public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, ForkingTrunkPlacer::new);
   });

   public ForkingTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FORKING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      setDirtAt(var1, var4.below());
      ArrayList var8 = Lists.newArrayList();
      Direction var9 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
      int var10 = var3 - var2.nextInt(4) - 1;
      int var11 = 3 - var2.nextInt(3);
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();
      int var13 = var4.getX();
      int var14 = var4.getZ();
      int var15 = 0;

      int var17;
      for(int var16 = 0; var16 < var3; ++var16) {
         var17 = var4.getY() + var16;
         if (var16 >= var10 && var11 > 0) {
            var13 += var9.getStepX();
            var14 += var9.getStepZ();
            --var11;
         }

         if (placeLog(var1, var2, var12.set(var13, var17, var14), var5, var6, var7)) {
            var15 = var17 + 1;
         }
      }

      var8.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var13, var15, var14), 1, false));
      var13 = var4.getX();
      var14 = var4.getZ();
      Direction var21 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
      if (var21 != var9) {
         var17 = var10 - var2.nextInt(2) - 1;
         int var18 = 1 + var2.nextInt(3);
         var15 = 0;

         for(int var19 = var17; var19 < var3 && var18 > 0; --var18) {
            if (var19 >= 1) {
               int var20 = var4.getY() + var19;
               var13 += var21.getStepX();
               var14 += var21.getStepZ();
               if (placeLog(var1, var2, var12.set(var13, var20, var14), var5, var6, var7)) {
                  var15 = var20 + 1;
               }
            }

            ++var19;
         }

         if (var15 > 1) {
            var8.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var13, var15, var14), 0, false));
         }
      }

      return var8;
   }
}
