package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class FoliagePlacer {
   public static final Codec<FoliagePlacer> CODEC;
   protected final UniformInt radius;
   protected final UniformInt offset;

   protected static <P extends FoliagePlacer> P2<Mu<P>, UniformInt, UniformInt> foliagePlacerParts(Instance<P> var0) {
      return var0.group(UniformInt.codec(0, 8, 8).fieldOf("radius").forGetter((var0x) -> {
         return var0x.radius;
      }), UniformInt.codec(0, 8, 8).fieldOf("offset").forGetter((var0x) -> {
         return var0x.offset;
      }));
   }

   public FoliagePlacer(UniformInt var1, UniformInt var2) {
      super();
      this.radius = var1;
      this.offset = var2;
   }

   protected abstract FoliagePlacerType<?> type();

   public void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, BoundingBox var9) {
      this.createFoliage(var1, var2, var3, var4, var5, var6, var7, var8, this.offset(var2), var9);
   }

   protected abstract void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliagePlacer.FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10);

   public abstract int foliageHeight(Random var1, int var2, TreeConfiguration var3);

   public int foliageRadius(Random var1, int var2) {
      return this.radius.sample(var1);
   }

   private int offset(Random var1) {
      return this.offset.sample(var1);
   }

   protected abstract boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6);

   protected boolean shouldSkipLocationSigned(Random var1, int var2, int var3, int var4, int var5, boolean var6) {
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

   protected void placeLeavesRow(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, BlockPos var4, int var5, Set<BlockPos> var6, int var7, boolean var8, BoundingBox var9) {
      int var10 = var8 ? 1 : 0;
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for(int var12 = -var5; var12 <= var5 + var10; ++var12) {
         for(int var13 = -var5; var13 <= var5 + var10; ++var13) {
            if (!this.shouldSkipLocationSigned(var2, var12, var7, var13, var5, var8)) {
               var11.setWithOffset(var4, var12, var7, var13);
               if (TreeFeature.validTreePos(var1, var11)) {
                  var1.setBlock(var11, var3.leavesProvider.getState(var2, var11), 19);
                  var9.expand(new BoundingBox(var11, var11));
                  var6.add(var11.immutable());
               }
            }
         }
      }

   }

   static {
      CODEC = Registry.FOLIAGE_PLACER_TYPES.dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
   }

   public static final class FoliageAttachment {
      private final BlockPos foliagePos;
      private final int radiusOffset;
      private final boolean doubleTrunk;

      public FoliageAttachment(BlockPos var1, int var2, boolean var3) {
         super();
         this.foliagePos = var1;
         this.radiusOffset = var2;
         this.doubleTrunk = var3;
      }

      public BlockPos foliagePos() {
         return this.foliagePos;
      }

      public int radiusOffset() {
         return this.radiusOffset;
      }

      public boolean doubleTrunk() {
         return this.doubleTrunk;
      }
   }
}
