package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.ints.IntImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public final class IntegerProperty extends Property<Integer> {
   private final IntImmutableList values;
   private final int min;
   private final int max;

   private IntegerProperty(String var1, int var2, int var3) {
      super(var1, Integer.class);
      if (var2 < 0) {
         throw new IllegalArgumentException("Min value of " + var1 + " must be 0 or greater");
      } else if (var3 <= var2) {
         throw new IllegalArgumentException("Max value of " + var1 + " must be greater than min (" + var2 + ")");
      } else {
         this.min = var2;
         this.max = var3;
         this.values = IntImmutableList.toList(IntStream.range(var2, var3 + 1));
      }
   }

   public List<Integer> getPossibleValues() {
      return this.values;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof IntegerProperty) {
            IntegerProperty var2 = (IntegerProperty)var1;
            if (super.equals(var1)) {
               return this.values.equals(var2.values);
            }
         }

         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }

   public static IntegerProperty create(String var0, int var1, int var2) {
      return new IntegerProperty(var0, var1, var2);
   }

   public Optional<Integer> getValue(String var1) {
      try {
         int var2 = Integer.parseInt(var1);
         return var2 >= this.min && var2 <= this.max ? Optional.of(var2) : Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public String getName(Integer var1) {
      return var1.toString();
   }

   public int getInternalIndex(Integer var1) {
      return var1 <= this.max ? var1 - this.min : -1;
   }

   // $FF: synthetic method
   public int getInternalIndex(final Comparable var1) {
      return this.getInternalIndex((Integer)var1);
   }

   // $FF: synthetic method
   public String getName(final Comparable var1) {
      return this.getName((Integer)var1);
   }
}
