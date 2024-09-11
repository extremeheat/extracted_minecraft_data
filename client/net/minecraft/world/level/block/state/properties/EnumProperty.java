package net.minecraft.world.level.block.state.properties;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.StringRepresentable;

public class EnumProperty<T extends Enum<T> & StringRepresentable> extends Property<T> {
   private static final int UNABLE_TO_USE_ORDINALS = -1;
   private final List<T> values;
   private final Map<String, T> names = Maps.newHashMap();
   @VisibleForTesting
   protected int minOffset;
   @VisibleForTesting
   protected final int maxUsableOrdinal;

   protected EnumProperty(String var1, Class<T> var2, List<T> var3) {
      super(var1, var2);
      if (var3.isEmpty()) {
         throw new IllegalArgumentException("Trying to make empty EnumProperty '" + var1 + "'");
      } else {
         int[] var4 = new int[]{-1};
         if (IntStream.range(0, var3.size()).allMatch(var2x -> {
            int var3x = ((Enum)var3.get(var2x)).ordinal() - var2x;
            if (var4[0] == -1) {
               var4[0] = var3x;
            }

            return var3x == var4[0];
         })) {
            this.values = Collections.unmodifiableList(var3);
            this.maxUsableOrdinal = ((Enum)var3.getLast()).ordinal();
            this.minOffset = var4[0];
         } else {
            this.values = new ReferenceArrayList(var3);
            this.maxUsableOrdinal = -1;
            this.minOffset = -1;
         }

         for (Enum var6 : var3) {
            String var7 = ((StringRepresentable)var6).getSerializedName();
            if (this.names.containsKey(var7)) {
               throw new IllegalArgumentException("Multiple values have the same name '" + var7 + "'");
            }

            this.names.put(var7, (T)var6);
         }
      }
   }

   @Override
   public List<T> getPossibleValues() {
      return this.maxUsableOrdinal == -1 ? Collections.unmodifiableList(this.values) : this.values;
   }

   @Override
   public Optional<T> getValue(String var1) {
      return Optional.ofNullable(this.names.get(var1));
   }

   public String getName(T var1) {
      return ((StringRepresentable)var1).getSerializedName();
   }

   public int getInternalIndex(T var1) {
      int var2 = var1.ordinal();
      return var2 <= this.maxUsableOrdinal ? var2 - this.minOffset : this.values.indexOf(var1);
   }

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

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String var0, Class<T> var1, List<T> var2) {
      return new EnumProperty<>(var0, var1, var2);
   }
}
