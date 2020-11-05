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

public class PineFoliagePlacer extends FoliagePlacer {
   public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).and(UniformInt.codec(0, 16, 8).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, PineFoliagePlacer::new);
   });
   private final UniformInt height;

   public PineFoliagePlacer(UniformInt var1, UniformInt var2, UniformInt var3) {
      super(var1, var2);
      this.height = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      int var11 = 0;

      for(int var12 = var9; var12 >= var9 - var6; --var12) {
         this.placeLeavesRow(var1, var2, var3, var5.foliagePos(), var11, var8, var12, var5.doubleTrunk(), var10);
         if (var11 >= 1 && var12 == var9 - var6 + 1) {
            --var11;
         } else if (var11 < var7 + var5.radiusOffset()) {
            ++var11;
         }
      }

   }

   public int foliageRadius(Random var1, int var2) {
      return super.foliageRadius(var1, var2) + var1.nextInt(var2 + 1);
   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return this.height.sample(var1);
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var5 > 0;
   }
}
