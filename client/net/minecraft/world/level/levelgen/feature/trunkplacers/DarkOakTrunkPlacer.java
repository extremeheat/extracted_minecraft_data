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
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakTrunkPlacer extends TrunkPlacer {
   public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, DarkOakTrunkPlacer::new);
   });

   public DarkOakTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      ArrayList var8 = Lists.newArrayList();
      BlockPos var9 = var4.below();
      setDirtAt(var1, var9);
      setDirtAt(var1, var9.east());
      setDirtAt(var1, var9.south());
      setDirtAt(var1, var9.south().east());
      Direction var10 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
      int var11 = var3 - var2.nextInt(4);
      int var12 = 2 - var2.nextInt(3);
      int var13 = var4.getX();
      int var14 = var4.getY();
      int var15 = var4.getZ();
      int var16 = var13;
      int var17 = var15;
      int var18 = var14 + var3 - 1;

      int var19;
      int var20;
      for(var19 = 0; var19 < var3; ++var19) {
         if (var19 >= var11 && var12 > 0) {
            var16 += var10.getStepX();
            var17 += var10.getStepZ();
            --var12;
         }

         var20 = var14 + var19;
         BlockPos var21 = new BlockPos(var16, var20, var17);
         if (TreeFeature.isAirOrLeaves(var1, var21)) {
            placeLog(var1, var2, var21, var5, var6, var7);
            placeLog(var1, var2, var21.east(), var5, var6, var7);
            placeLog(var1, var2, var21.south(), var5, var6, var7);
            placeLog(var1, var2, var21.east().south(), var5, var6, var7);
         }
      }

      var8.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var16, var18, var17), 0, true));

      for(var19 = -1; var19 <= 2; ++var19) {
         for(var20 = -1; var20 <= 2; ++var20) {
            if ((var19 < 0 || var19 > 1 || var20 < 0 || var20 > 1) && var2.nextInt(3) <= 0) {
               int var23 = var2.nextInt(3) + 2;

               for(int var22 = 0; var22 < var23; ++var22) {
                  placeLog(var1, var2, new BlockPos(var13 + var19, var18 - var22 - 1, var15 + var20), var5, var6, var7);
               }

               var8.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var16 + var19, var18, var17 + var20), 0, false));
            }
         }
      }

      return var8;
   }
}
