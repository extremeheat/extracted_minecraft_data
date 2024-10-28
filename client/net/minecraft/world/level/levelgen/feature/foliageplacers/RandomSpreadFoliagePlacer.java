package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).and(var0.group(IntProvider.codec(1, 512).fieldOf("foliage_height").forGetter((var0x) -> {
         return var0x.foliageHeight;
      }), Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter((var0x) -> {
         return var0x.leafPlacementAttempts;
      }))).apply(var0, RandomSpreadFoliagePlacer::new);
   });
   private final IntProvider foliageHeight;
   private final int leafPlacementAttempts;

   public RandomSpreadFoliagePlacer(IntProvider var1, IntProvider var2, IntProvider var3, int var4) {
      super(var1, var2);
      this.foliageHeight = var3;
      this.leafPlacementAttempts = var4;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      BlockPos var10 = var6.pos();
      BlockPos.MutableBlockPos var11 = var10.mutable();

      for(int var12 = 0; var12 < this.leafPlacementAttempts; ++var12) {
         var11.setWithOffset(var10, var3.nextInt(var8) - var3.nextInt(var8), var3.nextInt(var7) - var3.nextInt(var7), var3.nextInt(var8) - var3.nextInt(var8));
         tryPlaceLeaf(var1, var2, var3, var4, var11);
      }

   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return this.foliageHeight.sample(var1);
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return false;
   }
}
