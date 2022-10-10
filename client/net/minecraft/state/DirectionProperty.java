package net.minecraft.state;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.EnumFacing;

public class DirectionProperty extends EnumProperty<EnumFacing> {
   protected DirectionProperty(String var1, Collection<EnumFacing> var2) {
      super(var1, EnumFacing.class, var2);
   }

   public static DirectionProperty func_177712_a(String var0, Predicate<EnumFacing> var1) {
      return func_177713_a(var0, (Collection)Arrays.stream(EnumFacing.values()).filter(var1).collect(Collectors.toList()));
   }

   public static DirectionProperty func_196962_a(String var0, EnumFacing... var1) {
      return func_177713_a(var0, Lists.newArrayList(var1));
   }

   public static DirectionProperty func_177713_a(String var0, Collection<EnumFacing> var1) {
      return new DirectionProperty(var0, var1);
   }
}
