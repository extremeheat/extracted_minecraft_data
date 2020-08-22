package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public abstract class FoliagePlacer implements Serializable {
   protected final int radius;
   protected final int radiusRandom;
   protected final FoliagePlacerType type;

   public FoliagePlacer(int var1, int var2, FoliagePlacerType var3) {
      this.radius = var1;
      this.radiusRandom = var2;
      this.type = var3;
   }

   public abstract void createFoliage(LevelSimulatedRW var1, Random var2, SmallTreeConfiguration var3, int var4, int var5, int var6, BlockPos var7, Set var8);

   public abstract int foliageRadius(Random var1, int var2, int var3, SmallTreeConfiguration var4);

   protected abstract boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, int var6);

   public abstract int getTreeRadiusForHeight(int var1, int var2, int var3, int var4);

   protected void placeLeavesRow(LevelSimulatedRW var1, Random var2, SmallTreeConfiguration var3, int var4, BlockPos var5, int var6, int var7, Set var8) {
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = -var7; var10 <= var7; ++var10) {
         for(int var11 = -var7; var11 <= var7; ++var11) {
            if (!this.shouldSkipLocation(var2, var4, var10, var6, var11, var7)) {
               var9.set(var10 + var5.getX(), var6 + var5.getY(), var11 + var5.getZ());
               this.placeLeaf(var1, var2, var9, var3, var8);
            }
         }
      }

   }

   protected void placeLeaf(LevelSimulatedRW var1, Random var2, BlockPos var3, SmallTreeConfiguration var4, Set var5) {
      if (AbstractTreeFeature.isAirOrLeaves(var1, var3) || AbstractTreeFeature.isReplaceablePlant(var1, var3) || AbstractTreeFeature.isBlockWater(var1, var3)) {
         var1.setBlock(var3, var4.leavesProvider.getState(var2, var3), 19);
         var5.add(var3.immutable());
      }

   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.FOLIAGE_PLACER_TYPES.getKey(this.type).toString())).put(var1.createString("radius"), var1.createInt(this.radius)).put(var1.createString("radius_random"), var1.createInt(this.radiusRandom));
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }
}
