package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluids;

public class PlaceOnGroundDecorator extends TreeDecorator {
   public static final MapCodec<PlaceOnGroundDecorator> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter((var0x) -> var0x.tries), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").orElse(2).forGetter((var0x) -> var0x.radius), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("height").orElse(1).forGetter((var0x) -> var0x.height), BlockStateProvider.CODEC.fieldOf("block_state_provider").forGetter((var0x) -> var0x.blockStateProvider)).apply(var0, PlaceOnGroundDecorator::new));
   private final int tries;
   private final int radius;
   private final int height;
   private final BlockStateProvider blockStateProvider;

   public PlaceOnGroundDecorator(int var1, int var2, int var3, BlockStateProvider var4) {
      super();
      this.tries = var1;
      this.radius = var2;
      this.height = var3;
      this.blockStateProvider = var4;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.PLACE_ON_GROUND;
   }

   public void place(TreeDecorator.Context var1) {
      List var2 = TreeFeature.getLowestTrunkOrRootOfTree(var1);
      if (!var2.isEmpty()) {
         BlockPos var3 = (BlockPos)var2.getFirst();
         int var4 = var3.getY();
         int var5 = var3.getX();
         int var6 = var3.getX();
         int var7 = var3.getZ();
         int var8 = var3.getZ();

         for(BlockPos var10 : var2) {
            if (var10.getY() == var4) {
               var5 = Math.min(var5, var10.getX());
               var6 = Math.max(var6, var10.getX());
               var7 = Math.min(var7, var10.getZ());
               var8 = Math.max(var8, var10.getZ());
            }
         }

         RandomSource var13 = var1.random();
         BoundingBox var14 = (new BoundingBox(var5, var4, var7, var6, var4, var8)).inflatedBy(this.radius, this.height, this.radius);
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

         for(int var12 = 0; var12 < this.tries; ++var12) {
            var11.set(var13.nextIntBetweenInclusive(var14.minX(), var14.maxX()), var13.nextIntBetweenInclusive(var14.minY(), var14.maxY()), var13.nextIntBetweenInclusive(var14.minZ(), var14.maxZ()));
            this.placeBlockAt(var1, var11);
         }

      }
   }

   private void placeBlockAt(TreeDecorator.Context var1, BlockPos var2) {
      BlockPos var3 = var2.above();
      if ((TreeFeature.validTreePos(var1.level(), var3) || TreeFeature.isVine(var1.level(), var3)) && TreeFeature.isGrassOrDirt(var1.level(), var3.below()) && !var1.level().isFluidAtPosition(var3, (var0) -> var0.is(Fluids.WATER))) {
         var1.setBlock(var3, this.blockStateProvider.getState(var1.random(), var2));
      }

   }
}
