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

public class BushFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return blobParts(var0).apply(var0, BushFoliagePlacer::new);
   });

   public BushFoliagePlacer(UniformInt var1, UniformInt var2, int var3) {
      super(var1, var2, var3);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      for(int var11 = var9; var11 >= var9 - var6; --var11) {
         int var12 = var7 + var5.radiusOffset() - 1 - var11;
         this.placeLeavesRow(var1, var2, var3, var5.foliagePos(), var12, var8, var11, var5.doubleTrunk(), var10);
      }

   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var1.nextInt(2) == 0;
   }
}
