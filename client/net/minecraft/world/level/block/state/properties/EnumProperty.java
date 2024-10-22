package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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

   private EnumProperty(String var1, Class<T> var2, List<T> var3) {
      super(var1, var2);
      if (var3.isEmpty()) {
         throw new IllegalArgumentException("Trying to make empty EnumProperty '" + var1 + "'");
      } else {
         this.values = List.copyOf(var3);
         Enum[] var4 = (Enum[])var2.getEnumConstants();
         this.ordinalToIndex = new int[var4.length];

         for (Enum var8 : var4) {
            this.ordinalToIndex[var8.ordinal()] = var3.indexOf(var8);
         }

         Builder var9 = ImmutableMap.builder();

         for (Enum var11 : var3) {
            String var12 = ((StringRepresentable)var11).getSerializedName();
            var9.put(var12, var11);
         }

         this.names = var9.buildOrThrow();
      }
   }

   @Override
   public List<T> getPossibleValues() {
      return this.values;
   }

   @Override
   public Optional<T> getValue(String var1) {
      return Optional.ofNullable(this.names.get(var1));
   }

   public String getName(T var1) {
      return ((StringRepresentable)var1).getSerializedName();
   }

   public int getInternalIndex(T var1) {
      return this.ordinalToIndex[var1.ordinal()];
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof EnumProperty var2 && super.equals(var1)) {
            return this.values.equals(var2.values);
         }

         return false;
      }
   }

   @Override
   public int generateHashCode() {
      int var1 = super.generateHashCode();
      return 31 * var1 + this.values.hashCode();
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1) {
      return create(var0, var1, var0x -> true);
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, Predicate<T> var2) {
      return create(var0, var1, Arrays.stream((Enum[])var1.getEnumConstants()).filter(var2).collect(Collectors.toList()));
   }

   @SafeVarargs
   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, T... var2) {
      return create(var0, var1, List.of((T[])var2));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, List<T> var2) {
      return new EnumProperty<>(var0, var1, var2);
   }
}
