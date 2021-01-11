package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.IStringSerializable;

public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T> {
   private final ImmutableSet<T> field_177711_a;
   private final Map<String, T> field_177710_b = Maps.newHashMap();

   protected PropertyEnum(String var1, Class<T> var2, Collection<T> var3) {
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

   public String func_177702_a(T var1) {
      return ((IStringSerializable)var1).func_176610_l();
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> func_177709_a(String var0, Class<T> var1) {
      return func_177708_a(var0, var1, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> func_177708_a(String var0, Class<T> var1, Predicate<T> var2) {
      return func_177707_a(var0, var1, Collections2.filter(Lists.newArrayList(var1.getEnumConstants()), var2));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> func_177706_a(String var0, Class<T> var1, T... var2) {
      return func_177707_a(var0, var1, Lists.newArrayList(var2));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> func_177707_a(String var0, Class<T> var1, Collection<T> var2) {
      return new PropertyEnum(var0, var1, var2);
   }
}
