package net.minecraft.world.level.block.grower;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import org.jspecify.annotations.Nullable;

public final class TreeGrower {
   private static final Map<String, TreeGrower> GROWERS = new Object2ObjectArrayMap();
   public static final Codec<TreeGrower> CODEC;
   public static final TreeGrower OAK;
   public static final TreeGrower SPRUCE;
   public static final TreeGrower MANGROVE;
   public static final TreeGrower AZALEA;
   public static final TreeGrower BIRCH;
   public static final TreeGrower JUNGLE;
   public static final TreeGrower ACACIA;
   public static final TreeGrower CHERRY;
   public static final TreeGrower DARK_OAK;
   public static final TreeGrower PALE_OAK;
   public static final TreeGrower POPLAR;
   private final String name;
   private final WeightedList<ResourceKey<Feature>> trees;
   private final WeightedList<ResourceKey<Feature>> megaTrees;
   private final WeightedList<ResourceKey<Feature>> flowerTrees;
   private final @Nullable ResourceKey<Feature> shortestTreeType;

   public TreeGrower(final String name, final WeightedList<ResourceKey<Feature>> trees, final WeightedList<ResourceKey<Feature>> megaTrees, final WeightedList<ResourceKey<Feature>> flowerTrees, final @Nullable ResourceKey<Feature> shortestTreeType) {
      super();
      this.name = name;
      this.trees = trees;
      this.megaTrees = megaTrees;
      this.flowerTrees = flowerTrees;
      this.shortestTreeType = shortestTreeType;
      GROWERS.put(name, this);
   }

   private @Nullable ResourceKey<Feature> getConfiguredFeature(final RandomSource random, final boolean hasFlowers) {
      return hasFlowers && !this.flowerTrees.isEmpty() ? (ResourceKey)this.flowerTrees.getRandom(random).orElse((Object)null) : (ResourceKey)this.trees.getRandom(random).orElse((Object)null);
   }

   private @Nullable ResourceKey<Feature> getConfiguredMegaFeature(final RandomSource random) {
      return (ResourceKey)this.megaTrees.getRandom(random).orElse((Object)null);
   }

   public boolean growTree(final ServerLevel level, final ChunkGenerator generator, final BlockPos pos, final BlockState state, final RandomSource random) {
      ResourceKey<Feature> megaFeatureKey = this.getConfiguredMegaFeature(random);
      if (megaFeatureKey != null) {
         Holder<Feature> featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(megaFeatureKey).orElse((Object)null);
         if (featureHolder != null) {
            Optional<TwoByTwoSaplingPos> twoByTwoSaplingPos = findTwoByTwoSaplingPos(level, state, pos);
            if (twoByTwoSaplingPos.isPresent()) {
               int dx = ((TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).offsetX();
               int dz = ((TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).offsetZ();
               List<Pair<BlockState, BlockPos>> groundLevelSurroundingBlocks = ((TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).groundLevelSurroundingBlocks();
               Feature feature = featureHolder.value();
               removeSaplings(level, groundLevelSurroundingBlocks);
               if (feature.place(level, generator, random, pos.offset(dx, 0, dz))) {
                  return true;
               }

               resetSaplings(level, groundLevelSurroundingBlocks);
               return false;
            }
         }
      }

      ResourceKey<Feature> featureKey = this.getConfiguredFeature(random, this.hasFlowers(level, pos));
      if (featureKey == null) {
         return false;
      } else {
         Holder<Feature> featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(featureKey).orElse((Object)null);
         if (featureHolder == null) {
            return false;
         } else {
            Feature feature = featureHolder.value();
            removeSapling(level, pos);
            if (feature.place(level, generator, random, pos)) {
               return true;
            } else {
               resetSaplings(level, List.of(Pair.of(state, pos)));
               return false;
            }
         }
      }
   }

   private static List<Pair<BlockState, BlockPos>> getSurroundingBlockStates(final ServerLevel level, final BlockPos pos, final int dx, final int dz) {
      return List.of(Pair.of(level.getBlockState(pos.offset(dx, 0, dz)), pos.offset(dx, 0, dz)), Pair.of(level.getBlockState(pos.offset(dx + 1, 0, dz)), pos.offset(dx + 1, 0, dz)), Pair.of(level.getBlockState(pos.offset(dx, 0, dz + 1)), pos.offset(dx, 0, dz + 1)), Pair.of(level.getBlockState(pos.offset(dx + 1, 0, dz + 1)), pos.offset(dx + 1, 0, dz + 1)));
   }

   private static void removeSaplings(final ServerLevel level, final List<Pair<BlockState, BlockPos>> saplingBlocks) {
      for(Pair<BlockState, BlockPos> saplingBlock : saplingBlocks) {
         BlockPos saplingPosition = (BlockPos)saplingBlock.getSecond();
         removeSapling(level, saplingPosition);
      }

   }

   private static void removeSapling(final ServerLevel level, final BlockPos saplingPosition) {
      BlockState emptyBlock = level.getFluidState(saplingPosition).createLegacyBlock();
      level.setBlock(saplingPosition, emptyBlock, 818);
   }

   private static void resetSaplings(final ServerLevel level, final List<Pair<BlockState, BlockPos>> saplingBlocks) {
      for(Pair<BlockState, BlockPos> saplingBlock : saplingBlocks) {
         level.setBlock((BlockPos)saplingBlock.getSecond(), (BlockState)saplingBlock.getFirst(), 260);
      }

   }

   private static boolean isTwoByTwoSapling(final BlockState state, final List<Pair<BlockState, BlockPos>> surroundingBlocks) {
      Block block = state.getBlock();

      for(Pair<BlockState, BlockPos> surroundingBlock : surroundingBlocks) {
         BlockState surroundingBlockState = (BlockState)surroundingBlock.getFirst();
         if (!surroundingBlockState.is(block)) {
            return false;
         }
      }

      return true;
   }

   private boolean hasFlowers(final LevelAccessor level, final BlockPos pos) {
      for(BlockPos p : BlockPos.MutableBlockPos.betweenClosed(pos.below().north(2).west(2), pos.above().south(2).east(2))) {
         if (level.getBlockState(p).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }

   public OptionalInt getMinimumHeight(final ServerLevel level) {
      ResourceKey<Feature> featureKey = this.shortestTreeType;
      if (featureKey == null) {
         return OptionalInt.empty();
      } else {
         Holder<Feature> featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(featureKey).orElse((Object)null);
         if (featureHolder != null) {
            Object var5 = featureHolder.value();
            if (var5 instanceof TreeFeature) {
               TreeFeature treeFeature = (TreeFeature)var5;
               return OptionalInt.of(treeFeature.trunkPlacer().getBaseHeight());
            }
         }

         return OptionalInt.empty();
      }
   }

   public boolean canGrow(final ServerLevel level, final BlockPos pos, final BlockState state) {
      ResourceKey<Feature> featureKey = this.getConfiguredFeature(level.getRandom(), this.hasFlowers(level, pos));
      ResourceKey<Feature> megaFeatureKey = this.getConfiguredMegaFeature(level.getRandom());
      return featureKey == null && megaFeatureKey != null ? findTwoByTwoSaplingPos(level, state, pos).isPresent() : true;
   }

   private static Optional<TwoByTwoSaplingPos> findTwoByTwoSaplingPos(final ServerLevel level, final BlockState state, final BlockPos pos) {
      for(int dx = 0; dx >= -1; --dx) {
         for(int dz = 0; dz >= -1; --dz) {
            List<Pair<BlockState, BlockPos>> groundLevelSurroundingBlocks = getSurroundingBlockStates(level, pos, dx, dz);
            if (isTwoByTwoSapling(state, groundLevelSurroundingBlocks)) {
               return Optional.of(new TwoByTwoSaplingPos(dx, dz, groundLevelSurroundingBlocks));
            }
         }
      }

      return Optional.empty();
   }

   static {
      Function var10000 = (g) -> g.name;
      Map var10001 = GROWERS;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      OAK = new TreeGrower("oak", WeightedList.of(new Weighted(TreeFeatures.OAK, 9), new Weighted(TreeFeatures.FANCY_OAK, 1)), WeightedList.of(), WeightedList.of(new Weighted(TreeFeatures.OAK_BEES_005, 9), new Weighted(TreeFeatures.FANCY_OAK_BEES_005, 1)), TreeFeatures.OAK);
      SPRUCE = new TreeGrower("spruce", WeightedList.of(TreeFeatures.SPRUCE), WeightedList.of(new Weighted(TreeFeatures.MEGA_SPRUCE, 1), new Weighted(TreeFeatures.MEGA_PINE, 1)), WeightedList.of(), TreeFeatures.SPRUCE);
      MANGROVE = new TreeGrower("mangrove", WeightedList.of(new Weighted(TreeFeatures.MANGROVE, 15), new Weighted(TreeFeatures.TALL_MANGROVE, 85)), WeightedList.of(), WeightedList.of(), TreeFeatures.MANGROVE);
      AZALEA = new TreeGrower("azalea", WeightedList.of(TreeFeatures.AZALEA_TREE), WeightedList.of(), WeightedList.of(), TreeFeatures.AZALEA_TREE);
      BIRCH = new TreeGrower("birch", WeightedList.of(TreeFeatures.BIRCH), WeightedList.of(), WeightedList.of(TreeFeatures.BIRCH_BEES_005), TreeFeatures.BIRCH);
      JUNGLE = new TreeGrower("jungle", WeightedList.of(TreeFeatures.JUNGLE_TREE_NO_VINE), WeightedList.of(TreeFeatures.MEGA_JUNGLE_TREE), WeightedList.of(), TreeFeatures.JUNGLE_TREE_NO_VINE);
      ACACIA = new TreeGrower("acacia", WeightedList.of(TreeFeatures.ACACIA), WeightedList.of(), WeightedList.of(), TreeFeatures.ACACIA);
      CHERRY = new TreeGrower("cherry", WeightedList.of(TreeFeatures.CHERRY), WeightedList.of(), WeightedList.of(TreeFeatures.CHERRY_BEES_005), TreeFeatures.CHERRY);
      DARK_OAK = new TreeGrower("dark_oak", WeightedList.of(), WeightedList.of(TreeFeatures.DARK_OAK), WeightedList.of(), (ResourceKey)null);
      PALE_OAK = new TreeGrower("pale_oak", WeightedList.of(), WeightedList.of(TreeFeatures.PALE_OAK_BONEMEAL), WeightedList.of(), (ResourceKey)null);
      POPLAR = new TreeGrower("poplar", WeightedList.of(new Weighted(TreeFeatures.RED_POPLAR, 1), new Weighted(TreeFeatures.ORANGE_POPLAR, 1), new Weighted(TreeFeatures.YELLOW_POPLAR, 1)), WeightedList.of(), WeightedList.of(), TreeFeatures.RED_POPLAR);
   }

   private static record TwoByTwoSaplingPos(int offsetX, int offsetZ, List<Pair<BlockState, BlockPos>> groundLevelSurroundingBlocks) {
      private TwoByTwoSaplingPos {
         super();
      }
   }
}
