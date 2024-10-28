package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E> extends AbstractList<E> {
   private final List<E> list;
   @Nullable
   private final E defaultValue;

   public static <E> NonNullList<E> create() {
      return new NonNullList(Lists.newArrayList(), (Object)null);
   }

   public static <E> NonNullList<E> createWithCapacity(int var0) {
      return new NonNullList(Lists.newArrayListWithCapacity(var0), (Object)null);
   }

   public static <E> NonNullList<E> withSize(int var0, E var1) {
      Validate.notNull(var1);
      Object[] var2 = new Object[var0];
      Arrays.fill(var2, var1);
      return new NonNullList(Arrays.asList(var2), var1);
   }

   @SafeVarargs
   public static <E> NonNullList<E> of(E var0, E... var1) {
      return new NonNullList(Arrays.asList(var1), var0);
   }

   protected NonNullList(List<E> var1, @Nullable E var2) {
      super();
      this.list = var1;
      this.defaultValue = var2;
   }

   @Nonnull
   public E get(int var1) {
      return this.list.get(var1);
   }

   public E set(int var1, E var2) {
      Validate.notNull(var2);
      return this.list.set(var1, var2);
   }

   public void add(int var1, E var2) {
      Validate.notNull(var2);
      this.list.add(var1, var2);
   }

   public E remove(int var1) {
      return this.list.remove(var1);
   }

   public int size() {
      return this.list.size();
   }

   public void clear() {
      if (this.defaultValue == null) {
         super.clear();
      } else {
         for(int var1 = 0; var1 < this.size(); ++var1) {
            this.set(var1, this.defaultValue);
         }
      }

   }
}
