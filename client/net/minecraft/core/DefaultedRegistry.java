package net.minecraft.core;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DefaultedRegistry<T> extends Registry<T> {
   @NonNull Identifier getKey(T thing);

   @NonNull T getValue(@Nullable Identifier key);

   @NonNull T byId(int id);

   Identifier getDefaultKey();
}
