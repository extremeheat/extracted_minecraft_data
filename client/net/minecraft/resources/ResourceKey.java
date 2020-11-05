package net.minecraft.resources;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class ResourceKey<T> {
   private static final Map<String, ResourceKey<?>> VALUES = Collections.synchronizedMap(Maps.newIdentityHashMap());
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> var0, ResourceLocation var1) {
      return create(var0.location, var1);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation var0) {
      return create(Registry.ROOT_REGISTRY_NAME, var0);
   }

   private static <T> ResourceKey<T> create(ResourceLocation var0, ResourceLocation var1) {
      String var2 = (var0 + ":" + var1).intern();
      return (ResourceKey)VALUES.computeIfAbsent(var2, (var2x) -> {
         return new ResourceKey(var0, var1);
      });
   }

   private ResourceKey(ResourceLocation var1, ResourceLocation var2) {
      super();
      this.registryName = var1;
      this.location = var2;
   }

   public String toString() {
      return "ResourceKey[" + this.registryName + " / " + this.location + ']';
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registryName.equals(var1.location());
   }

   public ResourceLocation location() {
      return this.location;
   }

   public static <T> Function<ResourceLocation, ResourceKey<T>> elementKey(ResourceKey<? extends Registry<T>> var0) {
      return (var1) -> {
         return create(var0, var1);
      };
   }
}
