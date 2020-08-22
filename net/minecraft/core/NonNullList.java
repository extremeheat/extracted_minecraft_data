package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList extends AbstractList {
   private final List list;
   private final Object defaultValue;

   public static NonNullList create() {
      return new NonNullList();
   }

   public static NonNullList withSize(int var0, Object var1) {
      Validate.notNull(var1);
      Object[] var2 = new Object[var0];
      Arrays.fill(var2, var1);
      return new NonNullList(Arrays.asList(var2), var1);
   }

   @SafeVarargs
   public static NonNullList of(Object var0, Object... var1) {
      return new NonNullList(Arrays.asList(var1), var0);
   }

   protected NonNullList() {
      this(Lists.newArrayList(), (Object)null);
   }

   protected NonNullList(List var1, @Nullable Object var2) {
      this.list = var1;
      this.defaultValue = var2;
   }

   @Nonnull
   public Object get(int var1) {
      return this.list.get(var1);
   }

   public Object set(int var1, Object var2) {
      Validate.notNull(var2);
      return this.list.set(var1, var2);
   }

   public void add(int var1, Object var2) {
      Validate.notNull(var2);
      this.list.add(var1, var2);
   }

   public Object remove(int var1) {
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
