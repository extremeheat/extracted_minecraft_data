package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends Property<Boolean> {
   private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

   protected BooleanProperty(String var1) {
      super(var1, Boolean.class);
   }

   @Override
   public Collection<Boolean> getPossibleValues() {
      return this.values;
   }

   public static BooleanProperty create(String var0) {
      return new BooleanProperty(var0);
   }

   @Override
   public Optional<Boolean> getValue(String var1) {
      return !"true".equals(var1) && !"false".equals(var1) ? Optional.empty() : Optional.of(Boolean.valueOf(var1));
   }

   public String getName(Boolean var1) {
      return var1.toString();
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof BooleanProperty var2 && super.equals(var1)) {
            return this.values.equals(var2.values);
         }

         return false;
      }
   }

   @Override
   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }
}
