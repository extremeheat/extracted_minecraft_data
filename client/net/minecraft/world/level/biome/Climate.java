package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

   public static Climate.TargetPoint target(float var0, float var1, float var2, float var3, float var4, float var5) {
      return new Climate.TargetPoint(
         quantizeCoord(var0), quantizeCoord(var1), quantizeCoord(var2), quantizeCoord(var3), quantizeCoord(var4), quantizeCoord(var5)
      );
   }

   public static Climate.ParameterPoint parameters(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      return new Climate.ParameterPoint(
         Climate.Parameter.point(var0),
         Climate.Parameter.point(var1),
         Climate.Parameter.point(var2),
         Climate.Parameter.point(var3),
         Climate.Parameter.point(var4),
         Climate.Parameter.point(var5),
         quantizeCoord(var6)
      );
   }

   public static Climate.ParameterPoint parameters(
      Climate.Parameter var0,
      Climate.Parameter var1,
      Climate.Parameter var2,
      Climate.Parameter var3,
      Climate.Parameter var4,
      Climate.Parameter var5,
      float var6
   ) {
      return new Climate.ParameterPoint(var0, var1, var2, var3, var4, var5, quantizeCoord(var6));
   }

   public static long quantizeCoord(float var0) {
      return (long)(var0 * 10000.0F);
   }

   public static float unquantizeCoord(long var0) {
      return (float)var0 / 10000.0F;
   }

   public static Climate.Sampler empty() {
      DensityFunction var0 = DensityFunctions.zero();
      return new Climate.Sampler(var0, var0, var0, var0, var0, var0, List.of());
   }

   public static BlockPos findSpawnPosition(List<Climate.ParameterPoint> var0, Climate.Sampler var1) {
      return (new Climate.SpawnFinder(var0, var1)).result.location();
   }

   interface DistanceMetric<T> {
      long distance(Climate.RTree.Node<T> var1, long[] var2);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static class ParameterList<T> {
      private final List<Pair<Climate.ParameterPoint, T>> values;
      private final Climate.RTree<T> index;

      public static <T> Codec<Climate.ParameterList<T>> codec(MapCodec<T> var0) {
         return ExtraCodecs.nonEmptyList(
               RecordCodecBuilder.create(
                     var1 -> var1.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), var0.forGetter(Pair::getSecond))
                           .apply(var1, Pair::of)
                  )
                  .listOf()
            )
            .xmap(Climate.ParameterList::new, Climate.ParameterList::values);
      }

      public ParameterList(List<Pair<Climate.ParameterPoint, T>> var1) {
         super();
         this.values = var1;
         this.index = Climate.RTree.create(var1);
      }

      public List<Pair<Climate.ParameterPoint, T>> values() {
         return this.values;
      }

      public T findValue(Climate.TargetPoint var1) {
         return this.findValueIndex(var1);
      }

      @VisibleForTesting
      public T findValueBruteForce(Climate.TargetPoint var1) {
         Iterator var2 = this.values().iterator();
         Pair var3 = (Pair)var2.next();
         long var4 = ((Climate.ParameterPoint)var3.getFirst()).fitness(var1);
         Object var6 = var3.getSecond();

         while (var2.hasNext()) {
            Pair var7 = (Pair)var2.next();
            long var8 = ((Climate.ParameterPoint)var7.getFirst()).fitness(var1);
            if (var8 < var4) {
               var4 = var8;
               var6 = var7.getSecond();
            }
         }

         return (T)var6;
      }

      public T findValueIndex(Climate.TargetPoint var1) {
         return this.findValueIndex(var1, Climate.RTree.Node::distance);
      }

      protected T findValueIndex(Climate.TargetPoint var1, Climate.DistanceMetric<T> var2) {
         return this.index.search(var1, var2);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   protected static final class RTree<T> {
      private static final int CHILDREN_PER_NODE = 6;
      private final Climate.RTree.Node<T> root;
      private final ThreadLocal<Climate.RTree.Leaf<T>> lastResult = new ThreadLocal<>();

      private RTree(Climate.RTree.Node<T> var1) {
         super();
         this.root = var1;
      }

      public static <T> Climate.RTree<T> create(List<Pair<Climate.ParameterPoint, T>> var0) {
         if (var0.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int var1 = ((Climate.ParameterPoint)((Pair)var0.get(0)).getFirst()).parameterSpace().size();
            if (var1 != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + var1);
            } else {
               List var2 = var0.stream()
                  .map(var0x -> new Climate.RTree.Leaf<>((Climate.ParameterPoint)var0x.getFirst(), var0x.getSecond()))
                  .collect(Collectors.toCollection(ArrayList::new));
               return new Climate.RTree<>(build(var1, var2));
            }
         }
      }

      private static <T> Climate.RTree.Node<T> build(int var0, List<? extends Climate.RTree.Node<T>> var1) {
         if (var1.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (var1.size() == 1) {
            return (Climate.RTree.Node<T>)var1.get(0);
         } else if (var1.size() <= 6) {
            var1.sort(Comparator.comparingLong(var1x -> {
               long var2x = 0L;

               for (int var4x = 0; var4x < var0; var4x++) {
                  Climate.Parameter var5x = var1x.parameterSpace[var4x];
                  var2x += Math.abs((var5x.min() + var5x.max()) / 2L);
               }

               return var2x;
            }));
            return new Climate.RTree.SubTree<>(var1);
         } else {
            long var2 = 9223372036854775807L;
            int var4 = -1;
            List var5 = null;

            for (int var6 = 0; var6 < var0; var6++) {
               sort(var1, var0, var6, false);
               List var7 = bucketize(var1);
               long var8 = 0L;

               for (Climate.RTree.SubTree var11 : var7) {
                  var8 += cost(var11.parameterSpace);
               }

               if (var2 > var8) {
                  var2 = var8;
                  var4 = var6;
                  var5 = var7;
               }
            }

            sort(var5, var0, var4, true);
            return new Climate.RTree.SubTree<>(var5.stream().map(var1x -> build(var0, Arrays.asList(var1x.children))).collect(Collectors.toList()));
         }
      }

      private static <T> void sort(List<? extends Climate.RTree.Node<T>> var0, int var1, int var2, boolean var3) {
         Comparator var4 = comparator(var2, var3);

         for (int var5 = 1; var5 < var1; var5++) {
            var4 = var4.thenComparing(comparator((var2 + var5) % var1, var3));
         }

         var0.sort(var4);
      }

      private static <T> Comparator<Climate.RTree.Node<T>> comparator(int var0, boolean var1) {
         return Comparator.comparingLong(var2 -> {
            Climate.Parameter var3 = var2.parameterSpace[var0];
            long var4 = (var3.min() + var3.max()) / 2L;
            return var1 ? Math.abs(var4) : var4;
         });
      }

      private static <T> List<Climate.RTree.SubTree<T>> bucketize(List<? extends Climate.RTree.Node<T>> var0) {
         ArrayList var1 = Lists.newArrayList();
         ArrayList var2 = Lists.newArrayList();
         int var3 = (int)Math.pow(6.0, Math.floor(Math.log((double)var0.size() - 0.01) / Math.log(6.0)));

         for (Climate.RTree.Node var5 : var0) {
            var2.add(var5);
            if (var2.size() >= var3) {
               var1.add(new Climate.RTree.SubTree(var2));
               var2 = Lists.newArrayList();
            }
         }

         if (!var2.isEmpty()) {
            var1.add(new Climate.RTree.SubTree(var2));
         }

         return var1;
      }

      private static long cost(Climate.Parameter[] var0) {
         long var1 = 0L;

         for (Climate.Parameter var6 : var0) {
            var1 += Math.abs(var6.max() - var6.min());
         }

         return var1;
      }

      static <T> List<Climate.Parameter> buildParameterSpace(List<? extends Climate.RTree.Node<T>> var0) {
         if (var0.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            byte var1 = 7;
            ArrayList var2 = Lists.newArrayList();

            for (int var3 = 0; var3 < 7; var3++) {
               var2.add(null);
            }

            for (Climate.RTree.Node var4 : var0) {
               for (int var5 = 0; var5 < 7; var5++) {
                  var2.set(var5, var4.parameterSpace[var5].span((Climate.Parameter)var2.get(var5)));
               }
            }

            return var2;
         }
      }

      public T search(Climate.TargetPoint var1, Climate.DistanceMetric<T> var2) {
         long[] var3 = var1.toParameterArray();
         Climate.RTree.Leaf var4 = this.root.search(var3, this.lastResult.get(), var2);
         this.lastResult.set(var4);
         return var4.value;
      }

      static final class Leaf<T> extends Climate.RTree.Node<T> {
         final T value;

         Leaf(Climate.ParameterPoint var1, T var2) {
            super(var1.parameterSpace());
            this.value = (T)var2;
         }

         @Override
         protected Climate.RTree.Leaf<T> search(long[] var1, @Nullable Climate.RTree.Leaf<T> var2, Climate.DistanceMetric<T> var3) {
            return this;
         }
      }

      abstract static class Node<T> {
         protected final Climate.Parameter[] parameterSpace;

         protected Node(List<Climate.Parameter> var1) {
            super();
            this.parameterSpace = var1.toArray(new Climate.Parameter[0]);
         }

         protected abstract Climate.RTree.Leaf<T> search(long[] var1, @Nullable Climate.RTree.Leaf<T> var2, Climate.DistanceMetric<T> var3);

         protected long distance(long[] var1) {
            long var2 = 0L;

            for (int var4 = 0; var4 < 7; var4++) {
               var2 += Mth.square(this.parameterSpace[var4].distance(var1[var4]));
            }

            return var2;
         }

         @Override
         public String toString() {
            return Arrays.toString((Object[])this.parameterSpace);
         }
      }

      static final class SubTree<T> extends Climate.RTree.Node<T> {
         final Climate.RTree.Node<T>[] children;

         protected SubTree(List<? extends Climate.RTree.Node<T>> var1) {
            this(Climate.RTree.buildParameterSpace(var1), var1);
         }

         protected SubTree(List<Climate.Parameter> var1, List<? extends Climate.RTree.Node<T>> var2) {
            super(var1);
            this.children = var2.toArray(new Climate.RTree.Node[0]);
         }

         @Override
         protected Climate.RTree.Leaf<T> search(long[] var1, @Nullable Climate.RTree.Leaf<T> var2, Climate.DistanceMetric<T> var3) {
            long var4 = var2 == null ? 9223372036854775807L : var3.distance(var2, var1);
            Climate.RTree.Leaf var6 = var2;

            for (Climate.RTree.Node var10 : this.children) {
               long var11 = var3.distance(var10, var1);
               if (var4 > var11) {
                  Climate.RTree.Leaf var13 = var10.search(var1, var6, var3);
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
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static class SpawnFinder {
      private static final long MAX_RADIUS = 2048L;
      Climate.SpawnFinder.Result result;

      SpawnFinder(List<Climate.ParameterPoint> var1, Climate.Sampler var2) {
         super();
         this.result = getSpawnPositionAndFitness(var1, var2, 0, 0);
         this.radialSearch(var1, var2, 2048.0F, 512.0F);
         this.radialSearch(var1, var2, 512.0F, 32.0F);
      }

      private void radialSearch(List<Climate.ParameterPoint> var1, Climate.Sampler var2, float var3, float var4) {
         float var5 = 0.0F;
         float var6 = var4;
         BlockPos var7 = this.result.location();

         while (var6 <= var3) {
            int var8 = var7.getX() + (int)(Math.sin((double)var5) * (double)var6);
            int var9 = var7.getZ() + (int)(Math.cos((double)var5) * (double)var6);
            Climate.SpawnFinder.Result var10 = getSpawnPositionAndFitness(var1, var2, var8, var9);
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

      private static Climate.SpawnFinder.Result getSpawnPositionAndFitness(List<Climate.ParameterPoint> var0, Climate.Sampler var1, int var2, int var3) {
         Climate.TargetPoint var4 = var1.sample(QuartPos.fromBlock(var2), 0, QuartPos.fromBlock(var3));
         Climate.TargetPoint var5 = new Climate.TargetPoint(var4.temperature(), var4.humidity(), var4.continentalness(), var4.erosion(), 0L, var4.weirdness());
         long var6 = 9223372036854775807L;

         for (Climate.ParameterPoint var9 : var0) {
            var6 = Math.min(var6, var9.fitness(var5));
         }

         long var12 = Mth.square((long)var2) + Mth.square((long)var3);
         long var10 = var6 * Mth.square(2048L) + var12;
         return new Climate.SpawnFinder.Result(new BlockPos(var2, 0, var3), var10);
      }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
