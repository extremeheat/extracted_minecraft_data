package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class CherryTrunkPlacer extends TrunkPlacer {
   private static final Codec<UniformInt> BRANCH_START_CODEC;
   public static final MapCodec<CherryTrunkPlacer> CODEC;
   private final IntProvider branchCount;
   private final IntProvider branchHorizontalLength;
   private final UniformInt branchStartOffsetFromTop;
   private final UniformInt secondBranchStartOffsetFromTop;
   private final IntProvider branchEndOffsetFromTop;

   public CherryTrunkPlacer(int var1, int var2, int var3, IntProvider var4, IntProvider var5, UniformInt var6, IntProvider var7) {
      super(var1, var2, var3);
      this.branchCount = var4;
      this.branchHorizontalLength = var5;
      this.branchStartOffsetFromTop = var6;
      this.secondBranchStartOffsetFromTop = UniformInt.of(var6.getMinValue(), var6.getMaxValue() - 1);
      this.branchEndOffsetFromTop = var7;
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.CHERRY_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      setDirtAt(var1, var2, var3, var5.below(), var6);
      int var7 = Math.max(0, var4 - 1 + this.branchStartOffsetFromTop.sample(var3));
      int var8 = Math.max(0, var4 - 1 + this.secondBranchStartOffsetFromTop.sample(var3));
      if (var8 >= var7) {
         ++var8;
      }

      int var9 = this.branchCount.sample(var3);
      boolean var10 = var9 == 3;
      boolean var11 = var9 >= 2;
      int var12;
      if (var10) {
         var12 = var4;
      } else if (var11) {
         var12 = Math.max(var7, var8) + 1;
      } else {
         var12 = var7 + 1;
      }

      for(int var13 = 0; var13 < var12; ++var13) {
         this.placeLog(var1, var2, var3, var5.above(var13), var6);
      }

      ArrayList var17 = new ArrayList();
      if (var10) {
         var17.add(new FoliagePlacer.FoliageAttachment(var5.above(var12), 0, false));
      }

      BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();
      Direction var15 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
      Function var16 = (var1x) -> {
         return (BlockState)var1x.trySetValue(RotatedPillarBlock.AXIS, var15.getAxis());
      };
      var17.add(this.generateBranch(var1, var2, var3, var4, var5, var6, var16, var15, var7, var7 < var12 - 1, var14));
      if (var11) {
         var17.add(this.generateBranch(var1, var2, var3, var4, var5, var6, var16, var15.getOpposite(), var8, var8 < var12 - 1, var14));
      }

      return var17;
   }

   private FoliagePlacer.FoliageAttachment generateBranch(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6, Function<BlockState, BlockState> var7, Direction var8, int var9, boolean var10, BlockPos.MutableBlockPos var11) {
      var11.set(var5).move(Direction.UP, var9);
      int var12 = var4 - 1 + this.branchEndOffsetFromTop.sample(var3);
      boolean var13 = var10 || var12 < var9;
      int var14 = this.branchHorizontalLength.sample(var3) + (var13 ? 1 : 0);
      BlockPos var15 = var5.relative(var8, var14).above(var12);
      int var16 = var13 ? 2 : 1;

      for(int var17 = 0; var17 < var16; ++var17) {
         this.placeLog(var1, var2, var3, var11.move(var8), var6, var7);
      }

      Direction var21 = var15.getY() > var11.getY() ? Direction.UP : Direction.DOWN;

      while(true) {
         int var18 = var11.distManhattan(var15);
         if (var18 == 0) {
            return new FoliagePlacer.FoliageAttachment(var15.above(), 0, false);
         }

         float var19 = (float)Math.abs(var15.getY() - var11.getY()) / (float)var18;
         boolean var20 = var3.nextFloat() < var19;
         var11.move(var20 ? var21 : var8);
         this.placeLog(var1, var2, var3, var11, var6, var20 ? Function.identity() : var7);
      }
   }

   static {
      BRANCH_START_CODEC = UniformInt.CODEC.codec().validate((var0) -> {
         return var0.getMaxValue() - var0.getMinValue() < 1 ? DataResult.error(() -> {
            return "Need at least 2 blocks variation for the branch starts to fit both branches";
         }) : DataResult.success(var0);
      });
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return trunkPlacerParts(var0).and(var0.group(IntProvider.codec(1, 3).fieldOf("branch_count").forGetter((var0x) -> {
            return var0x.branchCount;
         }), IntProvider.codec(2, 16).fieldOf("branch_horizontal_length").forGetter((var0x) -> {
            return var0x.branchHorizontalLength;
         }), IntProvider.validateCodec(-16, 0, BRANCH_START_CODEC).fieldOf("branch_start_offset_from_top").forGetter((var0x) -> {
            return var0x.branchStartOffsetFromTop;
         }), IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top").forGetter((var0x) -> {
            return var0x.branchEndOffsetFromTop;
         }))).apply(var0, CherryTrunkPlacer::new);
      });
   }
}
