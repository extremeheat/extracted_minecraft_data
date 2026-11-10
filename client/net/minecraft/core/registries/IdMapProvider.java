package net.minecraft.core.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IdMapProvider {
   <T> IdMap<T> idMapOrThrow(ResourceKey<? extends Registry<T>> registryKey);

   <T> IdMap<Holder<T>> idHolderMapOrThrow(ResourceKey<? extends Registry<T>> registryKey);
}
