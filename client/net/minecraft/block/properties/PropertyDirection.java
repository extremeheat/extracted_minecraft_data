package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.util.EnumFacing;

public class PropertyDirection extends PropertyEnum<EnumFacing> {
   protected PropertyDirection(String var1, Collection<EnumFacing> var2) {
      super(var1, EnumFacing.class, var2);
   }

   public static PropertyDirection func_177714_a(String var0) {
      return func_177712_a(var0, Predicates.alwaysTrue());
   }

   public static PropertyDirection func_177712_a(String var0, Predicate<EnumFacing> var1) {
      return func_177713_a(var0, Collections2.filter(Lists.newArrayList(EnumFacing.values()), var1));
   }

   public static PropertyDirection func_177713_a(String var0, Collection<EnumFacing> var1) {
      return new PropertyDirection(var0, var1);
   }
}
