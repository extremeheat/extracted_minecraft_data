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

public class AcaciaFoliagePlacer extends FoliagePlacer {
   public static final Codec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).apply(var0, AcaciaFoliagePlacer::new);
   });

   public AcaciaFoliagePlacer(UniformInt var1, UniformInt var2) {
      super(var1, var2);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      boolean var11 = var5.doubleTrunk();
      BlockPos var12 = var5.foliagePos().above(var9);
      this.placeLeavesRow(var1, var2, var3, var12, var7 + var5.radiusOffset(), var8, -1 - var6, var11, var10);
      this.placeLeavesRow(var1, var2, var3, var12, var7 - 1, var8, -var6, var11, var10);
      this.placeLeavesRow(var1, var2, var3, var12, var7 + var5.radiusOffset() - 1, var8, 0, var11, var10);
   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return 0;
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var3 == 0) {
         return (var2 > 1 || var4 > 1) && var2 != 0 && var4 != 0;
      } else {
         return var2 == var5 && var4 == var5 && var5 > 0;
      }
   }
}
