package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public record TreeFeature(Holder<BlockStateProvider> trunkProvider, TrunkPlacer trunkPlacer, Holder<BlockStateProvider> foliageProvider, FoliagePlacer foliagePlacer, Optional<RootPlacer> rootPlacer, FeatureSize minimumSize, List<TreeDecorator> decorators, boolean ignoreVines, Holder<BlockStateProvider> belowTrunkProvider) implements Feature {
   public static final MapCodec<TreeFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(TreeFeature::trunkProvider), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(TreeFeature::trunkPlacer), BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(TreeFeature::foliageProvider), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(TreeFeature::foliagePlacer), RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter(TreeFeature::rootPlacer), FeatureSize.CODEC.fieldOf("minimum_size").forGetter(TreeFeature::minimumSize), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(TreeFeature::decorators), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(TreeFeature::ignoreVines), BlockStateProvider.CODEC.fieldOf("below_trunk_provider").forGetter(TreeFeature::belowTrunkProvider)).apply(i, TreeFeature::new));
   private static final @Block.UpdateFlags int BLOCK_UPDATE_FLAGS = 19;
   private static final Direction[] DIRECTIONS = Direction.values();

   public TreeFeature {
      super();
   }

   public MapCodec<TreeFeature> codec() {
      return CODEC;
   }

   public static boolean isVine(final LevelSimulatedReader level, final BlockPos pos) {
      return level.isStateAtPosition(pos, (state) -> state.is(Blocks.VINE));
   }

   public static boolean isAirOrLeaves(final LevelSimulatedReader level, final BlockPos pos) {
      return level.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.LEAVES));
   }

   private static void setBlockKnownShape(final LevelWriter level, final BlockPos pos, final BlockState blockState) {
      level.setBlock(pos, blockState, 19);
   }

   public static boolean validTreePos(final LevelSimulatedReader level, final BlockPos pos) {
      return level.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES));
   }

   private boolean doPlace(final WorldGenLevel level, final RandomSource random, final BlockPos origin, final BiConsumer<BlockPos, BlockState> rootSetter, final BiConsumer<BlockPos, BlockState> trunkSetter, final FoliagePlacer.FoliageSetter foliageSetter) {
      int treeHeight = this.trunkPlacer.getTreeHeight(random);
      int foliageHeight = this.foliagePlacer.foliageHeight(random, treeHeight, this);
      int trunkHeight = treeHeight - foliageHeight;
      int leafRadius = this.foliagePlacer.foliageRadius(random, trunkHeight);
      BlockPos trunkOrigin = (BlockPos)this.rootPlacer.map((rootPlacer) -> rootPlacer.getTrunkOrigin(origin, random)).orElse(origin);
      int minY = Math.min(origin.getY(), trunkOrigin.getY());
      int maxY = Math.max(origin.getY(), trunkOrigin.getY()) + treeHeight + 1;
      if (minY >= level.getMinY() + 1 && maxY <= level.getMaxY() + 1) {
         OptionalInt minClippedHeight = this.minimumSize.minClippedHeight();
         int clippedTreeHeight = this.getMaxFreeTreeHeight(level, treeHeight, trunkOrigin);
         if (clippedTreeHeight >= treeHeight || !minClippedHeight.isEmpty() && clippedTreeHeight >= minClippedHeight.getAsInt()) {
            if (this.rootPlacer.isPresent() && !((RootPlacer)this.rootPlacer.get()).placeRoots(level, rootSetter, random, origin, trunkOrigin, this)) {
               return false;
            } else {
               List<FoliagePlacer.FoliageAttachment> foliageAttachments = this.trunkPlacer.placeTrunk(level, trunkSetter, random, clippedTreeHeight, trunkOrigin, this);
               foliageAttachments.forEach((foliageAttachment) -> this.foliagePlacer.createFoliage(level, foliageSetter, random, this, clippedTreeHeight, foliageAttachment, foliageHeight, leafRadius));
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int getMaxFreeTreeHeight(final WorldGenLevel level, final int maxTreeHeight, final BlockPos treePos) {
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

      for(int y = 0; y <= maxTreeHeight + 1; ++y) {
         int r = this.minimumSize.getSizeAtHeight(maxTreeHeight, y);

         for(int x = -r; x <= r; ++x) {
            for(int z = -r; z <= r; ++z) {
               blockPos.setWithOffset(treePos, x, y, z);
               if (!this.trunkPlacer.isFree(level, blockPos) || !this.ignoreVines && isVine(level, blockPos)) {
                  return y - 2;
               }
            }
         }
      }

      return maxTreeHeight;
   }

   public void setBlock(final LevelWriter level, final BlockPos pos, final BlockState blockState) {
      setBlockKnownShape(level, pos, blockState);
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      Set<BlockPos> roots = new HashSet();
      Set<BlockPos> trunks = new HashSet();
      final Set<BlockPos> foliage = new HashSet();
      Set<BlockPos> decorations = new HashSet();
      BiConsumer<BlockPos, BlockState> rootSetter = (pos, state) -> {
         roots.add(pos.immutable());
         level.setBlock(pos, state, 19);
      };
      BiConsumer<BlockPos, BlockState> trunkSetter = (pos, state) -> {
         trunks.add(pos.immutable());
         level.setBlock(pos, state, 19);
      };
      FoliagePlacer.FoliageSetter foliageSetter = new FoliagePlacer.FoliageSetter() {
         {
            Objects.requireNonNull(TreeFeature.this);
         }

         public void set(final BlockPos pos, final BlockState state) {
            foliage.add(pos.immutable());
            level.setBlock(pos, state, 19);
         }

         public boolean isSet(final BlockPos pos) {
            return foliage.contains(pos);
         }
      };
      BiConsumer<BlockPos, BlockState> decorationSetter = (pos, state) -> {
         decorations.add(pos.immutable());
         level.setBlock(pos, state, 19);
      };
      boolean result = this.doPlace(level, random, origin, rootSetter, trunkSetter, foliageSetter);
      if (result && (!trunks.isEmpty() || !foliage.isEmpty())) {
         if (!this.decorators.isEmpty()) {
            TreeDecorator.Context decoratorContext = new TreeDecorator.Context(level, decorationSetter, random, trunks, foliage, roots);
            this.decorators.forEach((decorator) -> decorator.place(decoratorContext));
         }

         return (Boolean)BoundingBox.encapsulatingPositions(Iterables.concat(roots, trunks, foliage, decorations)).map((bounds) -> {
            DiscreteVoxelShape shape = updateLeaves(level, bounds, trunks, decorations, roots);
            StructureTemplate.updateShapeAtEdge(level, 3, shape, bounds.minX(), bounds.minY(), bounds.minZ());
            return true;
         }).orElse(false);
      } else {
         return false;
      }
   }

   private static DiscreteVoxelShape updateLeaves(final LevelAccessor level, final BoundingBox bounds, final Set<BlockPos> logs, final Set<BlockPos> decorations, final Set<BlockPos> roots) {
      DiscreteVoxelShape updatedShape = new BitSetDiscreteVoxelShape(bounds.getXSpan(), bounds.getYSpan(), bounds.getZSpan());
      Consumer<BlockPos> fillShape = (posx) -> updatedShape.fill(posx.getX() - bounds.minX(), posx.getY() - bounds.minY(), posx.getZ() - bounds.minZ());
      logs.forEach(fillShape);
      decorations.forEach(fillShape);
      roots.forEach(fillShape);
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
      int distance = 0;
      LongArrayList blocksToCheck = new LongArrayList(logs.size());

      for(BlockPos log : logs) {
         blocksToCheck.add(log.asLong());
      }

      LongArrayList nextBlocksToCheck = new LongArrayList();

      while(!blocksToCheck.isEmpty() || !nextBlocksToCheck.isEmpty()) {
         if (blocksToCheck.isEmpty()) {
            blocksToCheck.addAll(nextBlocksToCheck);
            nextBlocksToCheck.clear();
            ++distance;
         }

         pos.set(blocksToCheck.popLong());
         int neighborDistance = distance + 1;

         for(Direction direction : DIRECTIONS) {
            neighborPos.setWithOffset(pos, (Direction)direction);
            if (bounds.isInside(neighborPos)) {
               int neighborIndexX = neighborPos.getX() - bounds.minX();
               int neighborIndexY = neighborPos.getY() - bounds.minY();
               int neighborIndexZ = neighborPos.getZ() - bounds.minZ();
               if (!updatedShape.isFull(neighborIndexX, neighborIndexY, neighborIndexZ)) {
                  BlockState neighborState = level.getBlockState(neighborPos);
                  OptionalInt existingNeighborDistance = LeavesBlock.getOptionalDistanceAt(neighborState);
                  if (!existingNeighborDistance.isEmpty() && existingNeighborDistance.getAsInt() >= neighborDistance) {
                     setBlockKnownShape(level, neighborPos, (BlockState)neighborState.setValue(BlockStateProperties.DISTANCE, neighborDistance));
                     updatedShape.fill(neighborIndexX, neighborIndexY, neighborIndexZ);
                     if (neighborDistance + 1 < 7) {
                        nextBlocksToCheck.push(neighborPos.asLong());
                     }
                  }
               }
            }
         }
      }

      return updatedShape;
   }

   public static List<BlockPos> getLowestTrunkOrRootOfTree(final TreeDecorator.Context context) {
      List<BlockPos> blockPositions = new ArrayList();
      List<BlockPos> roots = context.roots();
      List<BlockPos> logs = context.logs();
      if (roots.isEmpty()) {
         blockPositions.addAll(logs);
      } else if (!logs.isEmpty() && ((BlockPos)roots.get(0)).getY() == ((BlockPos)logs.get(0)).getY()) {
         blockPositions.addAll(logs);
         blockPositions.addAll(roots);
      } else {
         blockPositions.addAll(roots);
      }

      return blockPositions;
   }

   public static class Builder {
      public final Holder<BlockStateProvider> trunkProvider;
      private final TrunkPlacer trunkPlacer;
      public final Holder<BlockStateProvider> foliageProvider;
      private final FoliagePlacer foliagePlacer;
      private final Optional<RootPlacer> rootPlacer;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators;
      private boolean ignoreVines;
      private Holder<BlockStateProvider> belowTrunkProvider;

      public Builder(final Holder<BlockStateProvider> trunkProvider, final TrunkPlacer trunkPlacer, final Holder<BlockStateProvider> foliageProvider, final FoliagePlacer foliagePlacer, final Optional<RootPlacer> rootPlacer, final FeatureSize minimumSize, final Holder<BlockStateProvider> belowTrunkProvider) {
         super();
         this.decorators = List.of();
         this.trunkProvider = trunkProvider;
         this.trunkPlacer = trunkPlacer;
         this.foliageProvider = foliageProvider;
         this.foliagePlacer = foliagePlacer;
         this.rootPlacer = rootPlacer;
         this.minimumSize = minimumSize;
         this.belowTrunkProvider = belowTrunkProvider;
      }

      public Builder(final BlockStateProvider trunkProvider, final TrunkPlacer trunkPlacer, final BlockStateProvider foliageProvider, final FoliagePlacer foliagePlacer, final Optional<RootPlacer> rootPlacer, final FeatureSize minimumSize, final Holder<BlockStateProvider> belowTrunkProvider) {
         this(Holder.direct(trunkProvider), trunkPlacer, Holder.direct(foliageProvider), foliagePlacer, rootPlacer, minimumSize, belowTrunkProvider);
      }

      public Builder(final BlockStateProvider trunkProvider, final TrunkPlacer trunkPlacer, final BlockStateProvider foliageProvider, final FoliagePlacer foliagePlacer, final FeatureSize minimumSize, final Holder<BlockStateProvider> belowTrunkProvider) {
         this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize, belowTrunkProvider);
      }

      public Builder belowTrunkProvider(final Holder<BlockStateProvider> belowTrunkProvider) {
         this.belowTrunkProvider = belowTrunkProvider;
         return this;
      }

      public Builder decorators(final List<TreeDecorator> decorators) {
         this.decorators = decorators;
         return this;
      }

      public Builder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public TreeFeature build() {
         return new TreeFeature(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.minimumSize, this.decorators, this.ignoreVines, this.belowTrunkProvider);
      }
   }
}
