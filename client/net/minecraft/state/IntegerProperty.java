package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class IntegerProperty extends AbstractProperty<Integer> {
   private final ImmutableSet<Integer> field_177720_a;

   protected IntegerProperty(String var1, int var2, int var3) {
      super(var1, Integer.class);
      if (var2 < 0) {
         throw new IllegalArgumentException("Min value of " + var1 + " must be 0 or greater");
      } else if (var3 <= var2) {
         throw new IllegalArgumentException("Max value of " + var1 + " must be greater than min (" + var2 + ")");
      } else {
         HashSet var4 = Sets.newHashSet();

         for(int var5 = var2; var5 <= var3; ++var5) {
            var4.add(var5);
         }

         this.field_177720_a = ImmutableSet.copyOf(var4);
      }
   }

   public Collection<Integer> func_177700_c() {
      return this.field_177720_a;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof IntegerProperty && super.equals(var1)) {
         IntegerProperty var2 = (IntegerProperty)var1;
         return this.field_177720_a.equals(var2.field_177720_a);
      } else {
         return false;
      }
   }

   public int func_206906_c() {
      return 31 * super.func_206906_c() + this.field_177720_a.hashCode();
   }

   public static IntegerProperty func_177719_a(String var0, int var1, int var2) {
      return new IntegerProperty(var0, var1, var2);
   }

   public Optional<Integer> func_185929_b(String var1) {
      try {
         Integer var2 = Integer.valueOf(var1);
         return this.field_177720_a.contains(var2) ? Optional.of(var2) : Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public String func_177702_a(Integer var1) {
      return var1.toString();
   }
}
