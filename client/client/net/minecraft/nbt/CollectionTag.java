package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class CollectionTag<T extends Tag> extends AbstractList<T> implements Tag {
   public CollectionTag() {
      super();
   }

   public abstract T set(int var1, T var2);

   public abstract void add(int var1, T var2);

   public abstract T remove(int var1);

   public abstract boolean setTag(int var1, Tag var2);

   public abstract boolean addTag(int var1, Tag var2);

   public abstract byte getElementType();
}
