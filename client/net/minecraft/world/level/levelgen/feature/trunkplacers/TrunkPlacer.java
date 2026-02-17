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
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
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

   protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> trunkPlacerParts(final RecordCodecBuilder.Instance<P> instance) {
      return instance.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((p) -> p.baseHeight), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((p) -> p.heightRandA), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((p) -> p.heightRandB));
   }

   public TrunkPlacer(final int baseHeight, final int heightRandA, final int heightRandB) {
      super();
      this.baseHeight = baseHeight;
      this.heightRandA = heightRandA;
      this.heightRandB = heightRandB;
   }

   protected abstract TrunkPlacerType<?> type();

   public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final int treeHeight, final BlockPos origin, final TreeConfiguration config);

   public int getBaseHeight() {
      return this.baseHeight;
   }

   public int getTreeHeight(final RandomSource random) {
      return this.baseHeight + random.nextInt(this.heightRandA + 1) + random.nextInt(this.heightRandB + 1);
   }

   protected static void placeBelowTrunkBlock(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final BlockPos pos, final TreeConfiguration config) {
      BlockState blockBelowTrunk = config.belowTrunkProvider.getState(level, random, pos);
      if (blockBelowTrunk != null) {
         trunkSetter.accept(pos, blockBelowTrunk);
      }

   }

   protected boolean placeLog(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final BlockPos pos, final TreeConfiguration config) {
      return this.placeLog(level, trunkSetter, random, pos, config, Function.identity());
   }

   protected boolean placeLog(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final BlockPos pos, final TreeConfiguration config, final Function<BlockState, BlockState> stateModifier) {
      if (this.validTreePos(level, pos)) {
         trunkSetter.accept(pos, (BlockState)stateModifier.apply(config.trunkProvider.getState(random, pos)));
         return true;
      } else {
         return false;
      }
   }

   protected void placeLogIfFree(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final BlockPos.MutableBlockPos pos, final TreeConfiguration config) {
      if (this.isFree(level, pos)) {
         this.placeLog(level, trunkSetter, random, pos, config);
      }

   }

   protected boolean validTreePos(final WorldGenLevel level, final BlockPos pos) {
      return TreeFeature.validTreePos(level, pos);
   }

   public boolean isFree(final WorldGenLevel level, final BlockPos pos) {
      return this.validTreePos(level, pos) || level.isStateAtPosition(pos, (state) -> state.is(BlockTags.LOGS));
   }

   static {
      CODEC = BuiltInRegistries.TRUNK_PLACER_TYPE.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
   }
}
