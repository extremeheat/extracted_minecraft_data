package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.jspecify.annotations.Nullable;

public class Climate {
   private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
   private static final float QUANTIZATION_FACTOR = 10000.0F;
   @VisibleForTesting
   protected static final int PARAMETER_COUNT = 7;

   public Climate() {
      super();
   }

   public static TargetPoint target(final float temperature, final float humidity, final float continentalness, final float erosion, final float depth, final float weirdness) {
      return new TargetPoint(quantizeCoord(temperature), quantizeCoord(humidity), quantizeCoord(continentalness), quantizeCoord(erosion), quantizeCoord(depth), quantizeCoord(weirdness));
   }

   public static ParameterPoint parameters(final float temperature, final float humidity, final float continentalness, final float erosion, final float depth, final float weirdness, final float offset) {
      return new ParameterPoint(Climate.Parameter.point(temperature), Climate.Parameter.point(humidity), Climate.Parameter.point(continentalness), Climate.Parameter.point(erosion), Climate.Parameter.point(depth), Climate.Parameter.point(weirdness), quantizeCoord(offset));
   }

   public static ParameterPoint parameters(final Parameter temperature, final Parameter humidity, final Parameter continentalness, final Parameter erosion, final Parameter depth, final Parameter weirdness, final float offset) {
      return new ParameterPoint(temperature, humidity, continentalness, erosion, depth, weirdness, quantizeCoord(offset));
   }

   public static long quantizeCoord(final float coord) {
      return (long)(coord * 10000.0F);
   }

   public static float unquantizeCoord(final long coord) {
      return (float)coord / 10000.0F;
   }

   public static Sampler empty() {
      DensityFunction zero = DensityFunctions.zero();
      return new Sampler(zero, zero, zero, zero, zero, zero, List.of());
   }

   public static BlockPos findSpawnPosition(final List<ParameterPoint> targetClimates, final Sampler sampler) {
      return (new SpawnFinder(targetClimates, sampler)).result.location();
   }

   protected static final class RTree<T> {
      private static final int CHILDREN_PER_NODE = 6;
      private final Node<T> root;
      private final ThreadLocal<@Nullable Leaf<T>> lastResult = new ThreadLocal();

      private RTree(final Node<T> root) {
         super();
         this.root = root;
      }

      public static <T> RTree<T> create(final List<Pair<ParameterPoint, T>> values) {
         if (values.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int dimensions = ((ParameterPoint)((Pair)values.get(0)).getFirst()).parameterSpace().size();
            if (dimensions != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + dimensions);
            } else {
               List<Leaf<T>> leaves = (List)values.stream().map((p) -> new Leaf((ParameterPoint)p.getFirst(), p.getSecond())).collect(Collectors.toCollection(ArrayList::new));
               return new RTree<T>(build(dimensions, leaves));
            }
         }
      }

      private static <T> Node<T> build(final int dimensions, final List<? extends Node<T>> children) {
         if (children.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (children.size() == 1) {
            return (Node)children.get(0);
         } else if (children.size() <= 6) {
            children.sort(Comparator.comparingLong((leaf) -> {
               long totalMagnitude = 0L;

               for(int d = 0; d < dimensions; ++d) {
                  Parameter parameter = leaf.parameterSpace[d];
                  totalMagnitude += Math.abs((parameter.min() + parameter.max()) / 2L);
               }

               return totalMagnitude;
            }));
            return new SubTree<T>(children);
         } else {
            long minCost = 9223372036854775807L;
            int minDimension = -1;
            List<SubTree<T>> minBuckets = null;

            for(int d = 0; d < dimensions; ++d) {
               sort(children, dimensions, d, false);
               List<SubTree<T>> buckets = bucketize(children);
               long totalCost = 0L;

               for(SubTree<T> bucket : buckets) {
                  totalCost += cost(bucket.parameterSpace);
               }

               if (minCost > totalCost) {
                  minCost = totalCost;
                  minDimension = d;
                  minBuckets = buckets;
               }
            }

            sort(minBuckets, dimensions, minDimension, true);
            return new SubTree<T>((List)minBuckets.stream().map((b) -> build(dimensions, Arrays.asList(b.children))).collect(Collectors.toList()));
         }
      }

      private static <T> void sort(final List<? extends Node<T>> children, final int dimensions, final int dimension, final boolean absolute) {
         Comparator<Node<T>> comparator = comparator(dimension, absolute);

         for(int d = 1; d < dimensions; ++d) {
            comparator = comparator.thenComparing(comparator((dimension + d) % dimensions, absolute));
         }

         children.sort(comparator);
      }

      private static <T> Comparator<Node<T>> comparator(final int dimension, final boolean absolute) {
         return Comparator.comparingLong((leaf) -> {
            Parameter parameter = leaf.parameterSpace[dimension];
            long center = (parameter.min() + parameter.max()) / 2L;
            return absolute ? Math.abs(center) : center;
         });
      }

      private static <T> List<SubTree<T>> bucketize(final List<? extends Node<T>> nodes) {
         List<SubTree<T>> buckets = Lists.newArrayList();
         List<Node<T>> children = Lists.newArrayList();
         int expectedChildrenCount = (int)Math.pow(6.0, Math.floor(Math.log((double)nodes.size() - 0.01) / Math.log(6.0)));

         for(Node<T> child : nodes) {
            children.add(child);
            if (children.size() >= expectedChildrenCount) {
               buckets.add(new SubTree(children));
               children = Lists.newArrayList();
            }
         }

         if (!children.isEmpty()) {
            buckets.add(new SubTree(children));
         }

         return buckets;
      }

      private static long cost(final Parameter[] parameterSpace) {
         long result = 0L;

         for(Parameter parameter : parameterSpace) {
            result += Math.abs(parameter.max() - parameter.min());
         }

         return result;
      }

      private static <T> List<Parameter> buildParameterSpace(final List<? extends Node<T>> children) {
         if (children.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            int dimensions = 7;
            List<Parameter> bounds = Lists.newArrayList();

            for(int d = 0; d < 7; ++d) {
               bounds.add((Object)null);
            }

            for(Node<T> child : children) {
               for(int d = 0; d < 7; ++d) {
                  bounds.set(d, child.parameterSpace[d].span((Parameter)bounds.get(d)));
               }
            }

            return bounds;
         }
      }

      public T search(final TargetPoint target, final DistanceMetric<T> distanceMetric) {
         long[] targetArray = target.toParameterArray();
         Leaf<T> leaf = this.root.search(targetArray, (Leaf)this.lastResult.get(), distanceMetric);
         this.lastResult.set(leaf);
         return leaf.value;
      }

      @VisibleForTesting
      abstract static class Node<T> {
         protected final Parameter[] parameterSpace;

         protected Node(final List<Parameter> parameterSpace) {
            super();
            this.parameterSpace = (Parameter[])parameterSpace.toArray(new Parameter[0]);
         }

         protected abstract Leaf<T> search(final long[] target, final @Nullable Leaf<T> candidate, final DistanceMetric<T> distanceMetric);

         protected long distance(final long[] target) {
            long distance = 0L;

            for(int i = 0; i < 7; ++i) {
               distance += Mth.square(this.parameterSpace[i].distance(target[i]));
            }

            return distance;
         }

         public String toString() {
            return Arrays.toString(this.parameterSpace);
         }
      }

      private static final class Leaf<T> extends Node<T> {
         private final T value;

         private Leaf(final ParameterPoint parameterPoint, final T value) {
            super(parameterPoint.parameterSpace());
            this.value = value;
         }

         protected Leaf<T> search(final long[] target, final @Nullable Leaf<T> candidate, final DistanceMetric<T> distanceMetric) {
            return this;
         }
      }

      private static final class SubTree<T> extends Node<T> {
         private final Node<T>[] children;

         public SubTree(final List<? extends Node<T>> children) {
            this(Climate.RTree.buildParameterSpace(children), children);
         }

         public SubTree(final List<Parameter> parameterSpace, final List<? extends Node<T>> children) {
            super(parameterSpace);
            this.children = (Node[])children.toArray(new Node[0]);
         }

         protected Leaf<T> search(final long[] target, final @Nullable Leaf<T> candidate, final DistanceMetric<T> distanceMetric) {
            long minDistance = candidate == null ? 9223372036854775807L : distanceMetric.distance(candidate, target);
            Leaf<T> closestLeaf = candidate;

            for(Node<T> child : this.children) {
               long childDistance = distanceMetric.distance(child, target);
               if (minDistance > childDistance) {
                  Leaf<T> leaf = child.search(target, closestLeaf, distanceMetric);
                  long leafDistance = child == leaf ? childDistance : distanceMetric.distance(leaf, target);
                  if (minDistance > leafDistance) {
                     minDistance = leafDistance;
                     closestLeaf = leaf;
                  }
               }
            }

            return closestLeaf;
         }
      }
   }

   public static class ParameterList<T> {
      private final List<Pair<ParameterPoint, T>> values;
      private final RTree<T> index;

      public static <T> Codec<ParameterList<T>> codec(final MapCodec<T> valueCodec) {
         return ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((i) -> i.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), valueCodec.forGetter(Pair::getSecond)).apply(i, Pair::of)).listOf()).xmap(ParameterList::new, ParameterList::values);
      }

      public ParameterList(final List<Pair<ParameterPoint, T>> values) {
         super();
         this.values = values;
         this.index = Climate.RTree.<T>create(values);
      }

      public List<Pair<ParameterPoint, T>> values() {
         return this.values;
      }

      public T findValue(final TargetPoint target) {
         return (T)this.findValueIndex(target);
      }

      @VisibleForTesting
      public T findValueBruteForce(final TargetPoint target) {
         Iterator<Pair<ParameterPoint, T>> iterator = this.values().iterator();
         Pair<ParameterPoint, T> first = (Pair)iterator.next();
         long bestFitness = ((ParameterPoint)first.getFirst()).fitness(target);
         T best = (T)first.getSecond();

         while(iterator.hasNext()) {
            Pair<ParameterPoint, T> parameter = (Pair)iterator.next();
            long fitness = ((ParameterPoint)parameter.getFirst()).fitness(target);
            if (fitness < bestFitness) {
               bestFitness = fitness;
               best = (T)parameter.getSecond();
            }
         }

         return best;
      }

      public T findValueIndex(final TargetPoint target) {
         return (T)this.findValueIndex(target, RTree.Node::distance);
      }

      protected T findValueIndex(final TargetPoint target, final DistanceMetric<T> distanceMetric) {
         return this.index.search(target, distanceMetric);
      }
   }

   public static record TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
      public TargetPoint {
         super();
      }

      @VisibleForTesting
      long[] toParameterArray() {
         return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
      }
   }

   public static record ParameterPoint(Parameter temperature, Parameter humidity, Parameter continentalness, Parameter erosion, Parameter depth, Parameter weirdness, long offset) {
      public static final Codec<ParameterPoint> CODEC = RecordCodecBuilder.create((i) -> i.group(Climate.Parameter.CODEC.fieldOf("temperature").forGetter((p) -> p.temperature), Climate.Parameter.CODEC.fieldOf("humidity").forGetter((p) -> p.humidity), Climate.Parameter.CODEC.fieldOf("continentalness").forGetter((p) -> p.continentalness), Climate.Parameter.CODEC.fieldOf("erosion").forGetter((p) -> p.erosion), Climate.Parameter.CODEC.fieldOf("depth").forGetter((p) -> p.depth), Climate.Parameter.CODEC.fieldOf("weirdness").forGetter((p) -> p.weirdness), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((p) -> p.offset)).apply(i, ParameterPoint::new));

      public ParameterPoint {
         super();
      }

      private long fitness(final TargetPoint target) {
         return Mth.square(this.temperature.distance(target.temperature)) + Mth.square(this.humidity.distance(target.humidity)) + Mth.square(this.continentalness.distance(target.continentalness)) + Mth.square(this.erosion.distance(target.erosion)) + Mth.square(this.depth.distance(target.depth)) + Mth.square(this.weirdness.distance(target.weirdness)) + Mth.square(this.offset);
      }

      List<Parameter> parameterSpace() {
         return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Parameter(this.offset, this.offset));
      }
   }

   public static record Parameter(long min, long max) {
      public static final Codec<Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (min, max) -> min.compareTo(max) > 0 ? DataResult.error(() -> "Cannon construct interval, min > max (" + min + " > " + max + ")") : DataResult.success(new Parameter(Climate.quantizeCoord(min), Climate.quantizeCoord(max))), (p) -> Climate.unquantizeCoord(p.min()), (p) -> Climate.unquantizeCoord(p.max()));

      public Parameter {
         super();
      }

      public static Parameter point(final float min) {
         return span(min, min);
      }

      public static Parameter span(final float min, final float max) {
         if (min > max) {
            throw new IllegalArgumentException("min > max: " + min + " " + max);
         } else {
            return new Parameter(Climate.quantizeCoord(min), Climate.quantizeCoord(max));
         }
      }

      public static Parameter span(final Parameter min, final Parameter max) {
         if (min.min() > max.max()) {
            String var10002 = String.valueOf(min);
            throw new IllegalArgumentException("min > max: " + var10002 + " " + String.valueOf(max));
         } else {
            return new Parameter(min.min(), max.max());
         }
      }

      public String toString() {
         return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
      }

      public long distance(final long target) {
         long above = target - this.max;
         long below = this.min - target;
         return above > 0L ? above : Math.max(below, 0L);
      }

      public long distance(final Parameter target) {
         long above = target.min() - this.max;
         long below = this.min - target.max();
         return above > 0L ? above : Math.max(below, 0L);
      }

      public Parameter span(final @Nullable Parameter other) {
         return other == null ? this : new Parameter(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
      }
   }

   public static record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<ParameterPoint> spawnTarget) {
      public Sampler {
         super();
      }

      public TargetPoint sample(final int quartX, final int quartY, final int quartZ) {
         int blockX = QuartPos.toBlock(quartX);
         int blockY = QuartPos.toBlock(quartY);
         int blockZ = QuartPos.toBlock(quartZ);
         DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
         return Climate.target((float)this.temperature.compute(context), (float)this.humidity.compute(context), (float)this.continentalness.compute(context), (float)this.erosion.compute(context), (float)this.depth.compute(context), (float)this.weirdness.compute(context));
      }

      public BlockPos findSpawnPosition() {
         return this.spawnTarget.isEmpty() ? BlockPos.ZERO : Climate.findSpawnPosition(this.spawnTarget, this);
      }
   }

   private static class SpawnFinder {
      private static final long MAX_RADIUS = 2048L;
      private Result result;

      private SpawnFinder(final List<ParameterPoint> targetClimates, final Sampler sampler) {
         super();
         this.result = getSpawnPositionAndFitness(targetClimates, sampler, 0, 0);
         this.radialSearch(targetClimates, sampler, 2048.0F, 512.0F);
         this.radialSearch(targetClimates, sampler, 512.0F, 32.0F);
      }

      private void radialSearch(final List<ParameterPoint> targetClimates, final Sampler sampler, final float maxRadius, final float radiusIncrement) {
         float angle = 0.0F;
         float radius = radiusIncrement;
         BlockPos searchOrigin = this.result.location();

         while(radius <= maxRadius) {
            int x = searchOrigin.getX() + (int)(Math.sin((double)angle) * (double)radius);
            int z = searchOrigin.getZ() + (int)(Math.cos((double)angle) * (double)radius);
            Result candidate = getSpawnPositionAndFitness(targetClimates, sampler, x, z);
            if (candidate.fitness() < this.result.fitness()) {
               this.result = candidate;
            }

            angle += radiusIncrement / radius;
            if ((double)angle > 6.283185307179586) {
               angle = 0.0F;
               radius += radiusIncrement;
            }
         }

      }

      private static Result getSpawnPositionAndFitness(final List<ParameterPoint> targetClimates, final Sampler sampler, final int blockX, final int blockZ) {
         TargetPoint targetPoint = sampler.sample(QuartPos.fromBlock(blockX), 0, QuartPos.fromBlock(blockZ));
         TargetPoint zeroDepthTargetPoint = new TargetPoint(targetPoint.temperature(), targetPoint.humidity(), targetPoint.continentalness(), targetPoint.erosion(), 0L, targetPoint.weirdness());
         long minFitness = 9223372036854775807L;

         for(ParameterPoint point : targetClimates) {
            minFitness = Math.min(minFitness, point.fitness(zeroDepthTargetPoint));
         }

         long distanceBiasToWorldOrigin = Mth.square((long)blockX) + Mth.square((long)blockZ);
         long fitnessWithDistance = minFitness * Mth.square(2048L) + distanceBiasToWorldOrigin;
         return new Result(new BlockPos(blockX, 0, blockZ), fitnessWithDistance);
      }

      private static record Result(BlockPos location, long fitness) {
         private Result {
            super();
         }
      }
   }

   @VisibleForTesting
   interface DistanceMetric<T> {
      long distance(RTree.Node<T> node, long[] target);
   }
}
