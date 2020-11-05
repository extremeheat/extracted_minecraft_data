package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class TrunkPlacer {
   public static final Codec<TrunkPlacer> CODEC;
   protected final int baseHeight;
   protected final int heightRandA;
   protected final int heightRandB;

   protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> trunkPlacerParts(Instance<P> var0) {
      return var0.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((var0x) -> {
         return var0x.baseHeight;
      }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((var0x) -> {
         return var0x.heightRandA;
      }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((var0x) -> {
         return var0x.heightRandB;
      }));
   }

   public TrunkPlacer(int var1, int var2, int var3) {
      super();
      this.baseHeight = var1;
      this.heightRandA = var2;
      this.heightRandB = var3;
   }

   protected abstract TrunkPlacerType<?> type();

   public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7);

   public int getTreeHeight(Random var1) {
      return this.baseHeight + var1.nextInt(this.heightRandA + 1) + var1.nextInt(this.heightRandB + 1);
   }

   protected static void setBlock(LevelWriter var0, BlockPos var1, BlockState var2, BoundingBox var3) {
      TreeFeature.setBlockKnownShape(var0, var1, var2);
      var3.expand(new BoundingBox(var1, var1));
   }

   private static boolean isDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return Feature.isDirt(var0x) && !var0x.is(Blocks.GRASS_BLOCK) && !var0x.is(Blocks.MYCELIUM);
      });
   }

   protected static void setDirtAt(LevelSimulatedRW var0, BlockPos var1) {
      if (!isDirt(var0, var1)) {
         TreeFeature.setBlockKnownShape(var0, var1, Blocks.DIRT.defaultBlockState());
      }

   }

   protected static boolean placeLog(LevelSimulatedRW var0, Random var1, BlockPos var2, Set<BlockPos> var3, BoundingBox var4, TreeConfiguration var5) {
      if (TreeFeature.validTreePos(var0, var2)) {
         setBlock(var0, var2, var5.trunkProvider.getState(var1, var2), var4);
         var3.add(var2.immutable());
         return true;
      } else {
         return false;
      }
   }

   protected static void placeLogIfFree(LevelSimulatedRW var0, Random var1, BlockPos.MutableBlockPos var2, Set<BlockPos> var3, BoundingBox var4, TreeConfiguration var5) {
      if (TreeFeature.isFree(var0, var2)) {
         placeLog(var0, var1, var2, var3, var4, var5);
      }

   }

   static {
      CODEC = Registry.TRUNK_PLACER_TYPES.dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
   }
}
