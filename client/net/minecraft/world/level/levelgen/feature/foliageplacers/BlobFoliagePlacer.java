package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BlobFoliagePlacer extends FoliagePlacer {
   public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return blobParts(var0).apply(var0, BlobFoliagePlacer::new);
   });
   protected final int height;

   protected static <P extends BlobFoliagePlacer> P3<Mu<P>, UniformInt, UniformInt, Integer> blobParts(Instance<P> var0) {
      return foliagePlacerParts(var0).and(Codec.intRange(0, 16).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      }));
   }

   public BlobFoliagePlacer(UniformInt var1, UniformInt var2, int var3) {
      super(var1, var2);
      this.height = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      for(int var11 = var9; var11 >= var9 - var6; --var11) {
         int var12 = Math.max(var7 + var5.radiusOffset() - 1 - var11 / 2, 0);
         this.placeLeavesRow(var1, var2, var3, var5.foliagePos(), var12, var8, var11, var5.doubleTrunk(), var10);
      }

   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return this.height;
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && (var1.nextInt(2) == 0 || var3 == 0);
   }
}
