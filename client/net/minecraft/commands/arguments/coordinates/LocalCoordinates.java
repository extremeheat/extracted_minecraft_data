package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record LocalCoordinates(double left, double up, double forwards) implements Coordinates {
   public static final char PREFIX_LOCAL_COORDINATE = '^';

   public LocalCoordinates(double var1, double var3, double var5) {
      super();
      this.left = var1;
      this.up = var3;
      this.forwards = var5;
   }

   public Vec3 getPosition(CommandSourceStack var1) {
      Vec3 var2 = var1.getAnchor().apply(var1);
      return Vec3.applyLocalCoordinatesToRotation(var1.getRotation(), new Vec3(this.left, this.up, this.forwards)).add(var2.x, var2.y, var2.z);
   }

   public Vec2 getRotation(CommandSourceStack var1) {
      return Vec2.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalCoordinates parse(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      double var2 = readDouble(var0, var1);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         double var4 = readDouble(var0, var1);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            double var6 = readDouble(var0, var1);
            return new LocalCoordinates(var2, var4, var6);
         } else {
            var0.setCursor(var1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
         }
      } else {
         var0.setCursor(var1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
      }
   }

   private static double readDouble(StringReader var0, int var1) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(var0);
      } else if (var0.peek() != '^') {
         var0.setCursor(var1);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(var0);
      } else {
         var0.skip();
         return var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0;
      }
   }
}
