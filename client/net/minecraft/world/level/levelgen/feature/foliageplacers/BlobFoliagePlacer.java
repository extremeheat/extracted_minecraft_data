package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class BlobFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(var0 -> blobParts(var0).apply(var0, BlobFoliagePlacer::new));
   protected final int height;

   protected static <P extends BlobFoliagePlacer> P3<Mu<P>, IntProvider, IntProvider, Integer> blobParts(Instance<P> var0) {
      return foliagePlacerParts(var0).and(Codec.intRange(0, 16).fieldOf("height").forGetter(var0x -> var0x.height));
   }

   public BlobFoliagePlacer(IntProvider var1, IntProvider var2, int var3) {
      super(var1, var2);
      this.height = var3;
   }

   @Override
   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
   }

   @Override
   protected void createFoliage(
      LevelSimulatedReader var1,
      FoliagePlacer.FoliageSetter var2,
      RandomSource var3,
      TreeConfiguration var4,
      int var5,
      FoliagePlacer.FoliageAttachment var6,
      int var7,
      int var8,
      int var9
   ) {
      for(int var10 = var9; var10 >= var9 - var7; --var10) {
         int var11 = Math.max(var8 + var6.radiusOffset() - 1 - var10 / 2, 0);
         this.placeLeavesRow(var1, var2, var3, var4, var6.pos(), var11, var10, var6.doubleTrunk());
      }
   }

   @Override
   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return this.height;
   }

   @Override
   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && (var1.nextInt(2) == 0 || var3 == 0);
   }
}
