package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends AbstractProperty<Boolean> {
   private final ImmutableSet<Boolean> field_177717_a = ImmutableSet.of(true, false);

   protected BooleanProperty(String var1) {
      super(var1, Boolean.class);
   }

   public Collection<Boolean> func_177700_c() {
      return this.field_177717_a;
   }

   public static BooleanProperty func_177716_a(String var0) {
      return new BooleanProperty(var0);
   }

   public Optional<Boolean> func_185929_b(String var1) {
      return !"true".equals(var1) && !"false".equals(var1) ? Optional.empty() : Optional.of(Boolean.valueOf(var1));
   }

   public String func_177702_a(Boolean var1) {
      return var1.toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof BooleanProperty && super.equals(var1)) {
         BooleanProperty var2 = (BooleanProperty)var1;
         return this.field_177717_a.equals(var2.field_177717_a);
      } else {
         return false;
      }
   }

   public int func_206906_c() {
      return 31 * super.func_206906_c() + this.field_177717_a.hashCode();
   }
}
