package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class MegaJungleFoliagePlacer extends FoliagePlacer {
   public static final Codec<MegaJungleFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).and(Codec.intRange(0, 16).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, MegaJungleFoliagePlacer::new);
   });
   protected final int height;

   public MegaJungleFoliagePlacer(IntProvider var1, IntProvider var2, int var3) {
      super(var1, var2);
      this.height = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_JUNGLE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      int var10 = var6.doubleTrunk() ? var7 : 1 + var3.nextInt(2);

      for(int var11 = var9; var11 >= var9 - var10; --var11) {
         int var12 = var8 + var6.radiusOffset() + 1 - var11;
         this.placeLeavesRow(var1, var2, var3, var4, var6.pos(), var12, var11, var6.doubleTrunk());
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
