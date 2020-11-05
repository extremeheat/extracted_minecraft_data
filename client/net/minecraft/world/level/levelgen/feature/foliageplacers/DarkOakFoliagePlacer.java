package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakFoliagePlacer extends FoliagePlacer {
   public static final Codec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).apply(var0, DarkOakFoliagePlacer::new);
   });

   public DarkOakFoliagePlacer(UniformInt var1, UniformInt var2) {
      super(var1, var2);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      BlockPos var11 = var5.foliagePos().above(var9);
      boolean var12 = var5.doubleTrunk();
      if (var12) {
         this.placeLeavesRow(var1, var2, var3, var11, var7 + 2, var8, -1, var12, var10);
         this.placeLeavesRow(var1, var2, var3, var11, var7 + 3, var8, 0, var12, var10);
         this.placeLeavesRow(var1, var2, var3, var11, var7 + 2, var8, 1, var12, var10);
         if (var2.nextBoolean()) {
            this.placeLeavesRow(var1, var2, var3, var11, var7, var8, 2, var12, var10);
         }
      } else {
         this.placeLeavesRow(var1, var2, var3, var11, var7 + 2, var8, -1, var12, var10);
         this.placeLeavesRow(var1, var2, var3, var11, var7 + 1, var8, 0, var12, var10);
      }

   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return 4;
   }

   protected boolean shouldSkipLocationSigned(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var3 != 0 || !var6 || var2 != -var5 && var2 < var5 || var4 != -var5 && var4 < var5 ? super.shouldSkipLocationSigned(var1, var2, var3, var4, var5, var6) : true;
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var3 == -1 && !var6) {
         return var2 == var5 && var4 == var5;
      } else if (var3 == 1) {
         return var2 + var4 > var5 * 2 - 2;
      } else {
         return false;
      }
   }
}
