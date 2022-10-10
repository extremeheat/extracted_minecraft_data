package net.minecraft.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E> extends AbstractList<E> {
   private final List<E> field_191198_a;
   private final E field_191199_b;

   public static <E> NonNullList<E> func_191196_a() {
      return new NonNullList();
   }

   public static <E> NonNullList<E> func_191197_a(int var0, E var1) {
      Validate.notNull(var1);
      Object[] var2 = new Object[var0];
      Arrays.fill(var2, var1);
      return new NonNullList(Arrays.asList(var2), var1);
   }

   @SafeVarargs
   public static <E> NonNullList<E> func_193580_a(E var0, E... var1) {
      return new NonNullList(Arrays.asList(var1), var0);
   }

   protected NonNullList() {
      this(new ArrayList(), (Object)null);
   }

   protected NonNullList(List<E> var1, @Nullable E var2) {
      super();
      this.field_191198_a = var1;
      this.field_191199_b = var2;
   }

   @Nonnull
   public E get(int var1) {
      return this.field_191198_a.get(var1);
   }

   public E set(int var1, E var2) {
      Validate.notNull(var2);
      return this.field_191198_a.set(var1, var2);
   }

   public void add(int var1, E var2) {
      Validate.notNull(var2);
      this.field_191198_a.add(var1, var2);
   }

   public E remove(int var1) {
      return this.field_191198_a.remove(var1);
   }

   public int size() {
      return this.field_191198_a.size();
   }

   public void clear() {
      if (this.field_191199_b == null) {
         super.clear();
      } else {
         for(int var1 = 0; var1 < this.size(); ++var1) {
            this.set(var1, this.field_191199_b);
         }
      }

   }
}
