package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaPineFoliagePlacer extends FoliagePlacer {
   public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return foliagePlacerParts(var0).and(UniformInt.codec(0, 16, 8).fieldOf("crown_height").forGetter((var0x) -> {
         return var0x.crownHeight;
      })).apply(var0, MegaPineFoliagePlacer::new);
   });
   private final UniformInt crownHeight;

   public MegaPineFoliagePlacer(UniformInt var1, UniformInt var2, UniformInt var3) {
      super(var1, var2);
      this.crownHeight = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10) {
      BlockPos var11 = var5.foliagePos();
      int var12 = 0;

      for(int var13 = var11.getY() - var6 + var9; var13 <= var11.getY() + var9; ++var13) {
         int var14 = var11.getY() - var13;
         int var15 = var7 + var5.radiusOffset() + Mth.floor((float)var14 / (float)var6 * 3.5F);
         int var16;
         if (var14 > 0 && var15 == var12 && (var13 & 1) == 0) {
            var16 = var15 + 1;
         } else {
            var16 = var15;
         }

         this.placeLeavesRow(var1, var2, var3, new BlockPos(var11.getX(), var13, var11.getZ()), var16, var8, 0, var5.doubleTrunk(), var10);
         var12 = var15;
      }

   }

   public int foliageHeight(Random var1, int var2, TreeConfiguration var3) {
      return this.crownHeight.sample(var1);
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var2 + var4 >= 7) {
         return true;
      } else {
         return var2 * var2 + var4 * var4 > var5 * var5;
      }
   }
}
