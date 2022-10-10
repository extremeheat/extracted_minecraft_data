package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
   private int field_195868_a;
   private final IdentityHashMap<T, Integer> field_148749_a;
   private final List<T> field_148748_b;

   public ObjectIntIdentityMap() {
      this(512);
   }

   public ObjectIntIdentityMap(int var1) {
      super();
      this.field_148748_b = Lists.newArrayListWithExpectedSize(var1);
      this.field_148749_a = new IdentityHashMap(var1);
   }

   public void func_148746_a(T var1, int var2) {
      this.field_148749_a.put(var1, var2);

      while(this.field_148748_b.size() <= var2) {
         this.field_148748_b.add((Object)null);
      }

      this.field_148748_b.set(var2, var1);
      if (this.field_195868_a <= var2) {
         this.field_195868_a = var2 + 1;
      }

   }

   public void func_195867_b(T var1) {
      this.func_148746_a(var1, this.field_195868_a);
   }

   public int func_148747_b(T var1) {
      Integer var2 = (Integer)this.field_148749_a.get(var1);
      return var2 == null ? -1 : var2;
   }

   @Nullable
   public final T func_148745_a(int var1) {
      return var1 >= 0 && var1 < this.field_148748_b.size() ? this.field_148748_b.get(var1) : null;
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.field_148748_b.iterator(), Predicates.notNull());
   }

   public int func_186804_a() {
      return this.field_148749_a.size();
   }
}
