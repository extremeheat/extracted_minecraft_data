package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldCoordinate {
   public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.double", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.int", new Object[0]));
   private final boolean relative;
   private final double value;

   public WorldCoordinate(boolean var1, double var2) {
      super();
      this.relative = var1;
      this.value = var2;
   }

   public double get(double var1) {
      return this.relative ? this.value + var1 : this.value;
   }

   public static WorldCoordinate parseDouble(StringReader var0, boolean var1) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '^') {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(var0);
      } else if (!var0.canRead()) {
         throw ERROR_EXPECTED_DOUBLE.createWithContext(var0);
      } else {
         boolean var2 = isRelative(var0);
         int var3 = var0.getCursor();
         double var4 = var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0D;
         String var6 = var0.getString().substring(var3, var0.getCursor());
         if (var2 && var6.isEmpty()) {
            return new WorldCoordinate(true, 0.0D);
         } else {
            if (!var6.contains(".") && !var2 && var1) {
               var4 += 0.5D;
            }

            return new WorldCoordinate(var2, var4);
         }
      }
   }

   public static WorldCoordinate parseInt(StringReader var0) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '^') {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(var0);
      } else if (!var0.canRead()) {
         throw ERROR_EXPECTED_INT.createWithContext(var0);
      } else {
         boolean var1 = isRelative(var0);
         double var2;
         if (var0.canRead() && var0.peek() != ' ') {
            var2 = var1 ? var0.readDouble() : (double)var0.readInt();
         } else {
            var2 = 0.0D;
         }

         return new WorldCoordinate(var1, var2);
      }
   }

   private static boolean isRelative(StringReader var0) {
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
      } else if (!(var1 instanceof WorldCoordinate)) {
         return false;
      } else {
         WorldCoordinate var2 = (WorldCoordinate)var1;
         if (this.relative != var2.relative) {
            return false;
         } else {
            return Double.compare(var2.value, this.value) == 0;
         }
      }
   }

   public int hashCode() {
      int var1 = this.relative ? 1 : 0;
      long var2 = Double.doubleToLongBits(this.value);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public boolean isRelative() {
      return this.relative;
   }
}
