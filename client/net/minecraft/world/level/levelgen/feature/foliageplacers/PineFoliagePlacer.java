package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class PineFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<PineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).and(IntProvider.codec(0, 24).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, PineFoliagePlacer::new);
   });
   private final IntProvider height;

   public PineFoliagePlacer(IntProvider var1, IntProvider var2, IntProvider var3) {
      super(var1, var2);
      this.height = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      int var10 = 0;

      for(int var11 = var9; var11 >= var9 - var7; --var11) {
         this.placeLeavesRow(var1, var2, var3, var4, var6.pos(), var10, var11, var6.doubleTrunk());
         if (var10 >= 1 && var11 == var9 - var7 + 1) {
            --var10;
         } else if (var10 < var8 + var6.radiusOffset()) {
            ++var10;
         }
      }

   }

   public int foliageRadius(RandomSource var1, int var2) {
      return super.foliageRadius(var1, var2) + var1.nextInt(Math.max(var2 + 1, 1));
   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return this.height.sample(var1);
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var5 > 0;
   }
}
