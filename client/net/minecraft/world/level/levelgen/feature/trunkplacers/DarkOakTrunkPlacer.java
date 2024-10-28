package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class DarkOakTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, DarkOakTrunkPlacer::new);
   });

   public DarkOakTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      ArrayList var7 = Lists.newArrayList();
      BlockPos var8 = var5.below();
      setDirtAt(var1, var2, var3, var8, var6);
      setDirtAt(var1, var2, var3, var8.east(), var6);
      setDirtAt(var1, var2, var3, var8.south(), var6);
      setDirtAt(var1, var2, var3, var8.south().east(), var6);
      Direction var9 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
      int var10 = var4 - var3.nextInt(4);
      int var11 = 2 - var3.nextInt(3);
      int var12 = var5.getX();
      int var13 = var5.getY();
      int var14 = var5.getZ();
      int var15 = var12;
      int var16 = var14;
      int var17 = var13 + var4 - 1;

      int var18;
      int var19;
      for(var18 = 0; var18 < var4; ++var18) {
         if (var18 >= var10 && var11 > 0) {
            var15 += var9.getStepX();
            var16 += var9.getStepZ();
            --var11;
         }

         var19 = var13 + var18;
         BlockPos var20 = new BlockPos(var15, var19, var16);
         if (TreeFeature.isAirOrLeaves(var1, var20)) {
            this.placeLog(var1, var2, var3, var20, var6);
            this.placeLog(var1, var2, var3, var20.east(), var6);
            this.placeLog(var1, var2, var3, var20.south(), var6);
            this.placeLog(var1, var2, var3, var20.east().south(), var6);
         }
      }

      var7.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var15, var17, var16), 0, true));

      for(var18 = -1; var18 <= 2; ++var18) {
         for(var19 = -1; var19 <= 2; ++var19) {
            if ((var18 < 0 || var18 > 1 || var19 < 0 || var19 > 1) && var3.nextInt(3) <= 0) {
               int var22 = var3.nextInt(3) + 2;

               for(int var21 = 0; var21 < var22; ++var21) {
                  this.placeLog(var1, var2, var3, new BlockPos(var12 + var18, var17 - var21 - 1, var14 + var19), var6);
               }

               var7.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var15 + var18, var17, var16 + var19), 0, false));
            }
         }
      }

      return var7;
   }
}
