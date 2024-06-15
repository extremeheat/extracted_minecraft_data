package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DependencySorter<K, V extends DependencySorter.Entry<K>> {
   private final Map<K, V> contents = new HashMap<>();

   public DependencySorter() {
      super();
   }

   public DependencySorter<K, V> addEntry(K var1, V var2) {
      this.contents.put((K)var1, (V)var2);
      return this;
   }

   private void visitDependenciesAndElement(Multimap<K, K> var1, Set<K> var2, K var3, BiConsumer<K, V> var4) {
      if (var2.add(var3)) {
         var1.get(var3).forEach(var4x -> this.visitDependenciesAndElement(var1, var2, (K)var4x, var4));
         DependencySorter.Entry var5 = this.contents.get(var3);
         if (var5 != null) {
            var4.accept(var3, var5);
         }
      }
   }

   private static <K> boolean isCyclic(Multimap<K, K> var0, K var1, K var2) {
      Collection var3 = var0.get(var2);
      return var3.contains(var1) ? true : var3.stream().anyMatch(var2x -> isCyclic(var0, var1, var2x));
   }

   private static <K> void addDependencyIfNotCyclic(Multimap<K, K> var0, K var1, K var2) {
      if (!isCyclic(var0, var1, var2)) {
         var0.put(var1, var2);
      }
   }

   public void orderByDependencies(BiConsumer<K, V> var1) {
      HashMultimap var2 = HashMultimap.create();
      this.contents.forEach((var1x, var2x) -> var2x.visitRequiredDependencies(var2xx -> addDependencyIfNotCyclic(var2, (K)var1x, var2xx)));
      this.contents.forEach((var1x, var2x) -> var2x.visitOptionalDependencies(var2xx -> addDependencyIfNotCyclic(var2, (K)var1x, var2xx)));
      HashSet var3 = new HashSet();
      this.contents.keySet().forEach(var4 -> this.visitDependenciesAndElement(var2, var3, (K)var4, var1));
   }

   public interface Entry<K> {
      void visitRequiredDependencies(Consumer<K> var1);

      void visitOptionalDependencies(Consumer<K> var1);
   }
}
