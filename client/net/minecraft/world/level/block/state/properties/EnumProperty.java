package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public final class EnumProperty<T extends Enum<T> & StringRepresentable> extends Property<T> {
   private final List<T> values;
   private final Map<String, T> names;
   private final int[] ordinalToIndex;

   private EnumProperty(final String name, final Class<T> clazz, final List<T> values) {
      super(name, clazz);
      if (values.isEmpty()) {
         throw new IllegalArgumentException("Trying to make empty EnumProperty '" + name + "'");
      } else {
         this.values = List.copyOf(values);
         T[] allEnumValues = (T[])(clazz.getEnumConstants());
         this.ordinalToIndex = new int[allEnumValues.length];

         for(T value : allEnumValues) {
            this.ordinalToIndex[value.ordinal()] = values.indexOf(value);
         }

         ImmutableMap.Builder<String, T> names = ImmutableMap.builder();

         for(T value : values) {
            String key = ((StringRepresentable)value).getSerializedName();
            names.put(key, value);
         }

         this.names = names.buildOrThrow();
      }
   }

   public List<T> getPossibleValues() {
      return this.values;
   }

   public Optional<T> getValue(final String name) {
      return Optional.ofNullable((Enum)this.names.get(name));
   }

   public String getName(final T value) {
      return ((StringRepresentable)value).getSerializedName();
   }

   public int getInternalIndex(final T value) {
      return this.ordinalToIndex[value.ordinal()];
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof EnumProperty) {
            EnumProperty<?> that = (EnumProperty)o;
            if (super.equals(o)) {
               return this.values.equals(that.values);
            }
         }

         return false;
      }
   }

   public int generateHashCode() {
      int result = super.generateHashCode();
      result = 31 * result + this.values.hashCode();
      return result;
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(final String name, final Class<T> clazz) {
      return create(name, clazz, (Predicate)((t) -> true));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(final String name, final Class<T> clazz, final Predicate<T> filter) {
      return create(name, clazz, (List)Arrays.stream((Enum[])clazz.getEnumConstants()).filter(filter).collect(Collectors.toList()));
   }

   @SafeVarargs
   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(final String name, final Class<T> clazz, final T... values) {
      return create(name, clazz, List.of(values));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(final String name, final Class<T> clazz, final List<T> values) {
      return new EnumProperty<T>(name, clazz, values);
   }
}
