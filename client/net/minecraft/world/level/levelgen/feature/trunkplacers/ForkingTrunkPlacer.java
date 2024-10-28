package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class ForkingTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, ForkingTrunkPlacer::new);
   });

   public ForkingTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FORKING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      setDirtAt(var1, var2, var3, var5.below(), var6);
      ArrayList var7 = Lists.newArrayList();
      Direction var8 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
      int var9 = var4 - var3.nextInt(4) - 1;
      int var10 = 3 - var3.nextInt(3);
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      int var12 = var5.getX();
      int var13 = var5.getZ();
      OptionalInt var14 = OptionalInt.empty();

      int var16;
      for(int var15 = 0; var15 < var4; ++var15) {
         var16 = var5.getY() + var15;
         if (var15 >= var9 && var10 > 0) {
            var12 += var8.getStepX();
            var13 += var8.getStepZ();
            --var10;
         }

         if (this.placeLog(var1, var2, var3, var11.set(var12, var16, var13), var6)) {
            var14 = OptionalInt.of(var16 + 1);
         }
      }

      if (var14.isPresent()) {
         var7.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var12, var14.getAsInt(), var13), 1, false));
      }

      var12 = var5.getX();
      var13 = var5.getZ();
      Direction var20 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
      if (var20 != var8) {
         var16 = var9 - var3.nextInt(2) - 1;
         int var17 = 1 + var3.nextInt(3);
         var14 = OptionalInt.empty();

         for(int var18 = var16; var18 < var4 && var17 > 0; --var17) {
            if (var18 >= 1) {
               int var19 = var5.getY() + var18;
               var12 += var20.getStepX();
               var13 += var20.getStepZ();
               if (this.placeLog(var1, var2, var3, var11.set(var12, var19, var13), var6)) {
                  var14 = OptionalInt.of(var19 + 1);
               }
            }

            ++var18;
         }

         if (var14.isPresent()) {
            var7.add(new FoliagePlacer.FoliageAttachment(new BlockPos(var12, var14.getAsInt(), var13), 0, false));
         }
      }

      return var7;
   }
}
