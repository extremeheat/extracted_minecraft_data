package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FancyFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<FancyFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return blobParts(var0).apply(var0, FancyFoliagePlacer::new);
   });

   public FancyFoliagePlacer(UniformInt var1, UniformInt var2, int var3) {
      super(var1, var2, var3);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      for(int var11 = var9; var11 >= var9 - var6; --var11) {
         int var12 = var7 + (var11 != var9 && var11 != var9 - var6 ? 1 : 0);
         this.placeLeavesRow(var1, var2, var3, var5.foliagePos(), var12, var8, var11, var5.doubleTrunk(), var10);
      }

   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return Mth.square((float)var2 + 0.5F) + Mth.square((float)var4 + 0.5F) > (float)(var5 * var5);
   }
}
