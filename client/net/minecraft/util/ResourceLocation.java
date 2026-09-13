package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   private final String field_110626_a;
   private final String field_110625_b;

   public ResourceLocation(String var1, String var2) {
      super();
      Validate.notNull(var2);
      if (var1 != null && var1.length() != 0) {
         this.field_110626_a = var1;
      } else {
         this.field_110626_a = "minecraft";
      }

      this.field_110625_b = var2;
   }

   public ResourceLocation(String var1) {
      super();
      String var2 = "minecraft";
      String var3 = var1;
      int var4 = var1.indexOf(58);
      if (var4 >= 0) {
         var3 = var1.substring(var4 + 1, var1.length());
         if (var4 > 1) {
            var2 = var1.substring(0, var4);
         }
      }

      this.field_110626_a = var2.toLowerCase();
      this.field_110625_b = var3;
   }

   public String func_110623_a() {
      return this.field_110625_b;
   }

   public String func_110624_b() {
      return this.field_110626_a;
   }

   @Override
   public String toString() {
      return this.field_110626_a + ":" + this.field_110625_b;
   }

   @Override
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

   @Override
   public int hashCode() {
      return 31 * this.field_110626_a.hashCode() + this.field_110625_b.hashCode();
   }
}
