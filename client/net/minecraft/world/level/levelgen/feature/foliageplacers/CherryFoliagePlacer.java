package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class CherryFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<CherryFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).and(var0.group(IntProvider.codec(4, 16).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("wide_bottom_layer_hole_chance").forGetter((var0x) -> {
         return var0x.wideBottomLayerHoleChance;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("corner_hole_chance").forGetter((var0x) -> {
         return var0x.wideBottomLayerHoleChance;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_chance").forGetter((var0x) -> {
         return var0x.hangingLeavesChance;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_extension_chance").forGetter((var0x) -> {
         return var0x.hangingLeavesExtensionChance;
      }))).apply(var0, CherryFoliagePlacer::new);
   });
   private final IntProvider height;
   private final float wideBottomLayerHoleChance;
   private final float cornerHoleChance;
   private final float hangingLeavesChance;
   private final float hangingLeavesExtensionChance;

   public CherryFoliagePlacer(IntProvider var1, IntProvider var2, IntProvider var3, float var4, float var5, float var6, float var7) {
      super(var1, var2);
      this.height = var3;
      this.wideBottomLayerHoleChance = var4;
      this.cornerHoleChance = var5;
      this.hangingLeavesChance = var6;
      this.hangingLeavesExtensionChance = var7;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.CHERRY_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      boolean var10 = var6.doubleTrunk();
      BlockPos var11 = var6.pos().above(var9);
      int var12 = var8 + var6.radiusOffset() - 1;
      this.placeLeavesRow(var1, var2, var3, var4, var11, var12 - 2, var7 - 3, var10);
      this.placeLeavesRow(var1, var2, var3, var4, var11, var12 - 1, var7 - 4, var10);

      for(int var13 = var7 - 5; var13 >= 0; --var13) {
         this.placeLeavesRow(var1, var2, var3, var4, var11, var12, var13, var10);
      }

      this.placeLeavesRowWithHangingLeavesBelow(var1, var2, var3, var4, var11, var12, -1, var10, this.hangingLeavesChance, this.hangingLeavesExtensionChance);
      this.placeLeavesRowWithHangingLeavesBelow(var1, var2, var3, var4, var11, var12 - 1, -2, var10, this.hangingLeavesChance, this.hangingLeavesExtensionChance);
   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return this.height.sample(var1);
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var3 == -1 && (var2 == var5 || var4 == var5) && var1.nextFloat() < this.wideBottomLayerHoleChance) {
         return true;
      } else {
         boolean var7 = var2 == var5 && var4 == var5;
         boolean var8 = var5 > 2;
         if (var8) {
            return var7 || var2 + var4 > var5 * 2 - 2 && var1.nextFloat() < this.cornerHoleChance;
         } else {
            return var7 && var1.nextFloat() < this.cornerHoleChance;
         }
      }
   }
}
