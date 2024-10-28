package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class MegaPineFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).and(IntProvider.codec(0, 24).fieldOf("crown_height").forGetter((var0x) -> {
         return var0x.crownHeight;
      })).apply(var0, MegaPineFoliagePlacer::new);
   });
   private final IntProvider crownHeight;

   public MegaPineFoliagePlacer(IntProvider var1, IntProvider var2, IntProvider var3) {
      super(var1, var2);
      this.crownHeight = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      BlockPos var10 = var6.pos();
      int var11 = 0;

      for(int var12 = var10.getY() - var7 + var9; var12 <= var10.getY() + var9; ++var12) {
         int var13 = var10.getY() - var12;
         int var14 = var8 + var6.radiusOffset() + Mth.floor((float)var13 / (float)var7 * 3.5F);
         int var15;
         if (var13 > 0 && var14 == var11 && (var12 & 1) == 0) {
            var15 = var14 + 1;
         } else {
            var15 = var14;
         }

         this.placeLeavesRow(var1, var2, var3, var4, new BlockPos(var10.getX(), var12, var10.getZ()), var15, 0, var6.doubleTrunk());
         var11 = var14;
      }

   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return this.crownHeight.sample(var1);
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var2 + var4 >= 7) {
         return true;
      } else {
         return var2 * var2 + var4 * var4 > var5 * var5;
      }
   }
}
