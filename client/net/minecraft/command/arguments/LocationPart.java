package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TextComponentTranslation;

public class LocationPart {
   public static final SimpleCommandExceptionType field_197311_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.missing.double", new Object[0]));
   public static final SimpleCommandExceptionType field_197312_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.missing.int", new Object[0]));
   private final boolean field_197313_d;
   private final double field_197314_e;

   public LocationPart(boolean var1, double var2) {
      super();
      this.field_197313_d = var1;
      this.field_197314_e = var2;
   }

   public double func_197306_a(double var1) {
      return this.field_197313_d ? this.field_197314_e + var1 : this.field_197314_e;
   }

   public static LocationPart func_197308_a(StringReader var0, boolean var1) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '^') {
         throw Vec3Argument.field_200149_b.createWithContext(var0);
      } else if (!var0.canRead()) {
         throw field_197311_b.createWithContext(var0);
      } else {
         boolean var2 = func_197309_b(var0);
         int var3 = var0.getCursor();
         double var4 = var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0D;
         String var6 = var0.getString().substring(var3, var0.getCursor());
         if (var2 && var6.isEmpty()) {
            return new LocationPart(true, 0.0D);
         } else {
            if (!var6.contains(".") && !var2 && var1) {
               var4 += 0.5D;
            }

            return new LocationPart(var2, var4);
         }
      }
   }

   public static LocationPart func_197307_a(StringReader var0) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '^') {
         throw Vec3Argument.field_200149_b.createWithContext(var0);
      } else if (!var0.canRead()) {
         throw field_197312_c.createWithContext(var0);
      } else {
         boolean var1 = func_197309_b(var0);
         double var2;
         if (var0.canRead() && var0.peek() != ' ') {
            var2 = var1 ? var0.readDouble() : (double)var0.readInt();
         } else {
            var2 = 0.0D;
         }

         return new LocationPart(var1, var2);
      }
   }

   private static boolean func_197309_b(StringReader var0) {
      boolean var1;
      if (var0.peek() == '~') {
         var1 = true;
         var0.skip();
      } else {
         var1 = false;
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocationPart)) {
         return false;
      } else {
         LocationPart var2 = (LocationPart)var1;
         if (this.field_197313_d != var2.field_197313_d) {
            return false;
         } else {
            return Double.compare(var2.field_197314_e, this.field_197314_e) == 0;
         }
      }
   }

   public int hashCode() {
      int var1 = this.field_197313_d ? 1 : 0;
      long var2 = Double.doubleToLongBits(this.field_197314_e);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public boolean func_200386_a() {
      return this.field_197313_d;
   }
}
