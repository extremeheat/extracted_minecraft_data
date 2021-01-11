package net.minecraft.util;

import java.util.Random;

public class EnchantmentNameParts {
   private static final EnchantmentNameParts field_148338_a = new EnchantmentNameParts();
   private Random field_148336_b = new Random();
   private String[] field_148337_c = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale ".split(" ");

   private EnchantmentNameParts() {
      super();
   }

   public static EnchantmentNameParts func_178176_a() {
      return field_148338_a;
   }

   public String func_148334_a() {
      int var1 = this.field_148336_b.nextInt(2) + 3;
      String var2 = "";

      for(int var3 = 0; var3 < var1; ++var3) {
         if (var3 > 0) {
            var2 = var2 + " ";
         }

         var2 = var2 + this.field_148337_c[this.field_148336_b.nextInt(this.field_148337_c.length)];
      }

      return var2;
   }

   public void func_148335_a(long var1) {
      this.field_148336_b.setSeed(var1);
   }
}
