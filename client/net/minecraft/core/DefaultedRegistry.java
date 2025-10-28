package net.minecraft.core;

import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DefaultedRegistry<T> extends Registry<T> {
   @NonNull ResourceLocation getKey(T var1);

   @NonNull T getValue(@Nullable ResourceLocation var1);

   @NonNull T byId(int var1);

   ResourceLocation getDefaultKey();
}
