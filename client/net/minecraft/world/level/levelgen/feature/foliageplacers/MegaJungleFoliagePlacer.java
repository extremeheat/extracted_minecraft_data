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

public class MegaJungleFoliagePlacer extends FoliagePlacer {
   public static final Codec<MegaJungleFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).and(Codec.intRange(0, 16).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, MegaJungleFoliagePlacer::new);
   });
   protected final int height;

   public MegaJungleFoliagePlacer(UniformInt var1, UniformInt var2, int var3) {
      super(var1, var2);
      this.height = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_JUNGLE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      int var11 = var5.doubleTrunk() ? var6 : 1 + var2.nextInt(2);

      for(int var12 = var9; var12 >= var9 - var11; --var12) {
         int var13 = var7 + var5.radiusOffset() + 1 - var12;
         this.placeLeavesRow(var1, var2, var3, var5.foliagePos(), var13, var8, var12, var5.doubleTrunk(), var10);
      }

   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return this.height;
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var2 + var4 >= 7) {
         return true;
      } else {
         return var2 * var2 + var4 * var4 > var5 * var5;
      }
   }
}
