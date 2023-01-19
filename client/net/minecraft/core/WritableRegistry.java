package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import net.minecraft.resources.ResourceKey;

public interface WritableRegistry<T> extends Registry<T> {
   Holder<T> registerMapping(int var1, ResourceKey<T> var2, T var3, Lifecycle var4);

   Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3);

   boolean isEmpty();

   HolderGetter<T> createRegistrationLookup();
}
