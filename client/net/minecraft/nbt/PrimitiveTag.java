package net.minecraft.nbt;

public sealed interface PrimitiveTag extends Tag permits NumericTag, StringTag {
   default Tag copy() {
      return this;
   }
}
