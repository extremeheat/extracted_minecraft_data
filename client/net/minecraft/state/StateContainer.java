package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.MapPopulator;

public class StateContainer<O, S extends IStateHolder<S>> {
   private static final Pattern field_185921_a = Pattern.compile("^[a-z0-9_]+$");
   private final O field_177627_c;
   private final ImmutableSortedMap<String, IProperty<?>> field_177624_d;
   private final ImmutableList<S> field_177625_e;

   protected <A extends AbstractStateHolder<O, S>> StateContainer(O var1, StateContainer.IFactory<O, S, A> var2, Map<String, IProperty<?>> var3) {
      super();
      this.field_177627_c = var1;
      this.field_177624_d = ImmutableSortedMap.copyOf(var3);
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      ArrayList var5 = Lists.newArrayList();
      Stream var6 = Stream.of(Collections.emptyList());

      IProperty var8;
      for(UnmodifiableIterator var7 = this.field_177624_d.values().iterator(); var7.hasNext(); var6 = var6.flatMap((var1x) -> {
         return var8.func_177700_c().stream().map((var1) -> {
            ArrayList var2 = Lists.newArrayList(var1x);
            var2.add(var1);
            return var2;
         });
      })) {
         var8 = (IProperty)var7.next();
      }

      var6.forEach((var5x) -> {
         Map var6 = MapPopulator.func_179400_b(this.field_177624_d.values(), var5x);
         AbstractStateHolder var7 = var2.create(var1, ImmutableMap.copyOf(var6));
         var4.put(var6, var7);
         var5.add(var7);
      });
      Iterator var9 = var5.iterator();

      while(var9.hasNext()) {
         AbstractStateHolder var10 = (AbstractStateHolder)var9.next();
         var10.func_206874_a(var4);
      }

      this.field_177625_e = ImmutableList.copyOf(var5);
   }

   public ImmutableList<S> func_177619_a() {
      return this.field_177625_e;
   }

   public S func_177621_b() {
      return (IStateHolder)this.field_177625_e.get(0);
   }

   public O func_177622_c() {
      return this.field_177627_c;
   }

   public Collection<IProperty<?>> func_177623_d() {
      return this.field_177624_d.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.field_177627_c).add("properties", this.field_177624_d.values().stream().map(IProperty::func_177701_a).collect(Collectors.toList())).toString();
   }

   @Nullable
   public IProperty<?> func_185920_a(String var1) {
      return (IProperty)this.field_177624_d.get(var1);
   }

   public static class Builder<O, S extends IStateHolder<S>> {
      private final O field_206895_a;
      private final Map<String, IProperty<?>> field_206896_b = Maps.newHashMap();

      public Builder(O var1) {
         super();
         this.field_206895_a = var1;
      }

      public StateContainer.Builder<O, S> func_206894_a(IProperty<?>... var1) {
         IProperty[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            IProperty var5 = var2[var4];
            this.func_206892_a(var5);
            this.field_206896_b.put(var5.func_177701_a(), var5);
         }

         return this;
      }

      private <T extends Comparable<T>> void func_206892_a(IProperty<T> var1) {
         String var2 = var1.func_177701_a();
         if (!StateContainer.field_185921_a.matcher(var2).matches()) {
            throw new IllegalArgumentException(this.field_206895_a + " has invalidly named property: " + var2);
         } else {
            Collection var3 = var1.func_177700_c();
            if (var3.size() <= 1) {
               throw new IllegalArgumentException(this.field_206895_a + " attempted use property " + var2 + " with <= 1 possible values");
            } else {
               Iterator var4 = var3.iterator();

               String var6;
               do {
                  if (!var4.hasNext()) {
                     if (this.field_206896_b.containsKey(var2)) {
                        throw new IllegalArgumentException(this.field_206895_a + " has duplicate property: " + var2);
                     }

                     return;
                  }

                  Comparable var5 = (Comparable)var4.next();
                  var6 = var1.func_177702_a(var5);
               } while(StateContainer.field_185921_a.matcher(var6).matches());

               throw new IllegalArgumentException(this.field_206895_a + " has property: " + var2 + " with invalidly named value: " + var6);
            }
         }
      }

      public <A extends AbstractStateHolder<O, S>> StateContainer<O, S> func_206893_a(StateContainer.IFactory<O, S, A> var1) {
         return new StateContainer(this.field_206895_a, var1, this.field_206896_b);
      }
   }

   public interface IFactory<O, S extends IStateHolder<S>, A extends AbstractStateHolder<O, S>> {
      A create(O var1, ImmutableMap<IProperty<?>, Comparable<?>> var2);
   }
}
