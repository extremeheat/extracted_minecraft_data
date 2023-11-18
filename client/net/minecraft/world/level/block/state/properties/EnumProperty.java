package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public class EnumProperty<T extends Enum<T> & StringRepresentable> extends Property<T> {
   private final ImmutableSet<T> values;
   private final Map<String, T> names = Maps.newHashMap();

   protected EnumProperty(String var1, Class<T> var2, Collection<T> var3) {
      super(var1, var2);
      this.values = ImmutableSet.copyOf(var3);

      for(Enum var5 : var3) {
         String var6 = ((StringRepresentable)var5).getSerializedName();
         if (this.names.containsKey(var6)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + var6 + "'");
         }

         this.names.put(var6, (T)var5);
      }
   }

   @Override
   public Collection<T> getPossibleValues() {
      return this.values;
   }

   @Override
   public Optional<T> getValue(String var1) {
      return Optional.ofNullable(this.names.get(var1));
   }

   public String getName(T var1) {
      return ((StringRepresentable)var1).getSerializedName();
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof EnumProperty var2 && super.equals(var1)) {
            return this.values.equals(var2.values) && this.names.equals(var2.names);
         }

         return false;
      }
   }

   @Override
   public int generateHashCode() {
      int var1 = super.generateHashCode();
      var1 = 31 * var1 + this.values.hashCode();
      return 31 * var1 + this.names.hashCode();
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1) {
      return create(var0, var1, var0x -> true);
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, Predicate<T> var2) {
      return create(var0, var1, Arrays.stream((Enum[])var1.getEnumConstants()).filter(var2).collect(Collectors.toList()));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, T... var2) {
      return create(var0, var1, Lists.newArrayList(var2));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, Collection<T> var2) {
      return new EnumProperty<>(var0, var1, var2);
   }
}
