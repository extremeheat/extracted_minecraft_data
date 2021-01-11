package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   protected final String field_110626_a;
   protected final String field_110625_b;

   protected ResourceLocation(int var1, String... var2) {
      super();
      this.field_110626_a = org.apache.commons.lang3.StringUtils.isEmpty(var2[0]) ? "minecraft" : var2[0].toLowerCase();
      this.field_110625_b = var2[1];
      Validate.notNull(this.field_110625_b);
   }

   public ResourceLocation(String var1) {
      this(0, func_177516_a(var1));
   }

   public ResourceLocation(String var1, String var2) {
      this(0, var1, var2);
   }

   protected static String[] func_177516_a(String var0) {
      String[] var1 = new String[]{null, var0};
      int var2 = var0.indexOf(58);
      if (var2 >= 0) {
         var1[1] = var0.substring(var2 + 1, var0.length());
         if (var2 > 1) {
            var1[0] = var0.substring(0, var2);
         }
      }

      return var1;
   }

   public String func_110623_a() {
      return this.field_110625_b;
   }

   public String func_110624_b() {
      return this.field_110626_a;
   }

   public String toString() {
      return this.field_110626_a + ':' + this.field_110625_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation var2 = (ResourceLocation)var1;
         return this.field_110626_a.equals(var2.field_110626_a) && this.field_110625_b.equals(var2.field_110625_b);
      }
   }

   public int hashCode() {
      return 31 * this.field_110626_a.hashCode() + this.field_110625_b.hashCode();
   }
}
