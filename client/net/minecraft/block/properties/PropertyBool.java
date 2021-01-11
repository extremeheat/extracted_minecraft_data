package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public class PropertyBool extends PropertyHelper<Boolean> {
   private final ImmutableSet<Boolean> field_177717_a = ImmutableSet.of(true, false);

   protected PropertyBool(String var1) {
      super(var1, Boolean.class);
   }

   public Collection<Boolean> func_177700_c() {
      return this.field_177717_a;
   }

   public static PropertyBool func_177716_a(String var0) {
      return new PropertyBool(var0);
   }

   public String func_177702_a(Boolean var1) {
      return var1.toString();
   }
}
