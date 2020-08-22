package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap extends Iterable {
   @Nullable
   Object byId(int var1);
}
