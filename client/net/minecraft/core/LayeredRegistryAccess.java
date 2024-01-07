package net.minecraft.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;

public class LayeredRegistryAccess<T> {
   private final List<T> keys;
   private final List<RegistryAccess.Frozen> values;
   private final RegistryAccess.Frozen composite;

   public LayeredRegistryAccess(List<T> var1) {
      this(var1, Util.make(() -> {
         RegistryAccess.Frozen[] var1xx = new RegistryAccess.Frozen[var1.size()];
         Arrays.fill(var1xx, RegistryAccess.EMPTY);
         return Arrays.asList(var1xx);
      }));
   }

   private LayeredRegistryAccess(List<T> var1, List<RegistryAccess.Frozen> var2) {
      super();
      this.keys = List.copyOf(var1);
      this.values = List.copyOf(var2);
      this.composite = new RegistryAccess.ImmutableRegistryAccess(collectRegistries(var2.stream())).freeze();
   }

   private int getLayerIndexOrThrow(T var1) {
      int var2 = this.keys.indexOf(var1);
      if (var2 == -1) {
         throw new IllegalStateException("Can't find " + var1 + " inside " + this.keys);
      } else {
         return var2;
      }
   }

   public RegistryAccess.Frozen getLayer(T var1) {
      int var2 = this.getLayerIndexOrThrow((T)var1);
      return this.values.get(var2);
   }

   public RegistryAccess.Frozen getAccessForLoading(T var1) {
      int var2 = this.getLayerIndexOrThrow((T)var1);
      return this.getCompositeAccessForLayers(0, var2);
   }

   public RegistryAccess.Frozen getAccessFrom(T var1) {
      int var2 = this.getLayerIndexOrThrow((T)var1);
      return this.getCompositeAccessForLayers(var2, this.values.size());
   }

   private RegistryAccess.Frozen getCompositeAccessForLayers(int var1, int var2) {
      return new RegistryAccess.ImmutableRegistryAccess(collectRegistries(this.values.subList(var1, var2).stream())).freeze();
   }

   public LayeredRegistryAccess<T> replaceFrom(T var1, RegistryAccess.Frozen... var2) {
      return this.replaceFrom((T)var1, Arrays.asList(var2));
   }

   public LayeredRegistryAccess<T> replaceFrom(T var1, List<RegistryAccess.Frozen> var2) {
      int var3 = this.getLayerIndexOrThrow((T)var1);
      if (var2.size() > this.values.size() - var3) {
         throw new IllegalStateException("Too many values to replace");
      } else {
         ArrayList var4 = new ArrayList();

         for(int var5 = 0; var5 < var3; ++var5) {
            var4.add(this.values.get(var5));
         }

         var4.addAll(var2);

         while(var4.size() < this.values.size()) {
            var4.add(RegistryAccess.EMPTY);
         }

         return new LayeredRegistryAccess<>(this.keys, var4);
      }
   }

   public RegistryAccess.Frozen compositeAccess() {
      return this.composite;
   }

   private static Map<ResourceKey<? extends Registry<?>>, Registry<?>> collectRegistries(Stream<? extends RegistryAccess> var0) {
      HashMap var1 = new HashMap();
      var0.forEach(var1x -> var1x.registries().forEach(var1xx -> {
            if (var1.put(var1xx.key(), var1xx.value()) != null) {
               throw new IllegalStateException("Duplicated registry " + var1xx.key());
            }
         }));
      return var1;
   }
}
