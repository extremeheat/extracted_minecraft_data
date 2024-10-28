package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class UpwardsBranchingTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<UpwardsBranchingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).and(var0.group(IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter((var0x) -> {
         return var0x.extraBranchSteps;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter((var0x) -> {
         return var0x.placeBranchPerLogProbability;
      }), IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter((var0x) -> {
         return var0x.extraBranchLength;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter((var0x) -> {
         return var0x.canGrowThrough;
      }))).apply(var0, UpwardsBranchingTrunkPlacer::new);
   });
   private final IntProvider extraBranchSteps;
   private final float placeBranchPerLogProbability;
   private final IntProvider extraBranchLength;
   private final HolderSet<Block> canGrowThrough;

   public UpwardsBranchingTrunkPlacer(int var1, int var2, int var3, IntProvider var4, float var5, IntProvider var6, HolderSet<Block> var7) {
      super(var1, var2, var3);
      this.extraBranchSteps = var4;
      this.placeBranchPerLogProbability = var5;
      this.extraBranchLength = var6;
      this.canGrowThrough = var7;
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.UPWARDS_BRANCHING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      ArrayList var7 = Lists.newArrayList();
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < var4; ++var9) {
         int var10 = var5.getY() + var9;
         if (this.placeLog(var1, var2, var3, var8.set(var5.getX(), var10, var5.getZ()), var6) && var9 < var4 - 1 && var3.nextFloat() < this.placeBranchPerLogProbability) {
            Direction var11 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
            int var12 = this.extraBranchLength.sample(var3);
            int var13 = Math.max(0, var12 - this.extraBranchLength.sample(var3) - 1);
            int var14 = this.extraBranchSteps.sample(var3);
            this.placeBranch(var1, var2, var3, var4, var6, var7, var8, var10, var11, var13, var14);
         }

         if (var9 == var4 - 1) {
            var7.add(new FoliagePlacer.FoliageAttachment(var8.set(var5.getX(), var10 + 1, var5.getZ()), 0, false));
         }
      }

      return var7;
   }

   private void placeBranch(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, TreeConfiguration var5, List<FoliagePlacer.FoliageAttachment> var6, BlockPos.MutableBlockPos var7, int var8, Direction var9, int var10, int var11) {
      int var12 = var8 + var10;
      int var13 = var7.getX();
      int var14 = var7.getZ();

      for(int var15 = var10; var15 < var4 && var11 > 0; --var11) {
         if (var15 >= 1) {
            int var16 = var8 + var15;
            var13 += var9.getStepX();
            var14 += var9.getStepZ();
            var12 = var16;
            if (this.placeLog(var1, var2, var3, var7.set(var13, var16, var14), var5)) {
               var12 = var16 + 1;
            }

            var6.add(new FoliagePlacer.FoliageAttachment(var7.immutable(), 0, false));
         }

         ++var15;
      }

      if (var12 - var8 > 1) {
         BlockPos var17 = new BlockPos(var13, var12, var14);
         var6.add(new FoliagePlacer.FoliageAttachment(var17, 0, false));
         var6.add(new FoliagePlacer.FoliageAttachment(var17.below(2), 0, false));
      }

   }

   protected boolean validTreePos(LevelSimulatedReader var1, BlockPos var2) {
      return super.validTreePos(var1, var2) || var1.isStateAtPosition(var2, (var1x) -> {
         return var1x.is(this.canGrowThrough);
      });
   }
}
