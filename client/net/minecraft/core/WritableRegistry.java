package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceKey;

public abstract class WritableRegistry<T> extends Registry<T> {
   public WritableRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      super(var1, var2);
   }

   public abstract <V extends T> V registerMapping(int var1, ResourceKey<T> var2, V var3, Lifecycle var4);

   public abstract <V extends T> V register(ResourceKey<T> var1, V var2, Lifecycle var3);

   public abstract <V extends T> V registerOrOverride(OptionalInt var1, ResourceKey<T> var2, V var3, Lifecycle var4);
}
