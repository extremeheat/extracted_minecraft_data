package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface DefaultedRegistry<T> extends Registry<T> {
   @Nonnull
   ResourceLocation getKey(T var1);

   @Nonnull
   T get(@Nullable ResourceLocation var1);

   @Nonnull
   T byId(int var1);

   ResourceLocation getDefaultKey();
}
