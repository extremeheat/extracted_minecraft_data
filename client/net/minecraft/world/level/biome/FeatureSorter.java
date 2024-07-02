package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
   public FeatureSorter() {
      super();
   }

   public static <T> List<FeatureSorter.StepFeatureData> buildFeaturesPerStep(List<T> var0, Function<T, List<HolderSet<PlacedFeature>>> var1, boolean var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      MutableInt var4 = new MutableInt(0);

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.modules.decompiler.exps.VarExprent.toJava(VarExprent.java:124)
//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
//   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
//   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
//   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)

      Comparator var5 = Comparator.comparingInt(1FeatureData::step).thenComparingInt(1FeatureData::featureIndex);
      TreeMap var6 = new TreeMap(var5);
      int var7 = 0;

      for (Object var9 : var0) {
         ArrayList var10 = Lists.newArrayList();
         List var11 = (List)var1.apply(var9);
         var7 = Math.max(var7, var11.size());

         for (int var12 = 0; var12 < var11.size(); var12++) {
            for (Holder var14 : (HolderSet)var11.get(var12)) {
               PlacedFeature var15 = (PlacedFeature)var14.value();
               var10.add(new 1FeatureData(var3.computeIfAbsent(var15, var1x -> var4.getAndIncrement()), var12, var15));
            }
         }

         for (int var24 = 0; var24 < var10.size(); var24++) {
            Set var27 = var6.computeIfAbsent((1FeatureData)var10.get(var24), var1x -> new TreeSet(var5));
            if (var24 < var10.size() - 1) {
               var27.add((1FeatureData)var10.get(var24 + 1));
            }
         }
      }

      TreeSet var19 = new TreeSet(var5);
      TreeSet var20 = new TreeSet(var5);
      ArrayList var21 = Lists.newArrayList();

      for (1FeatureData var25 : var6.keySet()) {
         if (!var20.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!var19.contains(var25) && Graph.depthFirstSearch(var6, var19, var20, var21::add, var25)) {
            if (!var2) {
               throw new IllegalStateException("Feature order cycle found");
            }

            ArrayList var28 = new ArrayList(var0);

            int var30;
            do {
               var30 = var28.size();
               ListIterator var32 = var28.listIterator();

               while (var32.hasNext()) {
                  Object var16 = var32.next();
                  var32.remove();

                  try {
                     buildFeaturesPerStep(var28, var1, false);
                  } catch (IllegalStateException var18) {
                     continue;
                  }

                  var32.add(var16);
               }
            } while (var30 != var28.size());

            throw new IllegalStateException("Feature order cycle found, involved sources: " + var28);
         }
      }

      Collections.reverse(var21);
      Builder var23 = ImmutableList.builder();

      for (int var26 = 0; var26 < var7; var26++) {
         int var29 = var26;
         List var31 = var21.stream().filter(var1x -> var1x.step() == var29).map(1FeatureData::feature).collect(Collectors.toList());
         var23.add(new FeatureSorter.StepFeatureData(var31));
      }

      return var23.build();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
