package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class AbstractStateHolder<O, S> implements IStateHolder<S> {
   private static final Function<Entry<IProperty<?>, Comparable<?>>, String> field_177233_b = new Function<Entry<IProperty<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<IProperty<?>, Comparable<?>> var1) {
         if (var1 == null) {
            return "<NULL>";
         } else {
            IProperty var2 = (IProperty)var1.getKey();
            return var2.func_177701_a() + "=" + this.func_185886_a(var2, (Comparable)var1.getValue());
         }
      }

      private <T extends Comparable<T>> String func_185886_a(IProperty<T> var1, Comparable<?> var2) {
         return var1.func_177702_a(var2);
      }

      // $FF: synthetic method
      public Object apply(@Nullable Object var1) {
         return this.apply((Entry)var1);
      }
   };
   protected final O field_206876_a;
   private final ImmutableMap<IProperty<?>, Comparable<?>> field_206877_c;
   private final int field_206878_d;
   private Table<IProperty<?>, Comparable<?>, S> field_206879_e;

   protected AbstractStateHolder(O var1, ImmutableMap<IProperty<?>, Comparable<?>> var2) {
      super();
      this.field_206876_a = var1;
      this.field_206877_c = var2;
      this.field_206878_d = var2.hashCode();
   }

   public <T extends Comparable<T>> S func_177231_a(IProperty<T> var1) {
      return this.func_206870_a(var1, (Comparable)func_177232_a(var1.func_177700_c(), this.func_177229_b(var1)));
   }

   protected static <T> T func_177232_a(Collection<T> var0, T var1) {
      Iterator var2 = var0.iterator();

      do {
         if (!var2.hasNext()) {
            return var2.next();
         }
      } while(!var2.next().equals(var1));

      if (var2.hasNext()) {
         return var2.next();
      } else {
         return var0.iterator().next();
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.field_206876_a);
      if (!this.func_206871_b().isEmpty()) {
         var1.append('[');
         var1.append((String)this.func_206871_b().entrySet().stream().map(field_177233_b).collect(Collectors.joining(",")));
         var1.append(']');
      }

      return var1.toString();
   }

   public Collection<IProperty<?>> func_206869_a() {
      return Collections.unmodifiableCollection(this.field_206877_c.keySet());
   }

   public <T extends Comparable<T>> boolean func_196959_b(IProperty<T> var1) {
      return this.field_206877_c.containsKey(var1);
   }

   public <T extends Comparable<T>> T func_177229_b(IProperty<T> var1) {
      Comparable var2 = (Comparable)this.field_206877_c.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Cannot get property " + var1 + " as it does not exist in " + this.field_206876_a);
      } else {
         return (Comparable)var1.func_177699_b().cast(var2);
      }
   }

   public <T extends Comparable<T>, V extends T> S func_206870_a(IProperty<T> var1, V var2) {
      Comparable var3 = (Comparable)this.field_206877_c.get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("Cannot set property " + var1 + " as it does not exist in " + this.field_206876_a);
      } else if (var3 == var2) {
         return this;
      } else {
         Object var4 = this.field_206879_e.get(var1, var2);
         if (var4 == null) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " to " + var2 + " on " + this.field_206876_a + ", it is not an allowed value");
         } else {
            return var4;
         }
      }
   }

   public void func_206874_a(Map<Map<IProperty<?>, Comparable<?>>, S> var1) {
      if (this.field_206879_e != null) {
         throw new IllegalStateException();
      } else {
         HashBasedTable var2 = HashBasedTable.create();
         UnmodifiableIterator var3 = this.field_206877_c.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            IProperty var5 = (IProperty)var4.getKey();
            Iterator var6 = var5.func_177700_c().iterator();

            while(var6.hasNext()) {
               Comparable var7 = (Comparable)var6.next();
               if (var7 != var4.getValue()) {
                  var2.put(var5, var7, var1.get(this.func_206875_b(var5, var7)));
               }
            }
         }

         this.field_206879_e = (Table)(var2.isEmpty() ? var2 : ArrayTable.create(var2));
      }
   }

   private Map<IProperty<?>, Comparable<?>> func_206875_b(IProperty<?> var1, Comparable<?> var2) {
      HashMap var3 = Maps.newHashMap(this.field_206877_c);
      var3.put(var1, var2);
      return var3;
   }

   public ImmutableMap<IProperty<?>, Comparable<?>> func_206871_b() {
      return this.field_206877_c;
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return this.field_206878_d;
   }
}
