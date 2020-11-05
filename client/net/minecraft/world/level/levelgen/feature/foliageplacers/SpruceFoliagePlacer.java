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

public class SpruceFoliagePlacer extends FoliagePlacer {
   public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).and(UniformInt.codec(0, 16, 8).fieldOf("trunk_height").forGetter((var0x) -> {
         return var0x.trunkHeight;
      })).apply(var0, SpruceFoliagePlacer::new);
   });
   private final UniformInt trunkHeight;

   public SpruceFoliagePlacer(UniformInt var1, UniformInt var2, UniformInt var3) {
      super(var1, var2);
      this.trunkHeight = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      BlockPos var11 = var5.foliagePos();
      int var12 = var2.nextInt(2);
      int var13 = 1;
      byte var14 = 0;

      for(int var15 = var9; var15 >= -var6; --var15) {
         this.placeLeavesRow(var1, var2, var3, var11, var12, var8, var15, var5.doubleTrunk(), var10);
         if (var12 >= var13) {
            var12 = var14;
            var14 = 1;
            var13 = Math.min(var13 + 1, var7 + var5.radiusOffset());
         } else {
            ++var12;
         }
      }

   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return Math.max(4, var2 - this.trunkHeight.sample(var1));
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var5 > 0;
   }
}
