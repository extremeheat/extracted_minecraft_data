package net.minecraft.data.worldgen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface BootstrapContext<T> {
   Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3);

   default Holder.Reference<T> register(ResourceKey<T> var1, T var2) {
      return this.register(var1, var2, Lifecycle.stable());
   }

   <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> var1);
}
