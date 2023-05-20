package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.Fluids;

public abstract class FoliagePlacer {
   public static final Codec<FoliagePlacer> CODEC = BuiltInRegistries.FOLIAGE_PLACER_TYPE
      .byNameCodec()
      .dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
   protected final IntProvider radius;
   protected final IntProvider offset;

   protected static <P extends FoliagePlacer> P2<Mu<P>, IntProvider, IntProvider> foliagePlacerParts(Instance<P> var0) {
      return var0.group(
         IntProvider.codec(0, 16).fieldOf("radius").forGetter(var0x -> var0x.radius),
         IntProvider.codec(0, 16).fieldOf("offset").forGetter(var0x -> var0x.offset)
      );
   }

   public FoliagePlacer(IntProvider var1, IntProvider var2) {
      super();
      this.radius = var1;
      this.offset = var2;
   }

   protected abstract FoliagePlacerType<?> type();

   public void createFoliage(
      LevelSimulatedReader var1,
      FoliagePlacer.FoliageSetter var2,
      RandomSource var3,
      TreeConfiguration var4,
      int var5,
      FoliagePlacer.FoliageAttachment var6,
      int var7,
      int var8
   ) {
      this.createFoliage(var1, var2, var3, var4, var5, var6, var7, var8, this.offset(var3));
   }

   protected abstract void createFoliage(
      LevelSimulatedReader var1,
      FoliagePlacer.FoliageSetter var2,
      RandomSource var3,
      TreeConfiguration var4,
      int var5,
      FoliagePlacer.FoliageAttachment var6,
      int var7,
      int var8,
      int var9
   );

   public abstract int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3);

   public int foliageRadius(RandomSource var1, int var2) {
      return this.radius.sample(var1);
   }

   private int offset(RandomSource var1) {
      return this.offset.sample(var1);
   }

   protected abstract boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6);

   protected boolean shouldSkipLocationSigned(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      int var7;
      int var8;
      if (var6) {
         var7 = Math.min(Math.abs(var2), Math.abs(var2 - 1));
         var8 = Math.min(Math.abs(var4), Math.abs(var4 - 1));
      } else {
         var7 = Math.abs(var2);
         var8 = Math.abs(var4);
      }

      return this.shouldSkipLocation(var1, var7, var3, var8, var5, var6);
   }

   protected void placeLeavesRow(
      LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, BlockPos var5, int var6, int var7, boolean var8
   ) {
      int var9 = var8 ? 1 : 0;
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

      for(int var11 = -var6; var11 <= var6 + var9; ++var11) {
         for(int var12 = -var6; var12 <= var6 + var9; ++var12) {
            if (!this.shouldSkipLocationSigned(var3, var11, var7, var12, var6, var8)) {
               var10.setWithOffset(var5, var11, var7, var12);
               tryPlaceLeaf(var1, var2, var3, var4, var10);
            }
         }
      }
   }

   protected final void placeLeavesRowWithHangingLeavesBelow(
      LevelSimulatedReader var1,
      FoliagePlacer.FoliageSetter var2,
      RandomSource var3,
      TreeConfiguration var4,
      BlockPos var5,
      int var6,
      int var7,
      boolean var8,
      float var9,
      float var10
   ) {
      this.placeLeavesRow(var1, var2, var3, var4, var5, var6, var7, var8);
      int var11 = var8 ? 1 : 0;
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for(Direction var14 : Direction.Plane.HORIZONTAL) {
         Direction var15 = var14.getClockWise();
         int var16 = var15.getAxisDirection() == Direction.AxisDirection.POSITIVE ? var6 + var11 : var6;
         var12.setWithOffset(var5, 0, var7 - 1, 0).move(var15, var16).move(var14, -var6);
         int var17 = -var6;

         while(var17 < var6 + var11) {
            boolean var18 = var2.isSet(var12.move(Direction.UP));
            var12.move(Direction.DOWN);
            if (var18 && !(var3.nextFloat() > var9) && tryPlaceLeaf(var1, var2, var3, var4, var12) && !(var3.nextFloat() > var10)) {
               tryPlaceLeaf(var1, var2, var3, var4, var12.move(Direction.DOWN));
               var12.move(Direction.UP);
            }

            ++var17;
            var12.move(var14);
         }
      }
   }

   protected static boolean tryPlaceLeaf(LevelSimulatedReader var0, FoliagePlacer.FoliageSetter var1, RandomSource var2, TreeConfiguration var3, BlockPos var4) {
      if (!TreeFeature.validTreePos(var0, var4)) {
         return false;
      } else {
         BlockState var5 = var3.foliageProvider.getState(var2, var4);
         if (var5.hasProperty(BlockStateProperties.WATERLOGGED)) {
            var5 = var5.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(var0.isFluidAtPosition(var4, var0x -> var0x.isSourceOfType(Fluids.WATER))));
         }

         var1.set(var4, var5);
         return true;
      }
   }

   public static final class FoliageAttachment {
      private final BlockPos pos;
      private final int radiusOffset;
      private final boolean doubleTrunk;

      public FoliageAttachment(BlockPos var1, int var2, boolean var3) {
         super();
         this.pos = var1;
         this.radiusOffset = var2;
         this.doubleTrunk = var3;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public int radiusOffset() {
         return this.radiusOffset;
      }

      public boolean doubleTrunk() {
         return this.doubleTrunk;
      }
   }

   public interface FoliageSetter {
      void set(BlockPos var1, BlockState var2);

      boolean isSet(BlockPos var1);
   }
}
