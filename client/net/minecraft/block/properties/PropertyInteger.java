package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class PropertyInteger extends PropertyHelper<Integer> {
   private final ImmutableSet<Integer> field_177720_a;

   protected PropertyInteger(String var1, int var2, int var3) {
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
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         if (!super.equals(var1)) {
            return false;
         } else {
            PropertyInteger var2 = (PropertyInteger)var1;
            return this.field_177720_a.equals(var2.field_177720_a);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.field_177720_a.hashCode();
      return var1;
   }

   public static PropertyInteger func_177719_a(String var0, int var1, int var2) {
      return new PropertyInteger(var0, var1, var2);
   }

   public String func_177702_a(Integer var1) {
      return var1.toString();
   }
}
