package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class IntegerProperty extends Property<Integer> {
   private final ImmutableSet<Integer> values;
   private final int min;
   private final int max;

   protected IntegerProperty(String var1, int var2, int var3) {
      super(var1, Integer.class);
      if (var2 < 0) {
         throw new IllegalArgumentException("Min value of " + var1 + " must be 0 or greater");
      } else if (var3 <= var2) {
         throw new IllegalArgumentException("Max value of " + var1 + " must be greater than min (" + var2 + ")");
      } else {
         this.min = var2;
         this.max = var3;
         HashSet var4 = Sets.newHashSet();

         for(int var5 = var2; var5 <= var3; ++var5) {
            var4.add(var5);
         }

         this.values = ImmutableSet.copyOf(var4);
      }
   }

   public Collection<Integer> getPossibleValues() {
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
         Integer var2 = Integer.valueOf(var1);
         return var2 >= this.min && var2 <= this.max ? Optional.of(var2) : Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public String getName(Integer var1) {
      return var1.toString();
   }
}
