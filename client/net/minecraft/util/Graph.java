package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph {
   private Graph() {
      super();
   }

   public static <T> boolean depthFirstSearch(Map<T, Set<T>> var0, Set<T> var1, Set<T> var2, Consumer<T> var3, T var4) {
      if (var1.contains(var4)) {
         return false;
      } else if (var2.contains(var4)) {
         return true;
      } else {
         var2.add(var4);

         for(Object var6 : (Set)var0.getOrDefault(var4, ImmutableSet.of())) {
            if (depthFirstSearch(var0, var1, var2, var3, var6)) {
               return true;
            }
         }

         var2.remove(var4);
         var1.add(var4);
         var3.accept(var4);
         return false;
      }
   }
}
