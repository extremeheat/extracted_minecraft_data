package net.minecraft.core;

public interface HolderOwner<T> {
   default boolean canSerializeIn(HolderOwner<T> var1) {
      return var1 == this;
   }
}
