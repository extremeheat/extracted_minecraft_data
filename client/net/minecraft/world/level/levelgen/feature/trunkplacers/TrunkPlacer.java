package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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

   protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> trunkPlacerParts(RecordCodecBuilder.Instance<P> var0) {
      return var0.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((var0x) -> var0x.baseHeight), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((var0x) -> var0x.heightRandA), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((var0x) -> var0x.heightRandB));
   }

   public TrunkPlacer(int var1, int var2, int var3) {
      super();
      this.baseHeight = var1;
      this.heightRandA = var2;
      this.heightRandB = var3;
   }

   protected abstract TrunkPlacerType<?> type();

   public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6);

   public int getTreeHeight(RandomSource var1) {
      return this.baseHeight + var1.nextInt(this.heightRandA + 1) + var1.nextInt(this.heightRandB + 1);
   }

   private static boolean isDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> Feature.isDirt(var0x) && !var0x.is(Blocks.GRASS_BLOCK) && !var0x.is(Blocks.MYCELIUM));
   }

   protected static void setDirtAt(LevelSimulatedReader var0, BiConsumer<BlockPos, BlockState> var1, RandomSource var2, BlockPos var3, TreeConfiguration var4) {
      if (var4.forceDirt || !isDirt(var0, var3)) {
         var1.accept(var3, var4.dirtProvider.getState(var2, var3));
      }

   }

   protected boolean placeLog(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, TreeConfiguration var5) {
      return this.placeLog(var1, var2, var3, var4, var5, Function.identity());
   }

   protected boolean placeLog(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, TreeConfiguration var5, Function<BlockState, BlockState> var6) {
      if (this.validTreePos(var1, var4)) {
         var2.accept(var4, (BlockState)var6.apply(var5.trunkProvider.getState(var3, var4)));
         return true;
      } else {
         return false;
      }
   }

   protected void placeLogIfFree(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos.MutableBlockPos var4, TreeConfiguration var5) {
      if (this.isFree(var1, var4)) {
         this.placeLog(var1, var2, var3, var4, var5);
      }

   }

   protected boolean validTreePos(LevelSimulatedReader var1, BlockPos var2) {
      return TreeFeature.validTreePos(var1, var2);
   }

   public boolean isFree(LevelSimulatedReader var1, BlockPos var2) {
      return this.validTreePos(var1, var2) || var1.isStateAtPosition(var2, (var0) -> var0.is(BlockTags.LOGS));
   }

   static {
      CODEC = BuiltInRegistries.TRUNK_PLACER_TYPE.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
   }
}
