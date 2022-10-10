package net.minecraft.state;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;

public class EnumProperty<T extends Enum<T> & IStringSerializable> extends AbstractProperty<T> {
   private final ImmutableSet<T> field_177711_a;
   private final Map<String, T> field_177710_b = Maps.newHashMap();

   protected EnumProperty(String var1, Class<T> var2, Collection<T> var3) {
      super(var1, var2);
      this.field_177711_a = ImmutableSet.copyOf(var3);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Enum var5 = (Enum)var4.next();
         String var6 = ((IStringSerializable)var5).func_176610_l();
         if (this.field_177710_b.containsKey(var6)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + var6 + "'");
         }

         this.field_177710_b.put(var6, var5);
      }

   }

   public Collection<T> func_177700_c() {
      return this.field_177711_a;
   }

   public Optional<T> func_185929_b(String var1) {
      return Optional.ofNullable(this.field_177710_b.get(var1));
   }

   public String func_177702_a(T var1) {
      return ((IStringSerializable)var1).func_176610_l();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof EnumProperty && super.equals(var1)) {
         EnumProperty var2 = (EnumProperty)var1;
         return this.field_177711_a.equals(var2.field_177711_a) && this.field_177710_b.equals(var2.field_177710_b);
      } else {
         return false;
      }
   }

   public int func_206906_c() {
      int var1 = super.func_206906_c();
      var1 = 31 * var1 + this.field_177711_a.hashCode();
      var1 = 31 * var1 + this.field_177710_b.hashCode();
      return var1;
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> func_177709_a(String var0, Class<T> var1) {
      return func_177708_a(var0, var1, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> func_177708_a(String var0, Class<T> var1, Predicate<T> var2) {
      return func_177707_a(var0, var1, (Collection)Arrays.stream(var1.getEnumConstants()).filter(var2).collect(Collectors.toList()));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> func_177706_a(String var0, Class<T> var1, T... var2) {
      return func_177707_a(var0, var1, Lists.newArrayList(var2));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> func_177707_a(String var0, Class<T> var1, Collection<T> var2) {
      return new EnumProperty(var0, var1, var2);
   }
}
