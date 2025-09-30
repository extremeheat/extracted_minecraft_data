package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;

public record WorldCoordinate(boolean relative, double value) {
   private static final char PREFIX_RELATIVE = '~';
   public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType(Component.translatable("argument.pos.missing.double"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType(Component.translatable("argument.pos.missing.int"));

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
         double var4 = var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0;
         String var6 = var0.getString().substring(var3, var0.getCursor());
         if (var2 && var6.isEmpty()) {
            return new WorldCoordinate(true, 0.0);
         } else {
            if (!var6.contains(".") && !var2 && var1) {
               var4 += 0.5;
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
            var2 = 0.0;
         }

         return new WorldCoordinate(var1, var2);
      }
   }

   public static boolean isRelative(StringReader var0) {
      boolean var1;
      if (var0.peek() == '~') {
         var1 = true;
         var0.skip();
      } else {
         var1 = false;
      }

      return var1;
   }

   public boolean isRelative() {
      return this.relative;
   }
}
