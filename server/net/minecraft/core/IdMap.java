package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
   int getId(T var1);

   @Nullable
   T byId(int var1);
}
