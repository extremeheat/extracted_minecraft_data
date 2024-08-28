package net.minecraft.resources;

@FunctionalInterface
public interface DependantName<T, V> {
   V get(ResourceKey<T> var1);

   static <T, V> DependantName<T, V> fixed(V var0) {
      return var1 -> (V)var0;
   }
}
