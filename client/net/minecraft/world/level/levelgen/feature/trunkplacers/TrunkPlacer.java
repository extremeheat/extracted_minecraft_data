package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public abstract class TrunkPlacer {
   public static final Codec<TrunkPlacer> CODEC;
   private static final int MAX_BASE_HEIGHT = 32;
   private static final int MAX_RAND = 24;
   public static final int MAX_HEIGHT = 80;
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

   public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, int var4, BlockPos var5, TreeConfiguration var6);

   public int getTreeHeight(Random var1) {
      return this.baseHeight + var1.nextInt(this.heightRandA + 1) + var1.nextInt(this.heightRandB + 1);
   }

   private static boolean isDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return Feature.isDirt(var0x) && !var0x.is(Blocks.GRASS_BLOCK) && !var0x.is(Blocks.MYCELIUM);
      });
   }

   protected static void setDirtAt(LevelSimulatedReader var0, BiConsumer<BlockPos, BlockState> var1, Random var2, BlockPos var3, TreeConfiguration var4) {
      if (var4.forceDirt || !isDirt(var0, var3)) {
         var1.accept(var3, var4.dirtProvider.getState(var2, var3));
      }

   }

   protected static boolean placeLog(LevelSimulatedReader var0, BiConsumer<BlockPos, BlockState> var1, Random var2, BlockPos var3, TreeConfiguration var4) {
      return placeLog(var0, var1, var2, var3, var4, Function.identity());
   }

   protected static boolean placeLog(LevelSimulatedReader var0, BiConsumer<BlockPos, BlockState> var1, Random var2, BlockPos var3, TreeConfiguration var4, Function<BlockState, BlockState> var5) {
      if (TreeFeature.validTreePos(var0, var3)) {
         var1.accept(var3, (BlockState)var5.apply(var4.trunkProvider.getState(var2, var3)));
         return true;
      } else {
         return false;
      }
   }

   protected static void placeLogIfFree(LevelSimulatedReader var0, BiConsumer<BlockPos, BlockState> var1, Random var2, BlockPos.MutableBlockPos var3, TreeConfiguration var4) {
      if (TreeFeature.isFree(var0, var3)) {
         placeLog(var0, var1, var2, var3, var4);
      }

   }

   static {
      CODEC = Registry.TRUNK_PLACER_TYPES.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
   }
}
