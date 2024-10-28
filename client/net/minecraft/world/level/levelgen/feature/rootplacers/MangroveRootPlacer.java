package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MangroveRootPlacer extends RootPlacer {
   public static final int ROOT_WIDTH_LIMIT = 8;
   public static final int ROOT_LENGTH_LIMIT = 15;
   public static final MapCodec<MangroveRootPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return rootPlacerParts(var0).and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter((var0x) -> {
         return var0x.mangroveRootPlacement;
      })).apply(var0, MangroveRootPlacer::new);
   });
   private final MangroveRootPlacement mangroveRootPlacement;

   public MangroveRootPlacer(IntProvider var1, BlockStateProvider var2, Optional<AboveRootPlacement> var3, MangroveRootPlacement var4) {
      super(var1, var2, var3);
      this.mangroveRootPlacement = var4;
   }

   public boolean placeRoots(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, BlockPos var5, TreeConfiguration var6) {
      ArrayList var7 = Lists.newArrayList();
      BlockPos.MutableBlockPos var8 = var4.mutable();

      while(var8.getY() < var5.getY()) {
         if (!this.canPlaceRoot(var1, var8)) {
            return false;
         }

         var8.move(Direction.UP);
      }

      var7.add(var5.below());
      Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

      while(var9.hasNext()) {
         Direction var10 = (Direction)var9.next();
         BlockPos var11 = var5.relative(var10);
         ArrayList var12 = Lists.newArrayList();
         if (!this.simulateRoots(var1, var3, var11, var10, var5, var12, 0)) {
            return false;
         }

         var7.addAll(var12);
         var7.add(var5.relative(var10));
      }

      var9 = var7.iterator();

      while(var9.hasNext()) {
         BlockPos var13 = (BlockPos)var9.next();
         this.placeRoot(var1, var2, var3, var13, var6);
      }

      return true;
   }

   private boolean simulateRoots(LevelSimulatedReader var1, RandomSource var2, BlockPos var3, Direction var4, BlockPos var5, List<BlockPos> var6, int var7) {
      int var8 = this.mangroveRootPlacement.maxRootLength();
      if (var7 != var8 && var6.size() <= var8) {
         List var9 = this.potentialRootPositions(var3, var4, var2, var5);
         Iterator var10 = var9.iterator();

         while(var10.hasNext()) {
            BlockPos var11 = (BlockPos)var10.next();
            if (this.canPlaceRoot(var1, var11)) {
               var6.add(var11);
               if (!this.simulateRoots(var1, var2, var11, var4, var5, var6, var7 + 1)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected List<BlockPos> potentialRootPositions(BlockPos var1, Direction var2, RandomSource var3, BlockPos var4) {
      BlockPos var5 = var1.below();
      BlockPos var6 = var1.relative(var2);
      int var7 = var1.distManhattan(var4);
      int var8 = this.mangroveRootPlacement.maxRootWidth();
      float var9 = this.mangroveRootPlacement.randomSkewChance();
      if (var7 > var8 - 3 && var7 <= var8) {
         return var3.nextFloat() < var9 ? List.of(var5, var6.below()) : List.of(var5);
      } else if (var7 > var8) {
         return List.of(var5);
      } else if (var3.nextFloat() < var9) {
         return List.of(var5);
      } else {
         return var3.nextBoolean() ? List.of(var6) : List.of(var5);
      }
   }

   protected boolean canPlaceRoot(LevelSimulatedReader var1, BlockPos var2) {
      return super.canPlaceRoot(var1, var2) || var1.isStateAtPosition(var2, (var1x) -> {
         return var1x.is(this.mangroveRootPlacement.canGrowThrough());
      });
   }

   protected void placeRoot(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, TreeConfiguration var5) {
      if (var1.isStateAtPosition(var4, (var1x) -> {
         return var1x.is(this.mangroveRootPlacement.muddyRootsIn());
      })) {
         BlockState var6 = this.mangroveRootPlacement.muddyRootsProvider().getState(var3, var4);
         var2.accept(var4, this.getPotentiallyWaterloggedState(var1, var4, var6));
      } else {
         super.placeRoot(var1, var2, var3, var4, var5);
      }

   }

   protected RootPlacerType<?> type() {
      return RootPlacerType.MANGROVE_ROOT_PLACER;
   }
}
