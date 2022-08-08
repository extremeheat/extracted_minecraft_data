package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class Climate {
   private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
   private static final float QUANTIZATION_FACTOR = 10000.0F;
   @VisibleForTesting
   protected static final int PARAMETER_COUNT = 7;

   public Climate() {
      super();
   }

   public static TargetPoint target(float var0, float var1, float var2, float var3, float var4, float var5) {
      return new TargetPoint(quantizeCoord(var0), quantizeCoord(var1), quantizeCoord(var2), quantizeCoord(var3), quantizeCoord(var4), quantizeCoord(var5));
   }

   public static ParameterPoint parameters(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      return new ParameterPoint(Climate.Parameter.point(var0), Climate.Parameter.point(var1), Climate.Parameter.point(var2), Climate.Parameter.point(var3), Climate.Parameter.point(var4), Climate.Parameter.point(var5), quantizeCoord(var6));
   }

   public static ParameterPoint parameters(Parameter var0, Parameter var1, Parameter var2, Parameter var3, Parameter var4, Parameter var5, float var6) {
      return new ParameterPoint(var0, var1, var2, var3, var4, var5, quantizeCoord(var6));
   }

   public static long quantizeCoord(float var0) {
      return (long)(var0 * 10000.0F);
   }

   public static float unquantizeCoord(long var0) {
      return (float)var0 / 10000.0F;
   }

   public static Sampler empty() {
      DensityFunction var0 = DensityFunctions.zero();
      return new Sampler(var0, var0, var0, var0, var0, var0, List.of());
   }

   public static BlockPos findSpawnPosition(List<ParameterPoint> var0, Sampler var1) {
      return (new SpawnFinder(var0, var1)).result.location();
   }

   public static record TargetPoint(long a, long b, long c, long d, long e, long f) {
      final long temperature;
      final long humidity;
      final long continentalness;
      final long erosion;
      final long depth;
      final long weirdness;

      public TargetPoint(long var1, long var3, long var5, long var7, long var9, long var11) {
         super();
         this.temperature = var1;
         this.humidity = var3;
         this.continentalness = var5;
         this.erosion = var7;
         this.depth = var9;
         this.weirdness = var11;
      }

      @VisibleForTesting
      protected long[] toParameterArray() {
         return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
      }

      public long temperature() {
         return this.temperature;
      }

      public long humidity() {
         return this.humidity;
      }

      public long continentalness() {
         return this.continentalness;
      }

      public long erosion() {
         return this.erosion;
      }

      public long depth() {
         return this.depth;
      }

      public long weirdness() {
         return this.weirdness;
      }
   }

   public static record ParameterPoint(Parameter b, Parameter c, Parameter d, Parameter e, Parameter f, Parameter g, long h) {
      private final Parameter temperature;
      private final Parameter humidity;
      private final Parameter continentalness;
      private final Parameter erosion;
      private final Parameter depth;
      private final Parameter weirdness;
      private final long offset;
      public static final Codec<ParameterPoint> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Climate.Parameter.CODEC.fieldOf("temperature").forGetter((var0x) -> {
            return var0x.temperature;
         }), Climate.Parameter.CODEC.fieldOf("humidity").forGetter((var0x) -> {
            return var0x.humidity;
         }), Climate.Parameter.CODEC.fieldOf("continentalness").forGetter((var0x) -> {
            return var0x.continentalness;
         }), Climate.Parameter.CODEC.fieldOf("erosion").forGetter((var0x) -> {
            return var0x.erosion;
         }), Climate.Parameter.CODEC.fieldOf("depth").forGetter((var0x) -> {
            return var0x.depth;
         }), Climate.Parameter.CODEC.fieldOf("weirdness").forGetter((var0x) -> {
            return var0x.weirdness;
         }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((var0x) -> {
            return var0x.offset;
         })).apply(var0, ParameterPoint::new);
      });

      public ParameterPoint(Parameter var1, Parameter var2, Parameter var3, Parameter var4, Parameter var5, Parameter var6, long var7) {
         super();
         this.temperature = var1;
         this.humidity = var2;
         this.continentalness = var3;
         this.erosion = var4;
         this.depth = var5;
         this.weirdness = var6;
         this.offset = var7;
      }

      long fitness(TargetPoint var1) {
         return Mth.square(this.temperature.distance(var1.temperature)) + Mth.square(this.humidity.distance(var1.humidity)) + Mth.square(this.continentalness.distance(var1.continentalness)) + Mth.square(this.erosion.distance(var1.erosion)) + Mth.square(this.depth.distance(var1.depth)) + Mth.square(this.weirdness.distance(var1.weirdness)) + Mth.square(this.offset);
      }

      protected List<Parameter> parameterSpace() {
         return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Parameter(this.offset, this.offset));
      }

      public Parameter temperature() {
         return this.temperature;
      }

      public Parameter humidity() {
         return this.humidity;
      }

      public Parameter continentalness() {
         return this.continentalness;
      }

      public Parameter erosion() {
         return this.erosion;
      }

      public Parameter depth() {
         return this.depth;
      }

      public Parameter weirdness() {
         return this.weirdness;
      }

      public long offset() {
         return this.offset;
      }
   }

   public static record Parameter(long b, long c) {
      private final long min;
      private final long max;
      public static final Codec<Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (var0, var1) -> {
         return var0.compareTo(var1) > 0 ? DataResult.error("Cannon construct interval, min > max (" + var0 + " > " + var1 + ")") : DataResult.success(new Parameter(Climate.quantizeCoord(var0), Climate.quantizeCoord(var1)));
      }, (var0) -> {
         return Climate.unquantizeCoord(var0.min());
      }, (var0) -> {
         return Climate.unquantizeCoord(var0.max());
      });

      public Parameter(long var1, long var3) {
         super();
         this.min = var1;
         this.max = var3;
      }

      public static Parameter point(float var0) {
         return span(var0, var0);
      }

      public static Parameter span(float var0, float var1) {
         if (var0 > var1) {
            throw new IllegalArgumentException("min > max: " + var0 + " " + var1);
         } else {
            return new Parameter(Climate.quantizeCoord(var0), Climate.quantizeCoord(var1));
         }
      }

      public static Parameter span(Parameter var0, Parameter var1) {
         if (var0.min() > var1.max()) {
            throw new IllegalArgumentException("min > max: " + var0 + " " + var1);
         } else {
            return new Parameter(var0.min(), var1.max());
         }
      }

      public String toString() {
         return this.min == this.max ? String.format("%d", this.min) : String.format("[%d-%d]", this.min, this.max);
      }

      public long distance(long var1) {
         long var3 = var1 - this.max;
         long var5 = this.min - var1;
         return var3 > 0L ? var3 : Math.max(var5, 0L);
      }

      public long distance(Parameter var1) {
         long var2 = var1.min() - this.max;
         long var4 = this.min - var1.max();
         return var2 > 0L ? var2 : Math.max(var4, 0L);
      }

      public Parameter span(@Nullable Parameter var1) {
         return var1 == null ? this : new Parameter(Math.min(this.min, var1.min()), Math.max(this.max, var1.max()));
      }

      public long min() {
         return this.min;
      }

      public long max() {
         return this.max;
      }
   }

   public static record Sampler(DensityFunction a, DensityFunction b, DensityFunction c, DensityFunction d, DensityFunction e, DensityFunction f, List<ParameterPoint> g) {
      private final DensityFunction temperature;
      private final DensityFunction humidity;
      private final DensityFunction continentalness;
      private final DensityFunction erosion;
      private final DensityFunction depth;
      private final DensityFunction weirdness;
      private final List<ParameterPoint> spawnTarget;

      public Sampler(DensityFunction var1, DensityFunction var2, DensityFunction var3, DensityFunction var4, DensityFunction var5, DensityFunction var6, List<ParameterPoint> var7) {
         super();
         this.temperature = var1;
         this.humidity = var2;
         this.continentalness = var3;
         this.erosion = var4;
         this.depth = var5;
         this.weirdness = var6;
         this.spawnTarget = var7;
      }

      public TargetPoint sample(int var1, int var2, int var3) {
         int var4 = QuartPos.toBlock(var1);
         int var5 = QuartPos.toBlock(var2);
         int var6 = QuartPos.toBlock(var3);
         DensityFunction.SinglePointContext var7 = new DensityFunction.SinglePointContext(var4, var5, var6);
         return Climate.target((float)this.temperature.compute(var7), (float)this.humidity.compute(var7), (float)this.continentalness.compute(var7), (float)this.erosion.compute(var7), (float)this.depth.compute(var7), (float)this.weirdness.compute(var7));
      }

      public BlockPos findSpawnPosition() {
         return this.spawnTarget.isEmpty() ? BlockPos.ZERO : Climate.findSpawnPosition(this.spawnTarget, this);
      }

      public DensityFunction temperature() {
         return this.temperature;
      }

      public DensityFunction humidity() {
         return this.humidity;
      }

      public DensityFunction continentalness() {
         return this.continentalness;
      }

      public DensityFunction erosion() {
         return this.erosion;
      }

      public DensityFunction depth() {
         return this.depth;
      }

      public DensityFunction weirdness() {
         return this.weirdness;
      }

      public List<ParameterPoint> spawnTarget() {
         return this.spawnTarget;
      }
   }

   private static class SpawnFinder {
      Result result;

      SpawnFinder(List<ParameterPoint> var1, Sampler var2) {
         super();
         this.result = getSpawnPositionAndFitness(var1, var2, 0, 0);
         this.radialSearch(var1, var2, 2048.0F, 512.0F);
         this.radialSearch(var1, var2, 512.0F, 32.0F);
      }

      private void radialSearch(List<ParameterPoint> var1, Sampler var2, float var3, float var4) {
         float var5 = 0.0F;
         float var6 = var4;
         BlockPos var7 = this.result.location();

         while(var6 <= var3) {
            int var8 = var7.getX() + (int)(Math.sin((double)var5) * (double)var6);
            int var9 = var7.getZ() + (int)(Math.cos((double)var5) * (double)var6);
            Result var10 = getSpawnPositionAndFitness(var1, var2, var8, var9);
            if (var10.fitness() < this.result.fitness()) {
               this.result = var10;
            }

            var5 += var4 / var6;
            if ((double)var5 > 6.283185307179586) {
               var5 = 0.0F;
               var6 += var4;
            }
         }

      }

      private static Result getSpawnPositionAndFitness(List<ParameterPoint> var0, Sampler var1, int var2, int var3) {
         double var4 = Mth.square(2500.0);
         boolean var6 = true;
         long var7 = (long)((double)Mth.square(10000.0F) * Math.pow((double)(Mth.square((long)var2) + Mth.square((long)var3)) / var4, 2.0));
         TargetPoint var9 = var1.sample(QuartPos.fromBlock(var2), 0, QuartPos.fromBlock(var3));
         TargetPoint var10 = new TargetPoint(var9.temperature(), var9.humidity(), var9.continentalness(), var9.erosion(), 0L, var9.weirdness());
         long var11 = 9223372036854775807L;

         ParameterPoint var14;
         for(Iterator var13 = var0.iterator(); var13.hasNext(); var11 = Math.min(var11, var14.fitness(var10))) {
            var14 = (ParameterPoint)var13.next();
         }

         return new Result(new BlockPos(var2, 0, var3), var7 + var11);
      }

      private static record Result(BlockPos a, long b) {
         private final BlockPos location;
         private final long fitness;

         Result(BlockPos var1, long var2) {
            super();
            this.location = var1;
            this.fitness = var2;
         }

         public BlockPos location() {
            return this.location;
         }

         public long fitness() {
            return this.fitness;
         }
      }
   }

   public static class ParameterList<T> {
      private final List<Pair<ParameterPoint, T>> values;
      private final RTree<T> index;

      public ParameterList(List<Pair<ParameterPoint, T>> var1) {
         super();
         this.values = var1;
         this.index = Climate.RTree.create(var1);
      }

      public List<Pair<ParameterPoint, T>> values() {
         return this.values;
      }

      public T findValue(TargetPoint var1) {
         return this.findValueIndex(var1);
      }

      @VisibleForTesting
      public T findValueBruteForce(TargetPoint var1) {
         Iterator var2 = this.values().iterator();
         Pair var3 = (Pair)var2.next();
         long var4 = ((ParameterPoint)var3.getFirst()).fitness(var1);
         Object var6 = var3.getSecond();

         while(var2.hasNext()) {
            Pair var7 = (Pair)var2.next();
            long var8 = ((ParameterPoint)var7.getFirst()).fitness(var1);
            if (var8 < var4) {
               var4 = var8;
               var6 = var7.getSecond();
            }
         }

         return var6;
      }

      public T findValueIndex(TargetPoint var1) {
         return this.findValueIndex(var1, RTree.Node::distance);
      }

      protected T findValueIndex(TargetPoint var1, DistanceMetric<T> var2) {
         return this.index.search(var1, var2);
      }
   }

   protected static final class RTree<T> {
      private static final int CHILDREN_PER_NODE = 10;
      private final Node<T> root;
      private final ThreadLocal<Leaf<T>> lastResult = new ThreadLocal();

      private RTree(Node<T> var1) {
         super();
         this.root = var1;
      }

      public static <T> RTree<T> create(List<Pair<ParameterPoint, T>> var0) {
         if (var0.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int var1 = ((ParameterPoint)((Pair)var0.get(0)).getFirst()).parameterSpace().size();
            if (var1 != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + var1);
            } else {
               List var2 = (List)var0.stream().map((var0x) -> {
                  return new Leaf((ParameterPoint)var0x.getFirst(), var0x.getSecond());
               }).collect(Collectors.toCollection(ArrayList::new));
               return new RTree(build(var1, var2));
            }
         }
      }

      private static <T> Node<T> build(int var0, List<? extends Node<T>> var1) {
         if (var1.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (var1.size() == 1) {
            return (Node)var1.get(0);
         } else if (var1.size() <= 10) {
            var1.sort(Comparator.comparingLong((var1x) -> {
               long var2 = 0L;

               for(int var4 = 0; var4 < var0; ++var4) {
                  Parameter var5 = var1x.parameterSpace[var4];
                  var2 += Math.abs((var5.min() + var5.max()) / 2L);
               }

               return var2;
            }));
            return new SubTree(var1);
         } else {
            long var2 = 9223372036854775807L;
            int var4 = -1;
            List var5 = null;

            for(int var6 = 0; var6 < var0; ++var6) {
               sort(var1, var0, var6, false);
               List var7 = bucketize(var1);
               long var8 = 0L;

               SubTree var11;
               for(Iterator var10 = var7.iterator(); var10.hasNext(); var8 += cost(var11.parameterSpace)) {
                  var11 = (SubTree)var10.next();
               }

               if (var2 > var8) {
                  var2 = var8;
                  var4 = var6;
                  var5 = var7;
               }
            }

            sort(var5, var0, var4, true);
            return new SubTree((List)var5.stream().map((var1x) -> {
               return build(var0, Arrays.asList(var1x.children));
            }).collect(Collectors.toList()));
         }
      }

      private static <T> void sort(List<? extends Node<T>> var0, int var1, int var2, boolean var3) {
         Comparator var4 = comparator(var2, var3);

         for(int var5 = 1; var5 < var1; ++var5) {
            var4 = var4.thenComparing(comparator((var2 + var5) % var1, var3));
         }

         var0.sort(var4);
      }

      private static <T> Comparator<Node<T>> comparator(int var0, boolean var1) {
         return Comparator.comparingLong((var2) -> {
            Parameter var3 = var2.parameterSpace[var0];
            long var4 = (var3.min() + var3.max()) / 2L;
            return var1 ? Math.abs(var4) : var4;
         });
      }

      private static <T> List<SubTree<T>> bucketize(List<? extends Node<T>> var0) {
         ArrayList var1 = Lists.newArrayList();
         ArrayList var2 = Lists.newArrayList();
         int var3 = (int)Math.pow(10.0, Math.floor(Math.log((double)var0.size() - 0.01) / Math.log(10.0)));
         Iterator var4 = var0.iterator();

         while(var4.hasNext()) {
            Node var5 = (Node)var4.next();
            var2.add(var5);
            if (var2.size() >= var3) {
               var1.add(new SubTree(var2));
               var2 = Lists.newArrayList();
            }
         }

         if (!var2.isEmpty()) {
            var1.add(new SubTree(var2));
         }

         return var1;
      }

      private static long cost(Parameter[] var0) {
         long var1 = 0L;
         Parameter[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Parameter var6 = var3[var5];
            var1 += Math.abs(var6.max() - var6.min());
         }

         return var1;
      }

      static <T> List<Parameter> buildParameterSpace(List<? extends Node<T>> var0) {
         if (var0.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            boolean var1 = true;
            ArrayList var2 = Lists.newArrayList();

            for(int var3 = 0; var3 < 7; ++var3) {
               var2.add((Object)null);
            }

            Iterator var6 = var0.iterator();

            while(var6.hasNext()) {
               Node var4 = (Node)var6.next();

               for(int var5 = 0; var5 < 7; ++var5) {
                  var2.set(var5, var4.parameterSpace[var5].span((Parameter)var2.get(var5)));
               }
            }

            return var2;
         }
      }

      public T search(TargetPoint var1, DistanceMetric<T> var2) {
         long[] var3 = var1.toParameterArray();
         Leaf var4 = this.root.search(var3, (Leaf)this.lastResult.get(), var2);
         this.lastResult.set(var4);
         return var4.value;
      }

      abstract static class Node<T> {
         protected final Parameter[] parameterSpace;

         protected Node(List<Parameter> var1) {
            super();
            this.parameterSpace = (Parameter[])var1.toArray(new Parameter[0]);
         }

         protected abstract Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3);

         protected long distance(long[] var1) {
            long var2 = 0L;

            for(int var4 = 0; var4 < 7; ++var4) {
               var2 += Mth.square(this.parameterSpace[var4].distance(var1[var4]));
            }

            return var2;
         }

         public String toString() {
            return Arrays.toString(this.parameterSpace);
         }
      }

      private static final class SubTree<T> extends Node<T> {
         final Node<T>[] children;

         protected SubTree(List<? extends Node<T>> var1) {
            this(Climate.RTree.buildParameterSpace(var1), var1);
         }

         protected SubTree(List<Parameter> var1, List<? extends Node<T>> var2) {
            super(var1);
            this.children = (Node[])var2.toArray(new Node[0]);
         }

         protected Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3) {
            long var4 = var2 == null ? 9223372036854775807L : var3.distance(var2, var1);
            Leaf var6 = var2;
            Node[] var7 = this.children;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Node var10 = var7[var9];
               long var11 = var3.distance(var10, var1);
               if (var4 > var11) {
                  Leaf var13 = var10.search(var1, var6, var3);
                  long var14 = var10 == var13 ? var11 : var3.distance(var13, var1);
                  if (var4 > var14) {
                     var4 = var14;
                     var6 = var13;
                  }
               }
            }

            return var6;
         }
      }

      private static final class Leaf<T> extends Node<T> {
         final T value;

         Leaf(ParameterPoint var1, T var2) {
            super(var1.parameterSpace());
            this.value = var2;
         }

         protected Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3) {
            return this;
         }
      }
   }

   interface DistanceMetric<T> {
      long distance(RTree.Node<T> var1, long[] var2);
   }
}
